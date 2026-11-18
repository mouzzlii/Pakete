package com.example.multicarrierscan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import java.text.SimpleDateFormat
import java.util.*

data class ScanItem(
    val id: String = UUID.randomUUID().toString(),
    val carrier: String,
    val trackingCode: String,
    val timestamp: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MultiCarrierMainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiCarrierMainScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scanHistory = remember { mutableStateListOf<ScanItem>() }

    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            com.google.mlkit.vision.barcode.common.Barcode.FORMAT_CODE_128,
            com.google.mlkit.vision.barcode.common.Barcode.FORMAT_DATA_MATRIX,
            com.google.mlkit.vision.barcode.common.Barcode.FORMAT_QR_CODE
        )
        .enableAutoZoom()
        .build()

    val scanner = GmsBarcodeScanning.getClient(context, options)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Multi-Carrier Gateway") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scanner.startScan()
                        .addOnSuccessListener { barcode ->
                            val rawValue = barcode.rawValue
                            if (!rawValue.isNullOrBlank()) {
                                val carrier = analyzeCarrier(rawValue)
                                val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                                
                                scanHistory.add(0, ScanItem(carrier = carrier, trackingCode = rawValue, timestamp = currentTime))
                                openOfficialCarrierApp(context, carrier, rawValue)
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Fehler: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Paket scannen")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Heutige Scans (${scanHistory.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (scanHistory.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Noch keine Pakete gescannt.\nTippe auf '+' um zu starten.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(scanHistory) { item ->
                        Card(
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = item.carrier,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = item.timestamp,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Code: ${item.trackingCode}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun analyzeCarrier(code: String): String {
    return when {
        code.length == 14 && code.all { it.isDigit() } -> "GLS"
        code.startsWith("0150") || (code.length >= 12 && code.all { it.isDigit() }) -> "DPD"
        else -> "Hermes"
    }
}

private fun openOfficialCarrierApp(context: android.content.Context, carrier: String, trackingCode: String) {
    val packageName = when (carrier) {
        "GLS" -> "de.gls_germany.glsone"
        "DPD" -> "com.dpd.consumer"
        else -> "de.hermes.papaket"
    }

    val intent = context.packageManager.getLaunchIntentForPackage(packageName)
    if (intent != null) {
        context.startActivity(intent)
        Toast.makeText(context, "$carrier App geöffnet", Toast.LENGTH_SHORT).show()
    } else {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$carrier+tracking+$trackingCode"))
            context.startActivity(browserIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Konnte App/Browser nicht öffnen", Toast.LENGTH_SHORT).show()
        }
    }
}
      
