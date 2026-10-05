package com.gutierrez.saborapp.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import com.gutierrez.saborapp.model.Plato

class PlatoDao(context: Context) {

    private val dbHelper = DBHelper(context)

    fun insertar(plato: Plato): Long {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put(DBHelper.COL_PLA_NOMBRE, plato.nombre)
            put(DBHelper.COL_PLA_CATEGORIA, plato.categoria)
            put(DBHelper.COL_PLA_PRECIO, plato.precio)
            put(DBHelper.COL_PLA_DISPONIBLE, plato.disponible)
        }
        val id = db.insert(DBHelper.TABLA_PLATO, null, valores)
        db.close()
        return id
    }

    fun obtener(id: Int): Plato? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DBHelper.TABLA_PLATO} WHERE ${DBHelper.COL_PLA_ID} = ?",
            arrayOf(id.toString())
        )
        var plato: Plato? = null
        if (cursor.moveToFirst()) {
            plato = Plato(
                id = cursor.getInt(0),
                nombre = cursor.getString(1),
                categoria = cursor.getString(2),
                precio = cursor.getDouble(3),
                disponible = cursor.getInt(4)
            )
        }
        cursor.close()
        db.close()
        return plato
    }

    fun actualizar(plato: Plato): Int {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put(DBHelper.COL_PLA_NOMBRE, plato.nombre)
            put(DBHelper.COL_PLA_CATEGORIA, plato.categoria)
            put(DBHelper.COL_PLA_PRECIO, plato.precio)
            put(DBHelper.COL_PLA_DISPONIBLE, plato.disponible)
        }
        val filas = db.update(
            DBHelper.TABLA_PLATO,
            valores,
            "${DBHelper.COL_PLA_ID} = ?",
            arrayOf(plato.id.toString())
        )
        db.close()
        return filas
    }

    fun eliminar(id: Int): Boolean {
        return try {
            val db = dbHelper.writableDatabase
            val filas = db.delete(
                DBHelper.TABLA_PLATO,
                "${DBHelper.COL_PLA_ID} = ?",
                arrayOf(id.toString())
            )
            db.close()
            filas > 0
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    fun listar(filtro: String = ""): List<Plato> {
        val lista = mutableListOf<Plato>()
        val db = dbHelper.readableDatabase
        val sql: String
        val args: Array<String>
        if (filtro.isEmpty()) {
            sql = "SELECT * FROM ${DBHelper.TABLA_PLATO} " +
                    "ORDER BY ${DBHelper.COL_PLA_CATEGORIA}, ${DBHelper.COL_PLA_NOMBRE}"
            args = emptyArray()
        } else {
            sql = "SELECT * FROM ${DBHelper.TABLA_PLATO} " +
                    "WHERE ${DBHelper.COL_PLA_NOMBRE} LIKE ? " +
                    "ORDER BY ${DBHelper.COL_PLA_CATEGORIA}, ${DBHelper.COL_PLA_NOMBRE}"
            args = arrayOf("%$filtro%")
        }
        val cursor = db.rawQuery(sql, args)
        while (cursor.moveToNext()) {
            lista.add(
                Plato(
                    id = cursor.getInt(0),
                    nombre = cursor.getString(1),
                    categoria = cursor.getString(2),
                    precio = cursor.getDouble(3),
                    disponible = cursor.getInt(4)
                )
            )
        }
        cursor.close()
        db.close()
        return lista
    }
}