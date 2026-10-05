package com.gutierrez.saborapp.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "saborapp.db"
        const val DB_VERSION = 2

        // Tabla usuario
        const val TABLA_USUARIO = "usuario"
        const val COL_USU_ID = "id"
        const val COL_USU_NOMBRE = "usuario"
        const val COL_USU_CLAVE = "clave"
        const val COL_USU_ROL = "rol"

        // Tabla plato
        const val TABLA_PLATO = "plato"
        const val COL_PLA_ID = "id"
        const val COL_PLA_NOMBRE = "nombre"
        const val COL_PLA_CATEGORIA = "categoria"
        const val COL_PLA_PRECIO = "precio"
        const val COL_PLA_DISPONIBLE = "disponible"

        // Tabla mesa
        const val TABLA_MESA = "mesa"
        const val COL_MES_ID = "id"
        const val COL_MES_NUMERO = "numero"
        const val COL_MES_CAPACIDAD = "capacidad"
        const val COL_MES_ESTADO = "estado"

        // Tabla pedido
        const val TABLA_PEDIDO = "pedido"
        const val COL_PED_ID = "id"
        const val COL_PED_ID_MESA = "id_mesa"
        const val COL_PED_FECHA = "fecha"
        const val COL_PED_ESTADO = "estado"
        const val COL_PED_TOTAL = "total"

        // Tabla detalle_pedido
        const val TABLA_DETALLE = "detalle_pedido"
        const val COL_DET_ID = "id"
        const val COL_DET_ID_PEDIDO = "id_pedido"
        const val COL_DET_ID_PLATO = "id_plato"
        const val COL_DET_CANTIDAD = "cantidad"
        const val COL_DET_PRECIO_UNIT = "precio_unit"
        const val COL_DET_SUBTOTAL = "subtotal"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLA_USUARIO (
                $COL_USU_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USU_NOMBRE TEXT UNIQUE,
                $COL_USU_CLAVE TEXT,
                $COL_USU_ROL TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLA_PLATO (
                $COL_PLA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PLA_NOMBRE TEXT NOT NULL,
                $COL_PLA_CATEGORIA TEXT,
                $COL_PLA_PRECIO REAL CHECK($COL_PLA_PRECIO > 0),
                $COL_PLA_DISPONIBLE INTEGER
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLA_MESA (
                $COL_MES_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_MES_NUMERO INTEGER UNIQUE,
                $COL_MES_CAPACIDAD INTEGER,
                $COL_MES_ESTADO TEXT
            )
            """.trimIndent()
        )

        crearTablasPedido(db)
        insertarUsuariosPorDefecto(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            crearTablasPedido(db)
        }
    }

    private fun crearTablasPedido(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLA_PEDIDO (
                $COL_PED_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PED_ID_MESA INTEGER,
                $COL_PED_FECHA TEXT,
                $COL_PED_ESTADO TEXT,
                $COL_PED_TOTAL REAL,
                FOREIGN KEY ($COL_PED_ID_MESA) REFERENCES $TABLA_MESA($COL_MES_ID)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLA_DETALLE (
                $COL_DET_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DET_ID_PEDIDO INTEGER,
                $COL_DET_ID_PLATO INTEGER,
                $COL_DET_CANTIDAD INTEGER CHECK($COL_DET_CANTIDAD > 0),
                $COL_DET_PRECIO_UNIT REAL,
                $COL_DET_SUBTOTAL REAL,
                FOREIGN KEY ($COL_DET_ID_PEDIDO) REFERENCES $TABLA_PEDIDO($COL_PED_ID) ON DELETE CASCADE,
                FOREIGN KEY ($COL_DET_ID_PLATO) REFERENCES $TABLA_PLATO($COL_PLA_ID)
            )
            """.trimIndent()
        )
    }

    private fun insertarUsuariosPorDefecto(db: SQLiteDatabase) {
        val admin = ContentValues().apply {
            put(COL_USU_NOMBRE, "admin")
            put(COL_USU_CLAVE, "1234")
            put(COL_USU_ROL, "ADMIN")
        }
        db.insert(TABLA_USUARIO, null, admin)

        val mozo = ContentValues().apply {
            put(COL_USU_NOMBRE, "mozo")
            put(COL_USU_CLAVE, "1234")
            put(COL_USU_ROL, "MOZO")
        }
        db.insert(TABLA_USUARIO, null, mozo)
    }
}