# VoiceCover Backend

## Como correr local con GPU
```bash
cd backend
pip install -r requirements.txt
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

## API
- POST /train -> sube wavs de tu voz
- POST /cover -> {"youtubeUrl": "https://...", "model_id": "estela_v1"}
- GET /status/{job_id} -> devuelve {progress: 68, status: "converting"} -> esto alimenta tu barra 68% dentro
- GET /files/{final.mp3}

## Deploy gratis
- **RunPod**: Usa template pytorch + este Dockerfile, con GPU RTX 4090 (0.5$/h)
- **HuggingFace Spaces**: Crea Space Docker, sube este backend
- **Render**: Conecta repo, Dockerfile detectado automático

## Conexión con Android
En MainActivity.kt cambia:
```kotlin
.baseUrl("https://TU_BACKEND_URL/")
```
por la URL que te de RunPod/Render.

La barra de progreso verde con 68% dentro se actualiza con el campo progress del /status.
