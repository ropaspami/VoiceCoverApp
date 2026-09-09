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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                VoiceCoverScreen()
            }
        }
    }
}

@Composable
fun VoiceCoverScreen() {
    var url by remember { mutableStateOf("") }
    var progress by remember { mutableStateOf(0) }
    var isGenerating by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("VoiceCover - Tu voz en cualquier canción", style = MaterialTheme.typography.headlineSmall)

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("1. Grabar tu voz", style = MaterialTheme.typography.titleMedium)
                Text("Graba 30 seg para clonar tu voz", style = MaterialTheme.typography.bodySmall)
                Button(onClick = {}) { Text("Grabar") }
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("2. Enlace de YouTube", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("https://youtube.com/watch?v=...") }, modifier = Modifier.fillMaxWidth())
                
                if (isGenerating) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Procesando voz...", style = MaterialTheme.typography.labelMedium)
                        Box(
                            Modifier.fillMaxWidth().height(24.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF2A2A2A))
                        ) {
                            Box(
                                Modifier.fillMaxWidth(progress/100f).fillMaxHeight().clip(RoundedCornerShape(12.dp)).background(Color(0xFF00D9A5))
                            )
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("$progress%", color = Color.White, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                        Text("Estimando ${100-progress}s restantes", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Button(onClick = {
                    isGenerating = true
                    showResult = false
                    // Simulación de progreso - aquí llamarías a tu backend
                    progress = 68
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (isGenerating) "Generando..." else "Crear cover con mi voz")
                }
            }
        }

        if (showResult || isGenerating) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Resultado generado", style = MaterialTheme.typography.titleMedium)
                    Text("Tu cover está listo • 02:45", style = MaterialTheme.typography.bodySmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { showResult = true; isGenerating = false; progress = 100 }) { Text("Descargar") }
                        OutlinedButton(onClick = {}) { Text("Compartir") }
                    }
                }
            }
        }
    }
}
