package ar.edu.anden

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class Robot(val id: String, val sector: String, val battery: Int)

interface FleetApi {
    @GET("fleet") suspend fun read(): List<Robot>
}

class FleetStore(context: Context) : SQLiteOpenHelper(context, "anden.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE fleet(id TEXT PRIMARY KEY, sector TEXT NOT NULL, battery INTEGER NOT NULL)")
        db.execSQL("INSERT INTO fleet VALUES ('DEMO-01','Taller (muestra)',68)")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit

    fun read(): List<Robot> = readableDatabase.rawQuery(
        "SELECT id,sector,battery FROM fleet ORDER BY id", null
    ).use { cursor ->
        buildList {
            while (cursor.moveToNext()) add(Robot(cursor.getString(0), cursor.getString(1), cursor.getInt(2)))
        }
    }

    fun replace(rows: List<Robot>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete("fleet", null, null)
            rows.forEach { robot ->
                val values = ContentValues().apply {
                    put("id", robot.id)
                    put("sector", robot.sector)
                    put("battery", robot.battery)
                }
                db.insertOrThrow("fleet", null, values)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}

class FleetRepository(context: Context) {
    private val store = FleetStore(context)
    private val api = Retrofit.Builder().baseUrl("http://localhost:8765/")
        .addConverterFactory(GsonConverterFactory.create()).build().create(FleetApi::class.java)

    fun local() = store.read()
    suspend fun refresh(): List<Robot> {
        store.replace(api.read())
        return local()
    }
    fun close() = store.close()
}
