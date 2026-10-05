package com.gutierrez.saborapp.data

import android.content.Context
import com.gutierrez.saborapp.model.Usuario

class UsuarioDao(context: Context) {

    private val dbHelper = DBHelper(context)

    /**
     * Devuelve el Usuario si usuario y clave coinciden, o null si no.
     */
    fun validarUsuario(usuario: String, clave: String): Usuario? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT ${DBHelper.COL_USU_ID}, ${DBHelper.COL_USU_NOMBRE}, " +
                    "${DBHelper.COL_USU_CLAVE}, ${DBHelper.COL_USU_ROL} " +
                    "FROM ${DBHelper.TABLA_USUARIO} " +
                    "WHERE ${DBHelper.COL_USU_NOMBRE} = ? AND ${DBHelper.COL_USU_CLAVE} = ?",
            arrayOf(usuario, clave)
        )

        var resultado: Usuario? = null
        if (cursor.moveToFirst()) {
            resultado = Usuario(
                id = cursor.getInt(0),
                usuario = cursor.getString(1),
                clave = cursor.getString(2),
                rol = cursor.getString(3)
            )
        }
        cursor.close()
        db.close()
        return resultado
    }
}