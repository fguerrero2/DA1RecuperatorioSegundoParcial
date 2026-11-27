
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

