package ar.edu.anden

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Board(state: PanelState, refresh: () -> Unit, inspect: (Robot) -> Unit) {
    MaterialTheme {
        LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                Button(onClick = refresh, enabled = !state.busy) { Text("Actualizar flota") }
                Text(if (state.busy) "Leyendo…" else state.status)
            }
            state.detail?.let { robot ->
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Ficha · ${robot.id}", style = MaterialTheme.typography.titleLarge)
                            Text("${robot.sector} · Batería ${robot.battery}%")
                        }
                    }
                }
            }
            item { Text("Unidades (${state.rows.size})", style = MaterialTheme.typography.titleMedium) }
            items(state.rows, key = { it.id }) { robot ->
                OutlinedButton(onClick = { inspect(robot) }, modifier = Modifier.fillMaxWidth()) {
                    Text("${robot.id} · ${robot.sector} · ${robot.battery}%")
                }
            }
        }
    }
}
