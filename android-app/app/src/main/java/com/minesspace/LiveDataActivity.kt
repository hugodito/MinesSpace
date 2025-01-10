package com.minesspace

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LiveDataActivity : ComponentActivity() {
    private val apiUrl = "http://10.0.2.2:3000/api/data" // URL API pour récupérer les données
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val launchId = intent.getIntExtra("launch_id", -1)

        setContent {
            if (launchId != -1) {
                LiveDataScreen(fetchLiveData = { fetchLiveData(launchId) }, launchId = launchId)
            }
        }
    }

    private suspend fun fetchLiveData(launchId: Int): List<LiveDataItem> {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$apiUrl?launch_id=$launchId") // Ajout du launch_id dans l'URL pour filtrer les données
                    .build()
                val response = client.newCall(request).execute()
                val responseData = response.peekBody(Long.MAX_VALUE).string()

                if (response.isSuccessful) {
                    val dataList = parseJson(responseData, launchId)
                    // Trier les données par date, de la plus récente à la plus vieille
                    dataList.sortedByDescending { it.timestamp.toLong() }                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    // Filtrer les données en fonction du launch_id
    private fun parseJson(responseData: String, launchId: Int): List<LiveDataItem> {
        val jsonArray = JSONArray(responseData)
        val dataList = mutableListOf<LiveDataItem>()

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val itemLaunchId = item.optInt("launch_id")

            // Ne récupérer que les données correspondant au launch_id
            if (itemLaunchId == launchId) {
                val liveDataItem = LiveDataItem(
                    timestamp = item.optString("timestamp"),
                    temperature = item.optString("temperature"),
                    pressure = item.optString("pression"),
                    altitude = item.optString("altitude"),
                    acceleration = item.optString("acceleration"),
                    vitesse = item.optString("vitesse"),
                    launchId = itemLaunchId
                )
                dataList.add(liveDataItem)
            }
        }

        return dataList
    }
}


data class LiveDataItem(
    val timestamp: String,
    val temperature: String,
    val pressure: String,
    val altitude: String,
    val acceleration: String,
    val vitesse: String,
    val launchId: Int
)

fun formatTimestampToDateTime(timestamp: String): String {
    return try {
        val milliseconds = timestamp.toLong()
        val date = Date(milliseconds)
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        format.format(date)
    } catch (e: Exception) {
        "Invalid timestamp"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveDataScreen(fetchLiveData: suspend () -> List<LiveDataItem>, launchId: Int) {
    var liveData by remember { mutableStateOf<List<LiveDataItem>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Mise à jour en temps réel avec un intervalle
    LaunchedEffect(Unit) {
        while (true) {
            try {
                liveData = fetchLiveData()
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Erreur lors de la récupération des données : ${e.message}"
            }
            delay(5000) // Actualisation toutes les 5 secondes
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Données du lancer $launchId") }, // Affichage du launch_id dans le titre
                navigationIcon = {
                    IconButton(onClick = { (context as? LiveDataActivity)?.finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (errorMessage != null) {
                Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)
            } else if (liveData.isEmpty()) {
                Text(text = "Aucune donnée disponible pour le moment.")
            } else {
                // Afficher les données sous forme de tableau
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Légendes des colonnes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment=Alignment.CenterVertically
                    ) {
                        Text("Date et Heure", modifier = Modifier.weight(1.5f), textAlign = TextAlign.Center)
                        Text("Température (°C)", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("Pression (hPa)", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("Altitude (m)", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("Accélération (m/s²)", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("Vitesse (m/s)", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    }

                    // Séparateur
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Lignes du tableau
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(liveData) { dataItem ->
                            LiveDataRow(dataItem)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiveDataRow(item: LiveDataItem) {
    val formattedDate = formatTimestampToDateTime(item.timestamp)

    // Créer un tableau avec une ligne pour chaque donnée
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary) // Bordure de la ligne
            .background(MaterialTheme.colorScheme.surface), // Fond de la ligne
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(formatTimestampToDateTime(item.timestamp), modifier = Modifier.weight(1.5f), textAlign = TextAlign.Center)
        Text(item.temperature, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(item.pressure, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(item.altitude, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(item.acceleration, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(item.vitesse, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
    }
}

@Preview(showBackground = true)
@Composable
fun LiveDataPreview() {
    val sampleData = listOf(
        LiveDataItem("1672502400000", "23", "1013", "150", "1.02", "15", 1),
        LiveDataItem("1672502410000", "22.5", "1012", "160", "1.05", "16", 2)
    )
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(sampleData) { LiveDataRow(it) }
    }
}
