package ar.edu.anden.ui.viewmodel

import ar.edu.anden.data.model.Robot
import ar.edu.anden.data.repository.FleetRepository

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PanelState(
    val rows: List<Robot> = emptyList(),
    val selectedId: String? = null,
    val busy: Boolean = false,
    val status: String = "",
    val lowBatteryOnly: Boolean = false
) {
    /** Unidades a mostrar: el filtro se aplica sobre la copia completa, sin tocar la red ni la base. */
    val visible: List<Robot>
        get() = if (lowBatteryOnly) rows.filter { it.battery < LOW_BATTERY } else rows

    /**
     * Ficha de la unidad seleccionada, siempre con los datos de la ronda vigente.
     * Se busca en [rows] y no en [visible]: filtrar solo cambia la lista, no la ficha.
     */
    val detail: Robot?
        get() = selectedId?.let { id -> rows.find { it.id == id } }

    companion object {
        const val LOW_BATTERY = 30
    }
}

class PanelModel(app: Application, private val saved: SavedStateHandle) : AndroidViewModel(app) {
    private val repo = FleetRepository(app)
    private val current = MutableStateFlow(
        PanelState(
            selectedId = saved[KEY_SELECTED],
            lowBatteryOnly = saved[KEY_LOW_BATTERY] ?: false
        )
    )
    val state = current.asStateFlow()

    init { load(false) }

    fun showLowBatteryOnly(enabled: Boolean) {
        saved[KEY_LOW_BATTERY] = enabled
        current.update { it.copy(lowBatteryOnly = enabled) }
    }

    fun inspect(robot: Robot) = select(robot.id)

    private fun select(id: String?) {
        saved[KEY_SELECTED] = id
        current.update { it.copy(selectedId = id) }
    }

    fun load(remote: Boolean) {
        if (current.value.busy) return
        current.update { it.copy(busy = true) }
        viewModelScope.launch {
            try {
                val rows = withContext(Dispatchers.IO) { if (remote) repo.refresh() else repo.local() }
                current.update { it.copy(rows = rows, status = if (remote) "Actualización recibida" else "Copia local") }
                // Si la unidad seleccionada ya no está en la ronda, se cierra su ficha
                // (y no se reabre sola si vuelve a aparecer en una ronda posterior).
                val selected = current.value.selectedId
                if (selected != null && rows.none { it.id == selected }) select(null)
            } catch (cancel: CancellationException) {
                throw cancel
            } catch (error: Exception) {
                current.update { it.copy(status = "Lectura fallida. Podés reintentar.") }
            } finally {
                current.update { it.copy(busy = false) }
            }
        }
    }
    override fun onCleared() { repo.close() }

    private companion object {
        const val KEY_LOW_BATTERY = "lowBatteryOnly"
        const val KEY_SELECTED = "selectedId"
    }
}
