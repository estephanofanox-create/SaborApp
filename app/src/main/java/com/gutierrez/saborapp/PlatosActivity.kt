package com.gutierrez.saborapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.gutierrez.saborapp.data.PlatoDao
import com.gutierrez.saborapp.databinding.ActivityPlatosBinding

class PlatosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlatosBinding
    private lateinit var adapter: PlatoAdapter
    private lateinit var platoDao: PlatoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlatosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        platoDao = PlatoDao(this)

        adapter = PlatoAdapter(emptyList()) { plato ->
            val i = Intent(this, PlatoFormActivity::class.java)
            i.putExtra("id", plato.id)
            startActivity(i)
        }
        binding.rvPlatos.layoutManager = LinearLayoutManager(this)
        binding.rvPlatos.adapter = adapter

        binding.fabAgregar.setOnClickListener {
            startActivity(Intent(this, PlatoFormActivity::class.java))
        }

        binding.etBuscar.doAfterTextChanged {
            cargarLista(it?.toString() ?: "")
        }
    }

    override fun onResume() {
        super.onResume()
        cargarLista(binding.etBuscar.text?.toString() ?: "")
    }

    private fun cargarLista(filtro: String) {
        adapter.actualizar(platoDao.listar(filtro))
    }
}