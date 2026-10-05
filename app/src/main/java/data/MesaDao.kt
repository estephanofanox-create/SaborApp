package com.gutierrez.saborapp.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import com.gutierrez.saborapp.model.Mesa

class MesaDao(context: Context) {

    private val dbHelper = DBHelper(context)

    fun insertar(mesa: Mesa): Long {
        return try {
            val db = dbHelper.writableDatabase
            val valores = ContentValues().apply {
                put(DBHelper.COL_MES_NUMERO, mesa.numero)
                put(DBHelper.COL_MES_CAPACIDAD, mesa.capacidad)
                put(DBHelper.COL_MES_ESTADO, mesa.estado)
            }
            val id = db.insert(DBHelper.TABLA_MESA, null, valores)
            db.close()
            id
        } catch (e: SQLiteConstraintException) {
            -1
        }
    }

    fun listar(): List<Mesa> {
        val lista = mutableListOf<Mesa>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DBHelper.TABLA_MESA} ORDER BY ${DBHelper.COL_MES_NUMERO}",
            null
        )
        while (cursor.moveToNext()) {
            lista.add(
                Mesa(
                    id = cursor.getInt(0),
                    numero = cursor.getInt(1),
                    capacidad = cursor.getInt(2),
                    estado = cursor.getString(3)
                )
            )
        }
        cursor.close()
        db.close()
        return lista
    }
}