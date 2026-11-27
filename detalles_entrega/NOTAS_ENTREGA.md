1. Decisión: separar responsabilidades. El ViewModel guarda solo `selectedId` y calcula la ficha y la lista a partir de `rows`; SQLite separa `fleet` (copia de la ronda, se reemplaza) de `inspection` (registro del operador, versión 2); el repositorio valida la ronda antes de escribirla.
2. Síntoma 1: con G1-02 abierto en la ronda A, al actualizar a B la ficha seguía mostrando "Recepción 76 %", porque el ViewModel guardaba una copia del Robot.
3. Síntoma 2: `replace()` borraba toda la base (no había dónde guardar "revisada") y la ronda invalida (145 %) se guardaba y pisaba la última copia válida.
4. Antes se reorganizó el código en paquetes MVVM (data/local, remote, repository, model; ui/view, viewmodel) para facilitar el mantenimiento.
5. Prueba 1. Entrada: rondas A y B. Acción: abrir G1-02, activar «Solo batería baja» y actualizar a B. Resultado: la ficha muestra "Ascensor 22 %" y el filtro solo cambió la lista.
6. Prueba 2. Entrada: ronda A y luego invalida. Acción: actualizar y reiniciar la app sin red. Resultado: "Ronda rechazada: batería fuera de 0..100 (AMR-03 con 145 %)" y se conservan las 3 unidades de A.
7. Adicional: G1-02 marcada como revisada sigue con "✓ Revisada" después de actualizar, cerrar la app y reabrir sin red.
8. Evidencia: capturas en `detalles_entrega/` (01 a 06).
