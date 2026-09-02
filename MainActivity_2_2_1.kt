package com.estela.voicecover

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var url by remember { mutableStateOf("") }
                var generando by remember { mutableStateOf(false) }
                Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("VoiceCover - Tu voz", style = MaterialTheme.typography.headlineSmall)
                    OutlinedTextField(url, { url = it }, label = { Text("URL YouTube o MP3") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = { generando = true }) { Text(if(generando) "Generando..." else "Crear cover con mi voz") }
                    Button(onClick = { /* DownloadManager */ }) { Text("Descargar resultado") }
                }
            }
        }
    }
}
