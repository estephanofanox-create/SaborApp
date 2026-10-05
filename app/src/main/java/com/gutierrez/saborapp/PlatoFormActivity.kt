package com.gutierrez.saborapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.gutierrez.saborapp.data.PlatoDao
import com.gutierrez.saborapp.databinding.ActivityPlatoFormBinding
import com.gutierrez.saborapp.model.Plato

class PlatoFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlatoFormBinding
    private lateinit var platoDao: PlatoDao

    private val categorias = listOf("Entradas", "Fondos", "Bebidas", "Postres")
    private var platoId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlatoFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        platoDao = PlatoDao(this)

        val adapterCat = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categorias
        )
        adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCategoria.adapter = adapterCat

        platoId = intent.getIntExtra("id", 0)
        if (platoId > 0) {
            val plato = platoDao.obtener(platoId)
            if (plato != null) {
                binding.etNombre.setText(plato.nombre)
                binding.etPrecio.setText(plato.precio.toString())
                binding.swDisponible.isChecked = plato.disponible == 1
                val pos = categorias.indexOf(plato.categoria)
                if (pos >= 0) binding.spCategoria.setSelection(pos)
                binding.btnGuardar.text = "Actualizar"
                binding.btnEliminar.visibility = android.view.View.VISIBLE
            }
        }

        binding.btnGuardar.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val precioTxt = binding.etPrecio.text.toString().trim()
            val categoria = binding.spCategoria.selectedItem.toString()
            val disponible = if (binding.swDisponible.isChecked) 1 else 0

            if (nombre.isEmpty()) {
                binding.tilNombre.error = "Campo obligatorio"
                return@setOnClickListener
            }
            binding.tilNombre.error = null

            val precio = precioTxt.toDoubleOrNull()
            if (precio == null || precio <= 0) {
                binding.tilPrecio.error = "Precio inválido"
                return@setOnClickListener
            }
            binding.tilPrecio.error = null

            if (platoId > 0) {
                val plato = Plato(platoId, nombre, categoria, precio, disponible)
                platoDao.actualizar(plato)
                Toast.makeText(this, "Plato actualizado", Toast.LENGTH_SHORT).show()
            } else {
                val plato = Plato(
                    nombre = nombre,
                    categoria = categoria,
                    precio = precio,
                    disponible = disponible
                )
                val id = platoDao.insertar(plato)
                if (id > 0) {
                    Toast.makeText(this, "Plato registrado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
            finish()
        }

        binding.btnEliminar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminar plato")
                .setMessage("¿Seguro que deseas eliminar este plato?")
                .setPositiveButton("Sí") { _, _ ->
                    if (platoDao.eliminar(platoId)) {
                        Toast.makeText(this, "Plato eliminado", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "No se puede eliminar: tiene pedidos",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }
    }
}