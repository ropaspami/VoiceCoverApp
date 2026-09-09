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
        setContent { MaterialTheme { VoiceCoverScreen() } }
    }
}
@Composable
fun VoiceCoverScreen() {
    var url by remember { mutableStateOf("") }
    var progress by remember { mutableStateOf(68) }
    var isGenerating by remember { mutableStateOf(true) }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("VoiceCover - Tu voz en cualquier canción", style = MaterialTheme.typography.headlineSmall)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("1. Grabar tu voz")
                Button(onClick = {}) { Text("Grabar") }
            }
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("2. Enlace de YouTube")
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("https://youtube.com/watch?v=...") }, modifier = Modifier.fillMaxWidth())
                if (isGenerating) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Procesando voz... 68% dentro de la barra", style = MaterialTheme.typography.labelMedium)
                        Box(Modifier.fillMaxWidth().height(24.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF2A2A2A))) {
                            Box(Modifier.fillMaxWidth(progress/100f).fillMaxHeight().clip(RoundedCornerShape(12.dp)).background(Color(0xFF00D9A5)))
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("$progress%", color = Color.White) }
                        }
                    }
                }
                Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("Crear cover con mi voz") }
            }
        }
    }
}
