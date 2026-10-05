package com.gutierrez.saborapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gutierrez.saborapp.data.MesaDao
import com.gutierrez.saborapp.data.PedidoDao
import com.gutierrez.saborapp.databinding.ActivityCuentaBinding

class CuentaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCuentaBinding
    private lateinit var adapter: DetalleAdapter
    private lateinit var pedidoDao: PedidoDao
    private lateinit var mesaDao: MesaDao

    private var idMesa: Int = 0
    private var numeroMesa: Int = 0
    private var idPedidoAbierto: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCuentaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pedidoDao = PedidoDao(this)
        mesaDao = MesaDao(this)

        idMesa = intent.getIntExtra("idMesa", 0)
        numeroMesa = intent.getIntExtra("numeroMesa", 0)

        binding.txtTituloCuenta.text = "Mesa $numeroMesa"

        adapter = DetalleAdapter(emptyList())
        binding.rvDetalle.layoutManager = LinearLayoutManager(this)
        binding.rvDetalle.adapter = adapter

        binding.btnCerrarCuenta.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cerrar cuenta")
                .setMessage("¿Confirmas cerrar la cuenta de la mesa $numeroMesa?")
                .setPositiveButton("Sí") { _, _ ->
                    if (pedidoDao.cerrarCuenta(idPedidoAbierto, idMesa)) {
                        Toast.makeText(this, "Cuenta cerrada", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Error al cerrar", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }

        binding.btnCompartirCuenta.setOnClickListener {
            compartirCuenta()
        }

        cargarCuenta()
    }

    override fun onResume() {
        super.onResume()
        cargarCuenta()
    }

    private fun cargarCuenta() {
        val pedido = pedidoDao.obtenerPedidoAbierto(idMesa)
        if (pedido == null) {
            adapter.actualizar(emptyList())
            binding.txtTotalCuenta.text = "Total: S/ 0.00"
            binding.btnCerrarCuenta.isEnabled = false
            binding.btnCompartirCuenta.isEnabled = false
            binding.txtEstadoMesa.text = "Sin pedido abierto"
        } else {
            idPedidoAbierto = pedido.id
            val detalles = pedidoDao.listarDetalle(pedido.id)
            adapter.actualizar(detalles)
            binding.txtTotalCuenta.text = "Total: S/ %.2f".format(pedido.total)
            binding.btnCerrarCuenta.isEnabled = detalles.isNotEmpty()
            binding.btnCompartirCuenta.isEnabled = detalles.isNotEmpty()
            binding.txtEstadoMesa.text = "Pedido #${pedido.id} · ${pedido.fecha}"
        }
    }

    private fun compartirCuenta() {
        val pedido = pedidoDao.obtenerPedidoAbierto(idMesa)
        if (pedido == null) {
            Toast.makeText(this, "No hay pedido abierto", Toast.LENGTH_SHORT).show()
            return
        }
        val detalles = pedidoDao.listarDetalle(pedido.id)

        val texto = buildString {
            appendLine("🍗 *Pollería El Buen Sabor*")
            appendLine("Mesa: $numeroMesa")
            appendLine("Fecha: ${pedido.fecha}")
            appendLine("------------------------")
            for (d in detalles) {
                appendLine("${d.cantidad} x ${d.nombrePlato} — S/ %.2f".format(d.subtotal))
            }
            appendLine("------------------------")
            appendLine("*TOTAL: S/ %.2f*".format(pedido.total))
        }

        val i = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, texto)
        }
        startActivity(Intent.createChooser(i, "Compartir cuenta"))
    }
}