package ar.edu.anden.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
import android.database.sqlite.SQLiteOpenHelper
import ar.edu.anden.data.model.Robot

/**
 * Dos responsabilidades separadas en la misma base:
 * - fleet: copia de la última ronda recibida; [replace] la reemplaza entera.
 * - inspection: registro del operador (unidades revisadas, por id); [replace] no la toca.
 */
class FleetStore(context: Context) : SQLiteOpenHelper(context, "anden.db", null, 2) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE fleet(id TEXT PRIMARY KEY, sector TEXT NOT NULL, battery INTEGER NOT NULL)")
        db.execSQL("INSERT INTO fleet VALUES ('DEMO-01','Taller (muestra)',68)")
        createInspection(db)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) createInspection(db)
    }

    private fun createInspection(db: SQLiteDatabase) =
        db.execSQL("CREATE TABLE IF NOT EXISTS inspection(id TEXT PRIMARY KEY)")

    fun inspected(): Set<String> = readableDatabase.rawQuery("SELECT id FROM inspection", null).use { cursor ->
        buildSet { while (cursor.moveToNext()) add(cursor.getString(0)) }
    }

    /** Idempotente: marcar dos veces tiene el mismo efecto que marcar una. */
    fun markInspected(id: String) {
        val values = ContentValues().apply { put("id", id) }
        writableDatabase.insertWithOnConflict("inspection", null, values, CONFLICT_IGNORE)
    }

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
