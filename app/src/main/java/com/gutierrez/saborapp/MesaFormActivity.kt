package com.gutierrez.saborapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gutierrez.saborapp.data.MesaDao
import com.gutierrez.saborapp.databinding.ActivityMesaFormBinding
import com.gutierrez.saborapp.model.Mesa

class MesaFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMesaFormBinding
    private lateinit var mesaDao: MesaDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMesaFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mesaDao = MesaDao(this)

        binding.btnGuardarMesa.setOnClickListener {
            val numeroTxt = binding.etNumero.text.toString().trim()
            val capacidadTxt = binding.etCapacidad.text.toString().trim()

            if (numeroTxt.isEmpty()) {
                binding.tilNumero.error = "Campo obligatorio"
                return@setOnClickListener
            }
            binding.tilNumero.error = null

            if (capacidadTxt.isEmpty()) {
                binding.tilCapacidad.error = "Campo obligatorio"
                return@setOnClickListener
            }
            binding.tilCapacidad.error = null

            val numero = numeroTxt.toIntOrNull()
            val capacidad = capacidadTxt.toIntOrNull()

            if (numero == null || numero <= 0) {
                binding.tilNumero.error = "Número inválido"
                return@setOnClickListener
            }
            if (capacidad == null || capacidad !in 1..12) {
                binding.tilCapacidad.error = "Capacidad inválida"
                return@setOnClickListener
            }

            val mesa = Mesa(numero = numero, capacidad = capacidad, estado = "LIBRE")
            val id = mesaDao.insertar(mesa)
            if (id > 0) {
                Toast.makeText(this, "Mesa registrada", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "La mesa ya existe", Toast.LENGTH_SHORT).show()
            }
        }
    }
}