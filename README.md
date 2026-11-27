# Andén | Puesto móvil de inspección

Un operador consulta la última ronda de robots de servicio y abre una ficha. El acelerómetro mide la orientación del teléfono que lleva el operador. La flota es simulada.

## Preparación anterior al examen

1. Abrí esta carpeta en Android Studio con JDK 17 y Android SDK 34. Sincronizá Gradle, ejecutá la variante debug en un AVD API 34 o teléfono Android 8+ y confirmá que aparece DEMO-01 en la primera instalación. La primera descarga requiere internet.
2. En una terminal de esta carpeta ejecutá python simulador.py a (en Linux también sirve python3). En otra ejecutá adb reverse tcp:8765 tcp:8765 con un solo dispositivo conectado. El servidor usa únicamente la interfaz local de la computadora.
3. Tocá Actualizar flota. Deben aparecer tres unidades. Abrí G1-02 y verificá Recepción, 76 %. En el AVD, Extended controls → Virtual sensors permite variar la inclinación del teléfono.
4. Para cambiar de ronda, detené el servidor con Ctrl+C y reinicialo con el argumento b, invalida o vacia. Actualizá desde el móvil. Con el servidor detenido podés ensayar desconexión. Restablecé la ronda a antes de empezar.

La API GET /fleet devuelve un arreglo JSON con id, sector y battery (entero porcentual). Cada respuesta representa el conjunto completo del momento. El cliente reemplaza su copia SQLite al actualizar. En cada arranque muestra la copia local. No se requiere robot físico, clave ni cuenta.
