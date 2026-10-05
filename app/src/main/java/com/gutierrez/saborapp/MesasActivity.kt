package com.gutierrez.saborapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.gutierrez.saborapp.data.MesaDao
import com.gutierrez.saborapp.databinding.ActivityMesasBinding

class MesasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMesasBinding
    private lateinit var adapter: MesaAdapter
    private lateinit var mesaDao: MesaDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMesasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mesaDao = MesaDao(this)

        adapter = MesaAdapter(emptyList()) { _ -> }
        binding.rvMesas.layoutManager = GridLayoutManager(this, 3)
        binding.rvMesas.adapter = adapter

        binding.fabAgregarMesa.setOnClickListener {
            startActivity(Intent(this, MesaFormActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.actualizar(mesaDao.listar())
    }
}