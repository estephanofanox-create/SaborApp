package com.gutierrez.saborapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.gutierrez.saborapp.data.MesaDao
import com.gutierrez.saborapp.databinding.ActivityPedidoBinding

class PedidoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPedidoBinding
    private lateinit var adapter: MesaAdapter
    private lateinit var mesaDao: MesaDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mesaDao = MesaDao(this)

        adapter = MesaAdapter(emptyList()) { mesa ->
            val i = Intent(this, PedidoDetalleActivity::class.java)
            i.putExtra("idMesa", mesa.id)
            i.putExtra("numeroMesa", mesa.numero)
            startActivity(i)
        }
        binding.rvMesasPedido.layoutManager = GridLayoutManager(this, 3)
        binding.rvMesasPedido.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        adapter.actualizar(mesaDao.listar())
    }
}