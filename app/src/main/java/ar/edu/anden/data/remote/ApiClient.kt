package ar.edu.anden.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Cliente Retrofit hacia el simulador Python.
 * El simulador escucha en 127.0.0.1:8765; en el dispositivo se expone con
 * `adb reverse tcp:8765 tcp:8765`, por eso la URL es localhost.
 */
object ApiClient {
    const val BASE_URL = "http://localhost:8765/"

    fun fleetApi(): FleetApi = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build().create(FleetApi::class.java)
}
