package ar.edu.anden.data.repository

import android.content.Context
import ar.edu.anden.data.local.FleetStore
import ar.edu.anden.data.model.Robot
import ar.edu.anden.data.remote.ApiClient
import ar.edu.anden.data.remote.FleetApi

/** La ronda recibida no es válida; [message] explica el motivo para el operador. */
class InvalidRoundException(message: String) : Exception(message)

class FleetRepository(
    context: Context,
    private val api: FleetApi = ApiClient.fleetApi()
) {
    private val store = FleetStore(context)

    fun local() = store.read()
    /**
     * Lee la ronda de la API (entrada externa) y la valida antes de escribir.
     * Si no es válida se rechaza entera y la base no se toca: queda la última copia válida.
     * Una ronda vacía es válida.
     */
    suspend fun refresh(): List<Robot> {
        val round = api.read()
        validate(round)
        store.replace(round)
        return local()
    }

    private fun validate(round: List<Robot>) {
        val invalid = round.filter { it.battery !in 0..100 }
        if (invalid.isNotEmpty()) {
            val detail = invalid.joinToString { "${it.id} con ${it.battery} %" }
            throw InvalidRoundException("batería fuera de 0..100 ($detail)")
        }
    }
    fun inspected() = store.inspected()
    fun markInspected(id: String) = store.markInspected(id)
    fun close() = store.close()
}
