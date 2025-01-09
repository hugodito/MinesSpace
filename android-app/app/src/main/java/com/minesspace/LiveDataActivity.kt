package com.minesspace

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class LiveDataActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Cacher la barre supérieure (ActionBar)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // Cacher la barre inférieure (NavigationBar)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        setContent {
            LiveDataScreen(isDataSaveEnabled = true) // Modifiez ici pour activer/désactiver la sauvegarde
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveDataScreen(isDataSaveEnabled: Boolean) {
    val context = LocalContext.current
    var sensorDataList by remember { mutableStateOf(emptyList<SensorData>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Charger les données si la sauvegarde est activée
    LaunchedEffect(isDataSaveEnabled) {
        if (isDataSaveEnabled) {
            try {
                val data = RetrofitInstance.api.getAllSensorData()
                sensorDataList = data
            } catch (e: Exception) {
                Log.e("LiveDataScreen", "Erreur : ${e.message}")
                errorMessage = "Erreur lors de la récupération des données"
            } finally {
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                        Text(text = "Lancer en direct", color = Color.White, fontSize = 20.sp)
                    }
                },
                navigationIcon = {
                    Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                        IconButton(onClick = { context.startActivity(Intent(context, MainActivity::class.java)) }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
                        }
                    }
                },
                modifier = Modifier.height(100.dp),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Red
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                !isDataSaveEnabled -> {
                    Text(
                        text = "Désolé, aucun lancer en direct pour le moment",
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }
                isLoading -> {
                    Text(text = "Chargement...", fontSize = 20.sp, color = Color.Black)
                }
                errorMessage != null -> {
                    Text(text = errorMessage!!, fontSize = 20.sp, color = Color.Red)
                }
                sensorDataList.isNotEmpty() -> {
                    Text(
                        text = "Données du capteur :",
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    sensorDataList.forEach { data ->
                        Text(
                            text = "ID: ${data.id}, Temp: ${data.temperature}, Pression: ${data.pression}, Altitude: ${data.altitude}",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                else -> {
                    Text(
                        text = "Aucune donnée disponible pour le moment",
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LiveDataScreen(isDataSaveEnabled = true)
}
