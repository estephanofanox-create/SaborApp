package com.gutierrez.saborapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gutierrez.saborapp.data.ReporteDao
import com.gutierrez.saborapp.databinding.ActivityReportesBinding

class ReportesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportesBinding
    private lateinit var reporteDao: ReporteDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reporteDao = ReporteDao(this)

        binding.btnCompartirResumen.setOnClickListener {
            compartirResumen()
        }

        cargarReportes()
    }

    override fun onResume() {
        super.onResume()
        cargarReportes()
    }

    private fun cargarReportes() {
        val venta = reporteDao.ventaDelDia()
        val cant = reporteDao.cantidadPedidosDelDia()
        val promedio = reporteDao.ticketPromedio()
        val top = reporteDao.topPlatos()

        binding.txtVentaDia.text = "S/ %.2f".format(venta)
        binding.txtCantPedidos.text = cant.toString()
        binding.txtTicketPromedio.text = "S/ %.2f".format(promedio)

        binding.layoutTopPlatos.removeAllViews()

        if (top.isEmpty()) {
            binding.txtSinVentas.visibility = android.view.View.VISIBLE
        } else {
            binding.txtSinVentas.visibility = android.view.View.GONE
            for ((nombre, cantidad, total) in top) {
                val fila = LayoutInflater.from(this)
                    .inflate(android.R.layout.simple_list_item_2, binding.layoutTopPlatos, false)

                val txt1 = fila.findViewById<TextView>(android.R.id.text1)
                val txt2 = fila.findViewById<TextView>(android.R.id.text2)

                txt1.text = nombre
                txt1.setTextColor(resources.getColor(R.color.negro, null))
                txt1.textSize = 15f

                txt2.text = "$cantidad vendidos · S/ %.2f".format(total)
                txt2.setTextColor(resources.getColor(R.color.gris_texto, null))
                txt2.textSize = 12f

                binding.layoutTopPlatos.addView(fila)
            }
        }
    }

    private fun compartirResumen() {
        val venta = reporteDao.ventaDelDia()
        val cant = reporteDao.cantidadPedidosDelDia()
        val top = reporteDao.topPlatos()

        val texto = buildString {
            appendLine("📊 *Resumen del día - SaborApp*")
            appendLine("Venta del día: S/ %.2f".format(venta))
            appendLine("Pedidos cerrados: $cant")
            appendLine()
            appendLine("*Top platos:*")
            if (top.isEmpty()) {
                appendLine("Sin ventas hoy")
            } else {
                for ((nombre, cantidad, _) in top) {
                    appendLine("• $nombre — $cantidad")
                }
            }
        }

        val i = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, texto)
        }
        startActivity(Intent.createChooser(i, "Compartir resumen"))
    }
}