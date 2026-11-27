package ar.edu.anden.data.repository

import android.content.Context
import ar.edu.anden.data.local.FleetStore
import ar.edu.anden.data.model.Robot
import ar.edu.anden.data.remote.ApiClient
import ar.edu.anden.data.remote.FleetApi

class FleetRepository(
    context: Context,
    private val api: FleetApi = ApiClient.fleetApi()
) {
    private val store = FleetStore(context)

    fun local() = store.read()
    suspend fun refresh(): List<Robot> {
        store.replace(api.read())
        return local()
    }
    fun inspected() = store.inspected()
    fun markInspected(id: String) = store.markInspected(id)
    fun close() = store.close()
}
