package ar.edu.anden.ui.view

import ar.edu.anden.R
import ar.edu.anden.ui.viewmodel.PanelModel

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.roundToInt

class PanelActivity : ComponentActivity(), SensorEventListener {
    private val model: PanelModel by viewModels()
    private val sensors by lazy { getSystemService(SENSOR_SERVICE) as SensorManager }
    private lateinit var tilt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.panel)
        tilt = findViewById(R.id.tilt)
        findViewById<ComposeView>(R.id.board).setContent {
            Board(model.state.collectAsStateWithLifecycle().value, { model.load(true) }, model::inspect)
        }
    }
    override fun onResume() {
        super.onResume()
        val sensor = sensors.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (sensor == null) tilt.text = "Terminal sin acelerómetro"
        else sensors.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
    }
    override fun onPause() {
        sensors.unregisterListener(this)
        super.onPause()
    }
    override fun onSensorChanged(event: SensorEvent) {
        val (x, y, z) = event.values
        val degrees = Math.toDegrees(atan2(hypot(x.toDouble(), y.toDouble()), z.toDouble())).roundToInt()
        tilt.text = "Inclinación del terminal · ${degrees}°"
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
