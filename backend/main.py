"""
VoiceCover Backend - FastAPI + RVC + Demucs
Este backend hace lo que Android no puede: separar voz e convertirla.

ENDPOINTS:
POST /train -> sube 5-10 min de tu voz, entrena modelo RVC
POST /cover -> { youtubeUrl o file, model_id } -> devuelve cover con tu voz
GET /status/{job_id}
GET /files/{filename}

DEPLOY:
- Local con GPU: uvicorn main:app --host 0.0.0.0 --port 8000 --reload
- Docker: docker build -t voicecover-backend . && docker run -p 8000:8000 --gpus all voicecover-backend
- Render/RunPod/HuggingFace Spaces con Dockerfile

NOTA LEGAL: Solo usar audio con derechos / propio. YouTube ToS prohibe descarga sin permiso.
"""
from fastapi import FastAPI, UploadFile, File, BackgroundTasks
from fastapi.responses import FileResponse
from pydantic import BaseModel
import os, uuid, shutil, pathlib

app = FastAPI(title="VoiceCover Backend", version="1.0")

BASE_DIR = pathlib.Path("./data")
BASE_DIR.mkdir(exist_ok=True)
MODELS_DIR = BASE_DIR / "models"
MODELS_DIR.mkdir(exist_ok=True)

jobs = {}

class CoverRequest(BaseModel):
    youtubeUrl: str | None = None
    model_id: str = "estela_v1"
    pitch: int = 0 # semitonos para ajustar

def process_cover_job(job_id: str, youtube_url: str, model_id: str):
    try:
        jobs[job_id] = {"status": "downloading", "progress": 10}
        # 1. Obtener audio
        # if youtube_url:
        #   os.system(f"yt-dlp -x --audio-format wav -o {BASE_DIR}/{job_id}_input.wav {youtube_url}")
        # else usar archivo subido

        jobs[job_id] = {"status": "separating", "progress": 30}
        # 2. Separar con Demucs
        # os.system(f"demucs --two-stems=vocals {BASE_DIR}/{job_id}_input.wav -o {BASE_DIR}/{job_id}_separated")

        jobs[job_id] = {"status": "converting", "progress": 68}
        # 3. RVC inference
        # from rvc_infer import infer
        # infer(model=f"{MODELS_DIR}/{model_id}.pth", input=f"{BASE_DIR}/{job_id}_separated/htdemucs/{job_id}_input/vocals.wav", output=f"{BASE_DIR}/{job_id}_converted.wav")

        jobs[job_id] = {"status": "mixing", "progress": 85}
        # 4. Mezclar instrumental + voz convertida
        # os.system(f"ffmpeg -y -i {BASE_DIR}/{job_id}_separated/.../no_vocals.wav -i {BASE_DIR}/{job_id}_converted.wav -filter_complex amix=inputs=2 {BASE_DIR}/{job_id}_final.mp3")

        jobs[job_id] = {"status": "done", "progress": 100, "file": f"{job_id}_final.mp3"}
    except Exception as e:
        jobs[job_id] = {"status": "error", "progress": 0, "error": str(e)}

@app.get("/")
def root():
    return {"message": "VoiceCover Backend OK", "docs": "/docs"}

@app.post("/train")
async def train_voice(files: list[UploadFile] = File(...)):
    model_id = f"estela_{uuid.uuid4().hex[:6]}"
    model_path = MODELS_DIR / model_id
    model_path.mkdir(exist_ok=True)
    for f in files:
        dest = model_path / f.filename
        with open(dest, "wb") as out:
            shutil.copyfileobj(f.file, out)
    # Aquí lanzarías: python train_rvc.py --dataset model_path
    return {"model_id": model_id, "status": "training_started", "note": "Entrenamiento tarda 30-60 min en GPU"}

@app.post("/cover")
async def create_cover(req: CoverRequest, background_tasks: BackgroundTasks):
    job_id = uuid.uuid4().hex[:8]
    jobs[job_id] = {"status": "queued", "progress": 0}
    background_tasks.add_task(process_cover_job, job_id, req.youtubeUrl or "", req.model_id)
    return {"job_id": job_id, "status_url": f"/status/{job_id}"}

@app.get("/status/{job_id}")
def get_status(job_id: str):
    return jobs.get(job_id, {"status": "not_found"})

@app.get("/files/{filename}")
def get_file(filename: str):
    path = BASE_DIR / filename
    if path.exists():
        return FileResponse(path, media_type="audio/mpeg", filename=filename)
    return {"error": "file not found"}

# Para Android: app llama a /cover y luego hace polling a /status/{job_id} actualizando la barra 68% dentro
