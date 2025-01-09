package com.minesspace

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
class LiveDataActivity : ComponentActivity() {
    private val apiUrl = "http://10.0.2.2:3000/api/data" // Émulateur Android pointe vers localhost avec 10.0.2.2
    private val liveStatusApiUrl = "http://10.0.2.2:3000/api/status" // API pour vérifier si live est actif
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.requestFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // Cacher la barre inférieure (NavigationBar)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        setContent {
            LiveDataScreen(
                fetchLiveDataStatus = ::fetchLiveDataStatus,
                fetchLiveData = ::fetchLiveData
            )
        }
    }

    private suspend fun fetchLiveDataStatus(): Boolean {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(liveStatusApiUrl).build()
            val response: Response = client.newCall(request).execute()
            val responseData = response.peekBody(Long.MAX_VALUE).string()
            responseData.trim() == "true"
        }
    }

    private suspend fun fetchLiveData(): String {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(apiUrl).build()
            val response: Response = client.newCall(request).execute()
            val responseData = response.peekBody(Long.MAX_VALUE).string()

            if (response.isSuccessful && responseData.isNotEmpty()) {
                parseJson(responseData)
            } else {
                "Erreur lors de la récupération des données."
            }
        }
    }

    private fun parseJson(responseData: String): String {
        val jsonArray = JSONArray(responseData)
        val stringBuilder = StringBuilder()

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val timestamp = item.getString("timestamp")
            val temperature = item.getString("temperature")
            val pressure = item.getString("pression")
            val altitude = item.getString("altitude")

            stringBuilder.append("Timestamp: $timestamp\n")
            stringBuilder.append("Température: $temperature °C\n")
            stringBuilder.append("Pression: $pressure hPa\n")
            stringBuilder.append("Altitude: $altitude m\n\n")
        }

        return stringBuilder.toString()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveDataScreen(
    fetchLiveDataStatus: suspend () -> Boolean,  // Fonction pour vérifier le statut du live
    fetchLiveData: suspend () -> String          // Fonction pour récupérer les données
) {
    // Déclarez un état pour la donnée en direct et le statut
    var liveData by remember { mutableStateOf("Données en attente...") }
    var status by remember { mutableStateOf("Vérification du statut...") }

    // Récupérer les données en direct
    LaunchedEffect(Unit) {
        try {
            val isLive = fetchLiveDataStatus()
            if (isLive) {
                status = "Live en cours..."
                liveData = fetchLiveData()
            } else {
                status = "Désolé, pas de lancer en direct pour le moment."
                liveData = ""
            }
        } catch (e: Exception) {
            status = "Erreur : ${e.message}"
            liveData = ""
        }
    }

    // Scaffold avec le TopAppBar et la mise en page de Column
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Data") },
                navigationIcon = {
                    IconButton(onClick = { /* Action pour revenir en arrière */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->  // innerPadding est automatiquement fourni par le Scaffold
        // Appliquez les padding à votre contenu ici, directement dans Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)  // Utilisez innerPadding pour prendre en compte les marges du Scaffold
                .padding(16.dp),  // Ajoutez du padding autour du contenu
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = status, style = MaterialTheme.typography.bodyLarge) // Utilisation de bodyLarge
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = liveData, style = MaterialTheme.typography.bodyLarge) // Utilisation de bodyLarge
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LiveDataScreen(
        fetchLiveDataStatus = { true },  // Retourne true par défaut dans le preview
        fetchLiveData = { "Données en attente..." } // Retourne une chaîne de test
    )
}
