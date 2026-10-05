package com.gutierrez.saborapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.gutierrez.saborapp.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val usuario = intent.getStringExtra("usuario") ?: "admin"
        val rol = intent.getStringExtra("rol") ?: "ADMIN"

        binding.txtHola.text = "Hola, $usuario"
        binding.txtRol.text = if (rol == "ADMIN") "Rol: Administrador" else "Rol: Mozo"

        if (rol == "MOZO") {
            binding.cardReportes.visibility = View.GONE
        }

        binding.cardPlatos.setOnClickListener {
            startActivity(Intent(this, PlatosActivity::class.java))
        }
        binding.cardMesas.setOnClickListener {
            startActivity(Intent(this, MesasActivity::class.java))
        }
        binding.cardPedidos.setOnClickListener {
            startActivity(Intent(this, PedidoActivity::class.java))
        }
        binding.cardReportes.setOnClickListener {
            startActivity(Intent(this, ReportesActivity::class.java))
        }
        binding.btnSalir.setOnClickListener {
            getSharedPreferences(LoginActivity.PREFS, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}