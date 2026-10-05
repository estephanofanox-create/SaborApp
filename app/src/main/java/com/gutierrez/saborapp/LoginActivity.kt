package com.gutierrez.saborapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gutierrez.saborapp.data.UsuarioDao
import com.gutierrez.saborapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    companion object {
        const val PREFS = "saborapp_prefs"
        const val KEY_USUARIO = "usuario"
        const val KEY_ROL = "rol"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Si ya hay sesión guardada, ir directo al menú
        val prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val usuarioGuardado = prefs.getString(KEY_USUARIO, null)
        val rolGuardado = prefs.getString(KEY_ROL, null)
        if (usuarioGuardado != null && rolGuardado != null) {
            irAlMenu(usuarioGuardado, rolGuardado)
            return
        }

        val usuarioDao = UsuarioDao(this)

        binding.btnIngresar.setOnClickListener {
            val usuario = binding.etUsuario.text.toString().trim()
            val clave = binding.etClave.text.toString().trim()

            binding.tilUsuario.error = null
            binding.tilClave.error = null

            if (usuario.isEmpty()) {
                binding.tilUsuario.error = getString(R.string.login_error_vacio)
                return@setOnClickListener
            }
            if (clave.isEmpty()) {
                binding.tilClave.error = getString(R.string.login_error_vacio)
                return@setOnClickListener
            }

            val u = usuarioDao.validarUsuario(usuario, clave)
            if (u != null) {
                prefs.edit()
                    .putString(KEY_USUARIO, u.usuario)
                    .putString(KEY_ROL, u.rol)
                    .apply()
                irAlMenu(u.usuario, u.rol)
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.login_error_credenciales),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun irAlMenu(usuario: String, rol: String) {
        val i = Intent(this, MenuActivity::class.java)
        i.putExtra("usuario", usuario)
        i.putExtra("rol", rol)
        startActivity(i)
        finish()
    }
}