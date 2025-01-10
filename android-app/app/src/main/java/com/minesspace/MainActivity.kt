package com.minesspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.minesspace.ui.theme.MinesSpaceTheme
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var fetchDataButton: Button
    private lateinit var postDataButton: Button
    private lateinit var displayDataTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialiser les vues
        fetchDataButton = findViewById(R.id.fetchDataButton)
        postDataButton = findViewById(R.id.postDataButton)
        displayDataTextView = findViewById(R.id.displayDataTextView)

        // Bouton pour récupérer les données
        fetchDataButton.setOnClickListener {
            fetchSensorData()
        }

        // Bouton pour envoyer des données
        postDataButton.setOnClickListener {
            val sensorData = SensorData(temperature = 22.5, pression = 1013.0)
            sendSensorData(sensorData)
        }
    }

    private fun fetchSensorData() {
        // Récupérer les données via Retrofit
        RetrofitClient.apiService.getSensorData().enqueue(object : Callback<List<SensorData>> {
            override fun onResponse(call: Call<List<SensorData>>, response: Response<List<SensorData>>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    displayDataTextView.text = data?.joinToString("\n") { "Temp: ${it.temperature}, Pression: ${it.pression}" }
                }
            }

            override fun onFailure(call: Call<List<SensorData>>, t: Throwable) {
                displayDataTextView.text = "Erreur lors de la récupération des données"
            }
        })
    }

    private fun sendSensorData(sensorData: SensorData) {
        // Envoyer les données via Retrofit
        RetrofitClient.apiService.postSensorData(sensorData).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    displayDataTextView.text = "Données envoyées avec succès!"
                } else {
                    displayDataTextView.text = "Erreur lors de l'envoi des données"
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                displayDataTextView.text = "Erreur réseau"
            }
        })
    }
}
