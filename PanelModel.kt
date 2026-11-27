package ar.edu.anden

import android.app.Application
import androidx.lifecycle.AndroidViewModel
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
    val detail: Robot? = null,
    val busy: Boolean = false,
    val status: String = ""
)

class PanelModel(app: Application) : AndroidViewModel(app) {
    private val repo = FleetRepository(app)
    private val current = MutableStateFlow(PanelState())
    val state = current.asStateFlow()

    init { load(false) }

    fun inspect(robot: Robot) = current.update { it.copy(detail = robot) }

    fun load(remote: Boolean) {
        if (current.value.busy) return
        current.update { it.copy(busy = true) }
        viewModelScope.launch {
            try {
                val rows = withContext(Dispatchers.IO) { if (remote) repo.refresh() else repo.local() }
                current.update { it.copy(rows = rows, status = if (remote) "Actualización recibida" else "Copia local") }
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
}
