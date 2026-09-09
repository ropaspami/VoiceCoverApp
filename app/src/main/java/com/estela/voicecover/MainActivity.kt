package com.estela.voicecover

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// ============ CONFIGURA AQUI TU BACKEND RUNPOD ============
const val BACKEND_URL = "https://TU_RUNPOD_ID-8000.proxy.runpod.net/" // <-- CAMBIA ESTO por tu URL de RunPod / Render / HF

// ============ API MODELS ============
data class CoverRequest(val youtubeUrl: String? = null, val model_id: String = "estela_v1", val pitch: Int = 0)
data class CoverResponse(val job_id: String, val status_url: String)
data class StatusResponse(val status: String, val progress: Int, val file: String? = null, val error: String? = null)

interface CoverApi {
    @POST("cover")
    suspend fun createCover(@Body req: CoverRequest): CoverResponse

    @GET("status/{job_id}")
    suspend fun getStatus(@Path("job_id") jobId: String): StatusResponse
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { VoiceCoverScreen() } }
    }
}

@Composable
fun VoiceCoverScreen() {
    var url by remember { mutableStateOf("https://www.youtube.com/watch?v=...") }
    var progress by remember { mutableStateOf(0) }
    var statusText by remember { mutableStateOf("Listo") }
    var isGenerating by remember { mutableStateOf(false) }
    var jobId by remember { mutableStateOf<String?>(null) }
    var downloadFile by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Retrofit client
    val api = remember {
        Retrofit.Builder()
            .baseUrl(BACKEND_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CoverApi::class.java)
    }

    // Polling loop - actualiza la barra 68% dentro en tiempo real
    LaunchedEffect(jobId, isGenerating) {
        if (jobId != null && isGenerating) {
            while (isGenerating) {
                try {
                    val status = api.getStatus(jobId!!)
                    progress = status.progress
                    statusText = when (status.status) {
                        "queued" -> "En cola..."
                        "downloading" -> "Descargando audio..."
                        "separating" -> "Separando voz e instrumental..."
                        "converting" -> "Convirtiendo a tu voz... ${status.progress}% dentro"
                        "mixing" -> "Mezclando instrumental + tu voz..."
                        "done" -> "¡Listo!"
                        "error" -> "Error: ${status.error}"
                        else -> status.status
                    }
                    if (status.status == "done") {
                        downloadFile = status.file
                        isGenerating = false
                        progress = 100
                        break
                    }
                    if (status.status == "error") {
                        isGenerating = false
                        break
                    }
                } catch (e: Exception) {
                    statusText = "Conectando a backend... ${e.message}"
                }
                delay(1000) // consulta cada 1 segundo
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("VoiceCover - Tu voz en cualquier canción", style = MaterialTheme.typography.headlineSmall)
        Text("Backend: $BACKEND_URL", style = MaterialTheme.typography.labelSmall, color = Color.Gray)

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("1. Grabar tu voz (entrena tu modelo)", style = MaterialTheme.typography.titleMedium)
                Text("Sube 5 min de tu voz a /train en el backend", style = MaterialTheme.typography.bodySmall)
                Button(onClick = {}) { Text("Grabar / Subir voz") }
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("2. Enlace de YouTube", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("Pega URL") }, modifier = Modifier.fillMaxWidth())

                // BARRA CON PORCENTAJE DENTRO - AHORA EN TIEMPO REAL
                if (isGenerating || progress > 0) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(statusText, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        Box(
                            Modifier.fillMaxWidth().height(28.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFF2A2A2A))
                        ) {
                            Box(
                                Modifier.fillMaxWidth(progress / 100f).fillMaxHeight().clip(RoundedCornerShape(14.dp)).background(Color(0xFF00D9A5))
                            )
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("$progress%", color = Color.White, style = MaterialTheme.typography.labelLarge)
                            }
                        }
                        Text("Job: $jobId • ${progress}%", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }

                Button(
                    onClick = {
                        scope.launch {
                            try {
                                isGenerating = true
                                progress = 5
                                statusText = "Creando job..."
                                val resp = api.createCover(CoverRequest(youtubeUrl = url))
                                jobId = resp.job_id
                            } catch (e: Exception) {
                                statusText = "Error backend: ${e.message} - Verifica BACKEND_URL"
                                isGenerating = false
                            }
                        }
                    },
                    enabled = !isGenerating,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isGenerating) "Generando... $progress% dentro" else "Crear cover con mi voz")
                }
            }
        }

        if (downloadFile != null) {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2A25))) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("✅ Resultado generado", style = MaterialTheme.typography.titleMedium)
                    Text("Archivo: $downloadFile", style = MaterialTheme.typography.bodySmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            // Aquí harías DownloadManager para BACKEND_URL + "files/" + downloadFile
                        }) { Text("Descargar MP3") }
                        OutlinedButton(onClick = {}) { Text("Compartir") }
                    }
                }
            }
        }
    }
}
