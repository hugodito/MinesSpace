package com.minesspace

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LaunchSelectionActivity : ComponentActivity() {
    private val apiUrl = "http://10.0.2.2:3000/api/data"
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LaunchSelectionScreen(fetchLaunchData = ::fetchLaunchData, navigateToLiveDataActivity = ::navigateToLiveDataActivity)
        }
    }

    private suspend fun fetchLaunchData(): List<LaunchDataItem> {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(apiUrl).build()
                val response = client.newCall(request).execute()
                val responseData = response.peekBody(Long.MAX_VALUE).string()

                if (response.isSuccessful) {
                    parseLaunchJson(responseData)
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    private fun parseLaunchJson(responseData: String): List<LaunchDataItem> {
        val jsonArray = JSONArray(responseData)
        val launchDataList = mutableListOf<LaunchDataItem>()
        val seenLaunchIds = mutableSetOf<Int>()

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

        for (launchId in launchDataMap.keys) {
            val sortedData = launchDataMap[launchId]?.sortedBy { it.timestamp.toLong() }
            // Ajoutez la première donnée (la plus ancienne)
            if (sortedData != null && sortedData.isNotEmpty()) {
                launchDataList.add(sortedData.first())
            }
        }

        // Retourner les données triées par launchId
        return launchDataList.sortedByDescending { it.launchId }
    }

    private fun navigateToLiveDataActivity(launchId: Int) {
        val intent = Intent(this, LiveDataActivity::class.java)
        intent.putExtra("launch_id", launchId)
        startActivity(intent)
    }
}

fun formatTimestampToDate(timestamp: String): String {
    return try {
        val milliseconds = timestamp.toLong()
        val date = Date(milliseconds)
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Affiche uniquement la date
        format.format(date)
    } catch (e: Exception) {
        "Invalid date"
    }
}

data class LaunchDataItem(
    val launchId: Int,
    val timestamp: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaunchSelectionScreen(fetchLaunchData: suspend () -> List<LaunchDataItem>, navigateToLiveDataActivity: (Int) -> Unit) {
    var launchData by remember { mutableStateOf<List<LaunchDataItem>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        try {
            launchData = fetchLaunchData()
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Erreur lors de la récupération des données : ${e.message}"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sélectionner un Lancer") },
                navigationIcon = {
                    IconButton(onClick = {
                        // Vérifier que le contexte est bien une activité
                        (context as? Activity)?.onBackPressed()
                    }) {
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
            } else if (launchData.isEmpty()) {
                Text(text = "Aucune donnée disponible.")
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(launchData) { launchItem ->
                        LaunchBox(launchItem, navigateToLiveDataActivity)
                    }
                }
            }
        }
    }
}

@Composable
fun LaunchBox(item: LaunchDataItem, navigateToLiveDataActivity: (Int) -> Unit) {
    val launchDate = formatTimestampToDate(item.timestamp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { navigateToLiveDataActivity(item.launchId) },
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