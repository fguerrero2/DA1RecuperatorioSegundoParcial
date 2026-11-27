
A) Primera decision tomada, organizar las carpetas para trabajar con el codigo de una manera mas facil y pensando en los que posteriormente deberan realizar mantenimiento

B) Segunda decision tomada (refactorizar)

Vemos que el ViewModel guarda una copia de la ficha en lugar de la seleccionada y esto impide que podamos atender al pedido 2 del operador (2. **Seguir una unidad entre rondas.**)

Debemos modificar el archivo "ui/viewmodel/PanelModel.kt"

- Síntoma: selecciono G1-02 en la ronda A, actualizo a la B y la ficha sigue mostrando "Recepción 76 %" en vez de "Ascensor 22 %".
- Segundo síntoma: si la unidad desaparece de la ronda, la ficha queda abierta con datos que ya no existen.
- Refactor: guardar selectedId: String? y calcular la ficha a partir de los datos (rows.find { it.id == selectedId }), igual que ya hacés con visible.
  - Si el id ya no está, la ficha se cierra sola.
  - Como se busca en rows y no en visible, filtrar no toca la ficha ("Filtrar la lista solo cambia la lista").
  - Opcional: guardar selectedId en SavedStateHandle, como el filtro.

C) Tercera Decisión tomada (refactorizar)
Debemos corregir los archivos para persistencia porque "FleetStore.kt" borra toda la tabla al actualizar, con esto no podemos marcar como inspeccionada una unidad
ya que se borraran siempre los datos

- Refactor:
  - Separar dos responsabilidades: la copia de la ronda (fleet, se reemplaza entera) y el registro del operador (una tabla inspection(id TEXT PRIMARY KEY) que replace() no toca).
  - Marcar con INSERT OR IGNORE o insertWithOnConflict(..., CONFLICT_IGNORE). Así marcar dos veces tiene el mismo efecto que marcar una, y la identidad es el id.
  - Pasar a la versión 2 con un onUpgrade que cree la tabla.
  - Ningún cambio va por la red, así que funciona sin conexión.

  D) Cuarta decision tomada (refactorizar)

  Al escribir no tenemos validaciones implementadas y son necesarias para el pedido 4.  Vamos a modificar "data/repository/FleetRepository.kt"
  Actualmente Lo que manda la API va directo a SQLite.

- Síntoma: con la ronda invalida (AMR-03 con 145 %), la base y la pantalla aceptan el 145 % y se pierde la última copia válida.
- Refactor: el repositorio valida antes de escribir. Si alguna batería está fuera de 0..100, lanza InvalidRoundException(motivo) sin tocar la base.
  - La última copia válida se conserva aunque reinicies la app, porque nunca se escribió nada.
  - Una lista vacía pasa la validación y replace(emptyList()) ya funciona dentro de la transacción, así que la ronda vacia se acepta sin cambios.