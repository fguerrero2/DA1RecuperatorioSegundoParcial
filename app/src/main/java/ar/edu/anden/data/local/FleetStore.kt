package ar.edu.anden.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ar.edu.anden.data.model.Robot

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
