package com.minesspace
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    // Endpoint pour envoyer des données
    @POST("/api/data")
    fun postSensorData(@Body data: SensorData): Call<Void> // Retourne rien (void)

    // Endpoint pour récupérer les données
    @GET("/api/data")
    fun getSensorData(): Call<List<SensorData>> // Retourne une liste de données
}
