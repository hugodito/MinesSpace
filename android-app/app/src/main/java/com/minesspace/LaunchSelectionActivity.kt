package com.minesspace

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Activité pour sélectionner un lancer
class LaunchSelectionActivity : ComponentActivity() {
    private val apiUrl = "http://10.0.2.2:3000/api/data" // URL de l'API pour récupérer les données
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Définit l'interface utilisateur de l'activité
        setContent {
            LaunchSelectionScreen(fetchLaunchData = ::fetchLaunchData, navigateToLiveDataActivity = ::navigateToLiveDataActivity)
        }
    }

    // Récupère les données des lancers depuis l'API
    private suspend fun fetchLaunchData(): List<LaunchDataItem> {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(apiUrl).build()
                val response = client.newCall(request).execute()
                val responseData = response.peekBody(Long.MAX_VALUE).string()

                // Si la requête est réussie, parse les données JSON
                if (response.isSuccessful) {
                    parseLaunchJson(responseData)
                } else {
                    emptyList() // Retourne une liste vide en cas d'erreur
                }
            } catch (e: Exception) {
                emptyList() // En cas d'exception, retourne une liste vide
            }
        }
    }

    // Parse les données JSON et les transforme en objets LaunchDataItem
    private fun parseLaunchJson(responseData: String): List<LaunchDataItem> {
        val jsonArray = JSONArray(responseData)
        val launchDataList = mutableListOf<LaunchDataItem>()

        val launchDataMap = mutableMapOf<Int, MutableList<LaunchDataItem>>()

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val launchId = item.optInt("launch_id")
            val timestamp = item.optString("timestamp")

            if (launchDataMap[launchId] == null) {
                launchDataMap[launchId] = mutableListOf()
            }
            launchDataMap[launchId]?.add(LaunchDataItem(launchId, timestamp))
        }

        // Trie les données et retourne la première entrée pour chaque launchId
        for (launchId in launchDataMap.keys) {
            val sortedData = launchDataMap[launchId]?.sortedBy { it.timestamp.toLong() }
            if (sortedData != null && sortedData.isNotEmpty()) {
                launchDataList.add(sortedData.first()) // Ajoute la première donnée triée
            }
        }

        // Retourne la liste triée des lancers
        return launchDataList.sortedByDescending { it.launchId }
    }

    // Navigue vers l'écran des données en direct d'un lancer
    private fun navigateToLiveDataActivity(launchId: Int) {
        val intent = Intent(this, LiveDataActivity::class.java)
        intent.putExtra("launch_id", launchId)
        startActivity(intent)
    }
}

// Formate un timestamp en une date lisible
fun formatTimestampToDate(timestamp: String): String {
    return try {
        val milliseconds = timestamp.toLong()
        val date = Date(milliseconds)
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Affiche uniquement la date
        format.format(date)
    } catch (e: Exception) {
        "Invalid date" // Si le format est incorrect, retourne "Invalid date"
    }
}

// Représente un élément de données de lancer
data class LaunchDataItem(
    val launchId: Int,
    val timestamp: String
)

// Composable pour afficher l'interface de sélection de lancers
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaunchSelectionScreen(fetchLaunchData: suspend () -> List<LaunchDataItem>, navigateToLiveDataActivity: (Int) -> Unit) {
    var launchData by remember { mutableStateOf<List<LaunchDataItem>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        try {
            launchData = fetchLaunchData() // Récupère les données des lancers
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Erreur lors de la récupération des données : ${e.message}" // Affiche un message d'erreur en cas de problème
        }
    }

    // Barre de navigation avec un bouton retour
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sélectionner un Lancer") },
                navigationIcon = {
                    IconButton(onClick = {
                        // Retourne à l'activité précédente
                        (context as? Activity)?.onBackPressed()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            // Affiche un message d'erreur ou une indication si aucune donnée n'est disponible
            if (errorMessage != null) {
                Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)
            } else if (launchData.isEmpty()) {
                Text(text = "Aucune donnée disponible.")
            } else {
                // Affiche les lancers dans une liste déroulante
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(launchData) { launchItem ->
                        LaunchBox(launchItem, navigateToLiveDataActivity)
                    }
                }
            }
        }
    }
}

// Composable pour afficher un lancer sous forme de carte
@Composable
fun LaunchBox(item: LaunchDataItem, navigateToLiveDataActivity: (Int) -> Unit) {
    val launchDate = formatTimestampToDate(item.timestamp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { navigateToLiveDataActivity(item.launchId) }, // Navigue vers l'écran des données en direct lorsqu'on clique
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Lancer ${item.launchId}")
            Text(text = "Date : $launchDate")
        }
    }
}
