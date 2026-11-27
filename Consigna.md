# Andén | Examen práctico individual
**Desarrollo de Aplicaciones I**

Recibís un puesto móvil de inspección para una flota de robots de servicio. El operador usa la lista y la ficha durante una ronda y puede perder conectividad. El proyecto se entrega abierto, compilado y con el simulador preparado según el README. El tiempo empieza con la ronda A visible.

## Pedido del operador

1. **Priorizar visitas.** Agregá un control «Solo batería baja» que muestre unidades con menos de 30 %. El contador debe reflejar el filtro. Al desactivarlo se recuperan todas las unidades, incluso sin red. La selección del filtro debe sobrevivir una rotación.
2. **Seguir una unidad entre rondas.** Con G1-02 seleccionado en A, actualizá a B. La ficha debe mostrar Ascensor y 22 %. Si la unidad seleccionada desaparece de la ronda, cerrá su ficha. Filtrar la lista solo cambia la lista.
3. **Recordar la inspección.** Desde la ficha, permití marcar una unidad como revisada. Mostrá esa condición al reabrir la ficha. Debe sobrevivir a una actualización y al cierre y reapertura del proceso, incluso sin red. Marcar dos veces debe tener el mismo efecto que marcar una. La identidad es el id del robot.
4. **Proteger la integridad del registro.** Considerá la respuesta de la API una entrada externa. Si cualquier batería está fuera de 0..100, rechazá la ronda completa, informá el motivo al operador y conservá la última copia válida también después del reinicio. Una ronda vacía válida debe aceptarse. Usá el escenario invalida para demostrar el rechazo.

## Decisión de arquitectura y entrega

Elegí y refactorizá al menos una parte del diseño cuya distribución de responsabilidades dificulte cumplir el pedido. Podés integrar ese cambio con una funcionalidad. Conservá MVVM, Retrofit, SQLite, la cabecera XML con lectura del sensor y el panel Compose. Eligan qué clases modificar. No se exige migrar frameworks ni incorporar librerías.

Entregá el proyecto y una nota de hasta ocho líneas con la decisión tomada, el síntoma que la motivó y dos pruebas efectuadas (entrada, acción y resultado observado). Podés usar IA y consultar el código durante el práctico. La defensa posterior será individual.

Agregá capturas de pantalla con la evidencia de las funcionalidades implementadas.
