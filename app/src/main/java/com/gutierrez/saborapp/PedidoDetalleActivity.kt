package com.gutierrez.saborapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gutierrez.saborapp.data.PedidoDao
import com.gutierrez.saborapp.databinding.ActivityPedidoDetalleBinding
import com.gutierrez.saborapp.model.Plato

class PedidoDetalleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPedidoDetalleBinding
    private lateinit var adapter: DetalleAdapter
    private lateinit var pedidoDao: PedidoDao

    private var idMesa: Int = 0
    private var numeroMesa: Int = 0
    private var platosDisponibles: List<Plato> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPedidoDetalleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pedidoDao = PedidoDao(this)

        idMesa = intent.getIntExtra("idMesa", 0)
        numeroMesa = intent.getIntExtra("numeroMesa", 0)

        binding.txtMesaTitulo.text = "Mesa $numeroMesa"

        adapter = DetalleAdapter(emptyList())
        binding.rvDetallePedido.layoutManager = LinearLayoutManager(this)
        binding.rvDetallePedido.adapter = adapter

        binding.btnAgregarPlato.setOnClickListener {
            agregarPlato()
        }

        binding.btnVerCuenta.setOnClickListener {
            val i = Intent(this, CuentaActivity::class.java)
            i.putExtra("idMesa", idMesa)
            i.putExtra("numeroMesa", numeroMesa)
            startActivity(i)
        }

        cargarPlatos()
        cargarDetalle()
    }

    override fun onResume() {
        super.onResume()
        cargarPlatos()
        cargarDetalle()
    }

    private fun cargarPlatos() {
        platosDisponibles = pedidoDao.listarPlatosDisponibles()
        val nombres = platosDisponibles.map { "${it.nombre} - S/ %.2f".format(it.precio) }
        val adapterSp = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            nombres
        )
        adapterSp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spPlatos.adapter = adapterSp
    }

    private fun cargarDetalle() {
        val pedido = pedidoDao.obtenerPedidoAbierto(idMesa)
        if (pedido == null) {
            adapter.actualizar(emptyList())
            binding.txtTotalPedido.text = "Total: S/ 0.00"
            binding.txtEstadoPedido.text = "Sin pedido abierto"
        } else {
            val detalles = pedidoDao.listarDetalle(pedido.id)
            adapter.actualizar(detalles)
            binding.txtTotalPedido.text = "Total: S/ %.2f".format(pedido.total)
            binding.txtEstadoPedido.text = "Pedido #${pedido.id} · ${pedido.fecha}"
        }
    }

    private fun agregarPlato() {
        if (platosDisponibles.isEmpty()) {
            Toast.makeText(this, "No hay platos disponibles", Toast.LENGTH_SHORT).show()
            return
        }
        val pos = binding.spPlatos.selectedItemPosition
        if (pos < 0) return
        val plato = platosDisponibles[pos]
        val cantidadTxt = binding.etCantidad.text.toString().trim()
        val cantidad = cantidadTxt.toIntOrNull()
        if (cantidad == null || cantidad <= 0) {
            binding.tilCantidad.error = "Cantidad inválida"
            return
        }
        binding.tilCantidad.error = null

        val ok = pedidoDao.agregarPlato(idMesa, plato.id, cantidad, plato.precio)
        if (ok) {
            Toast.makeText(this, "Plato agregado", Toast.LENGTH_SHORT).show()
            binding.etCantidad.setText("")
            cargarDetalle()
        } else {
            Toast.makeText(this, "Error al agregar", Toast.LENGTH_SHORT).show()
        }
    }
}