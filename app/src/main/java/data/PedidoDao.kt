package com.gutierrez.saborapp.data

import android.content.ContentValues
import android.content.Context
import com.gutierrez.saborapp.model.DetallePedido
import com.gutierrez.saborapp.model.Pedido
import com.gutierrez.saborapp.model.Plato
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PedidoDao(context: Context) {

    private val dbHelper = DBHelper(context)

    /**
     * Interno: NO cierra la BD. Lo usan otros métodos dentro de una transacción.
     */
    private fun obtenerPedidoAbiertoInterno(idMesa: Int): Pedido? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DBHelper.TABLA_PEDIDO} " +
                    "WHERE ${DBHelper.COL_PED_ID_MESA} = ? AND ${DBHelper.COL_PED_ESTADO} = 'ABIERTO' " +
                    "LIMIT 1",
            arrayOf(idMesa.toString())
        )
        var pedido: Pedido? = null
        if (cursor.moveToFirst()) {
            pedido = Pedido(
                id = cursor.getInt(0),
                idMesa = cursor.getInt(1),
                fecha = cursor.getString(2),
                estado = cursor.getString(3),
                total = cursor.getDouble(4)
            )
        }
        cursor.close()
        return pedido
    }

    /**
     * Público: cierra la BD.
     */
    fun obtenerPedidoAbierto(idMesa: Int): Pedido? {
        val pedido = obtenerPedidoAbiertoInterno(idMesa)
        dbHelper.close()
        return pedido
    }

    fun agregarPlato(idMesa: Int, idPlato: Int, cantidad: Int, precioUnit: Double): Boolean {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        return try {
            var pedido = obtenerPedidoAbiertoInterno(idMesa)
            if (pedido == null) {
                val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val valoresPedido = ContentValues().apply {
                    put(DBHelper.COL_PED_ID_MESA, idMesa)
                    put(DBHelper.COL_PED_FECHA, fecha)
                    put(DBHelper.COL_PED_ESTADO, "ABIERTO")
                    put(DBHelper.COL_PED_TOTAL, 0.0)
                }
                val idPedido = db.insert(DBHelper.TABLA_PEDIDO, null, valoresPedido)
                pedido = Pedido(idPedido.toInt(), idMesa, fecha, "ABIERTO", 0.0)

                val valoresMesa = ContentValues().apply {
                    put(DBHelper.COL_MES_ESTADO, "OCUPADA")
                }
                db.update(DBHelper.TABLA_MESA, valoresMesa,
                    "${DBHelper.COL_MES_ID} = ?", arrayOf(idMesa.toString()))
            }

            val subtotal = cantidad * precioUnit
            val valoresDetalle = ContentValues().apply {
                put(DBHelper.COL_DET_ID_PEDIDO, pedido.id)
                put(DBHelper.COL_DET_ID_PLATO, idPlato)
                put(DBHelper.COL_DET_CANTIDAD, cantidad)
                put(DBHelper.COL_DET_PRECIO_UNIT, precioUnit)
                put(DBHelper.COL_DET_SUBTOTAL, subtotal)
            }
            db.insert(DBHelper.TABLA_DETALLE, null, valoresDetalle)

            val cursor = db.rawQuery(
                "SELECT SUM(${DBHelper.COL_DET_SUBTOTAL}) FROM ${DBHelper.TABLA_DETALLE} " +
                        "WHERE ${DBHelper.COL_DET_ID_PEDIDO} = ?",
                arrayOf(pedido.id.toString())
            )
            var nuevoTotal = 0.0
            if (cursor.moveToFirst()) nuevoTotal = cursor.getDouble(0)
            cursor.close()

            val valoresTotal = ContentValues().apply {
                put(DBHelper.COL_PED_TOTAL, nuevoTotal)
            }
            db.update(DBHelper.TABLA_PEDIDO, valoresTotal,
                "${DBHelper.COL_PED_ID} = ?", arrayOf(pedido.id.toString()))

            db.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            false
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun listarDetalle(idPedido: Int): List<DetallePedido> {
        val lista = mutableListOf<DetallePedido>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT d.${DBHelper.COL_DET_ID}, d.${DBHelper.COL_DET_ID_PEDIDO},
                   d.${DBHelper.COL_DET_ID_PLATO}, d.${DBHelper.COL_DET_CANTIDAD},
                   d.${DBHelper.COL_DET_PRECIO_UNIT}, d.${DBHelper.COL_DET_SUBTOTAL},
                   p.${DBHelper.COL_PLA_NOMBRE}
            FROM ${DBHelper.TABLA_DETALLE} d
            INNER JOIN ${DBHelper.TABLA_PLATO} p ON p.${DBHelper.COL_PLA_ID} = d.${DBHelper.COL_DET_ID_PLATO}
            WHERE d.${DBHelper.COL_DET_ID_PEDIDO} = ?
            """.trimIndent(),
            arrayOf(idPedido.toString())
        )
        while (cursor.moveToNext()) {
            lista.add(
                DetallePedido(
                    id = cursor.getInt(0),
                    idPedido = cursor.getInt(1),
                    idPlato = cursor.getInt(2),
                    cantidad = cursor.getInt(3),
                    precioUnit = cursor.getDouble(4),
                    subtotal = cursor.getDouble(5),
                    nombrePlato = cursor.getString(6)
                )
            )
        }
        cursor.close()
        db.close()
        return lista
    }

    fun listarPlatosDisponibles(): List<Plato> {
        val lista = mutableListOf<Plato>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DBHelper.TABLA_PLATO} " +
                    "WHERE ${DBHelper.COL_PLA_DISPONIBLE} = 1 " +
                    "ORDER BY ${DBHelper.COL_PLA_CATEGORIA}, ${DBHelper.COL_PLA_NOMBRE}",
            null
        )
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

    fun cerrarCuenta(idPedido: Int, idMesa: Int): Boolean {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        return try {
            val valoresPedido = ContentValues().apply {
                put(DBHelper.COL_PED_ESTADO, "CERRADO")
            }
            db.update(DBHelper.TABLA_PEDIDO, valoresPedido,
                "${DBHelper.COL_PED_ID} = ?", arrayOf(idPedido.toString()))

            val valoresMesa = ContentValues().apply {
                put(DBHelper.COL_MES_ESTADO, "LIBRE")
            }
            db.update(DBHelper.TABLA_MESA, valoresMesa,
                "${DBHelper.COL_MES_ID} = ?", arrayOf(idMesa.toString()))

            db.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            false
        } finally {
            db.endTransaction()
            db.close()
        }
    }
}