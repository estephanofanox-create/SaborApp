package com.gutierrez.saborapp.data

import android.content.Context

class ReporteDao(context: Context) {

    private val dbHelper = DBHelper(context)

    fun ventaDelDia(): Double {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT IFNULL(SUM(${DBHelper.COL_PED_TOTAL}), 0) " +
                    "FROM ${DBHelper.TABLA_PEDIDO} " +
                    "WHERE ${DBHelper.COL_PED_ESTADO} = 'CERRADO' " +
                    "AND date(${DBHelper.COL_PED_FECHA}) = date('now','localtime')",
            null
        )
        var total = 0.0
        if (cursor.moveToFirst()) total = cursor.getDouble(0)
        cursor.close()
        db.close()
        return total
    }

    fun cantidadPedidosDelDia(): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM ${DBHelper.TABLA_PEDIDO} " +
                    "WHERE ${DBHelper.COL_PED_ESTADO} = 'CERRADO' " +
                    "AND date(${DBHelper.COL_PED_FECHA}) = date('now','localtime')",
            null
        )
        var cant = 0
        if (cursor.moveToFirst()) cant = cursor.getInt(0)
        cursor.close()
        db.close()
        return cant
    }

    fun ticketPromedio(): Double {
        val cant = cantidadPedidosDelDia()
        if (cant == 0) return 0.0
        return ventaDelDia() / cant
    }

    /**
     * Devuelve lista de Triple(nombrePlato, cantidadVendida, subtotal)
     */
    fun topPlatos(): List<Triple<String, Int, Double>> {
        val lista = mutableListOf<Triple<String, Int, Double>>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT p.${DBHelper.COL_PLA_NOMBRE},
                   SUM(d.${DBHelper.COL_DET_CANTIDAD}) AS cant,
                   SUM(d.${DBHelper.COL_DET_SUBTOTAL}) AS total
            FROM ${DBHelper.TABLA_DETALLE} d
            INNER JOIN ${DBHelper.TABLA_PLATO} p ON p.${DBHelper.COL_PLA_ID} = d.${DBHelper.COL_DET_ID_PLATO}
            INNER JOIN ${DBHelper.TABLA_PEDIDO} pe ON pe.${DBHelper.COL_PED_ID} = d.${DBHelper.COL_DET_ID_PEDIDO}
            WHERE pe.${DBHelper.COL_PED_ESTADO} = 'CERRADO'
            GROUP BY p.${DBHelper.COL_PLA_ID}
            ORDER BY cant DESC
            LIMIT 5
            """.trimIndent(),
            null
        )
        while (cursor.moveToNext()) {
            lista.add(
                Triple(cursor.getString(0), cursor.getInt(1), cursor.getDouble(2))
            )
        }
        cursor.close()
        db.close()
        return lista
    }
}