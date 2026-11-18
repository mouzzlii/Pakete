package com.example.pakete // Passe das Paket an dein Projekt an

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
// Hier werden später die ML Kit Scanner Komponenten eingebunden

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                CarrierGatewayScreen(
                    onScanResult = { trackingCode ->
                        handleTrackingCode(trackingCode)
                    }
                )
            }
        }
    }

    private fun handleTrackingCode(code: String) {
        val url = when {
            code.startsWith("GLS") || code.length > 12 -> "https://www.gls-pakete.de/en/tracking?match=$code"
            code.startsWith("DPD") -> "https://tracking.dpd.de/status/de_DE/parcels/$code"
            else -> "https://www.hermesworld.com/at/en/parcel-tracking/?shipmentId=$code"
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}

@Composable
fun CarrierGatewayScreen(onScanResult: (String) -> Unit) {
    var manualCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Multi-Carrier Gateway", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = manualCode,
            onValueChange = { manualCode = it },
            label = { Text("Tracking-Code eingeben / scannen") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { if (manualCode.isNotBlank()) onScanResult(manualCode) },
            modifier = Modifier.fillMaxWidth()
        )
        {
            Text("Paket verfolgen")
        }
    }
}
