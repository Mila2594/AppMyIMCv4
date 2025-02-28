package edu.camilacanete.ej09p04.helper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import edu.camilacanete.ej09p04.data.model.ImcRecord

class MyDBOpenHelper (context: Context) : SQLiteOpenHelper (context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val TAG = "SQLite"
    companion object {
        const val DATABASE_NAME = "imc_records.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "imc_records"
        const val COLUMN_ID = "id"
        const val COLUMN_DATE = "date"
        const val COLUMN_GENDER = "gender"
        const val COLUMN_WEIGHT = "weight"
        const val COLUMN_HEIGHT = "height"
        const val COLUMN_IMC_RESULT = "imc_result"
        const val COLUMN_STATE_RESULT = "state_result"
    }

    override fun onCreate(db: SQLiteDatabase) {
        try {
            val createTableQuery = """
                CREATE TABLE $TABLE_NAME (
                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_DATE TEXT NOT NULL,
                    $COLUMN_STATE_RESULT TEXT NOT NULL,
                    $COLUMN_GENDER TEXT NOT NULL,  
                    $COLUMN_WEIGHT REAL NOT NULL,
                    $COLUMN_HEIGHT REAL NOT NULL,
                    $COLUMN_IMC_RESULT REAL NOT NULL
                )
            """.trimIndent()

            db.execSQL(createTableQuery)
        } catch (e: SQLiteException){
            Log.e("$TAG (onCreate)", e.message.toString())

        }        }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        try {
            val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
            db!!.execSQL(dropTableQuery)
            onCreate(db)
        } catch (e: SQLiteException){
            Log.e("$TAG (onUpgrade)", e.message.toString())
        }
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
    }

    /**
     * Método para agregar un nuevo registro en la tabla imc_records
     */
    fun addRecord(
        date: String,
        stateResult: String,
        gender: String, weight:
        Double,
        height: Double,
        imcResult: Double) : Long {


        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_DATE, date)
        values.put(COLUMN_GENDER, gender)
        values.put(COLUMN_IMC_RESULT, imcResult)
        values.put(COLUMN_WEIGHT, weight)
        values.put(COLUMN_HEIGHT, height)
        values.put(COLUMN_STATE_RESULT, stateResult)
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    /**
     * Método para eliminar un registro de la tabla imc_records
     */
    fun deleteRecord(id: Long) : Int {
        val db = this.writableDatabase
        val rowsDelete = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsDelete
    }

    /**
     * Método para obtener todos los registros de la tabla imc_records
     */
    fun getAllRecords(): List<ImcRecord> {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, null)
        val records = mutableListOf<ImcRecord>()

        try {
            if (cursor.moveToFirst()) {
                do {
                    val record = ImcRecord(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        gender = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)),
                        weight = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)),
                        height = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT)),
                        imcResult = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_IMC_RESULT)),
                        stateResult = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATE_RESULT))
                    )
                    records.add(record)
                }while (cursor.moveToNext())
            }
        }catch (e: IllegalArgumentException){
            Log.e("getAllRecords", "Column not found: ${e.message}")
        }finally {
            cursor.close()
            db.close()
        }
        return records
    }
}