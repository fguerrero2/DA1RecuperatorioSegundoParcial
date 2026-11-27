package ar.edu.anden.data.remote

import ar.edu.anden.data.model.Robot
import retrofit2.http.GET

/** Endpoint del simulador Python (simulador.py): GET /fleet */
interface FleetApi {
    @GET("fleet") suspend fun read(): List<Robot>
}
