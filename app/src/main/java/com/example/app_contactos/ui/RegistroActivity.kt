package com.example.app_contactos.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app_contactos.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class RegistroActivity : AppCompatActivity() {

    private lateinit var tilNombre: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout

    private lateinit var etNombre: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText

    private lateinit var btnRegistrar: Button
    private lateinit var tvIrALogin: TextView

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        tilNombre = findViewById(R.id.tilNombre)
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword)

        etNombre = findViewById(R.id.etNombre)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)

        btnRegistrar = findViewById(R.id.btnRegistrar)
        tvIrALogin = findViewById(R.id.tvIrALogin)

        btnRegistrar.setOnClickListener {
            if (validarCampos()) {
                registrarUsuario()
            }
        }

        tvIrALogin.setOnClickListener {
            finish()
        }
    }

    private fun validarCampos(): Boolean {
        var esValido = true
        val nombre = etNombre.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // Validar Nombre
        if (nombre.isEmpty()) {
            tilNombre.error = "Ingresa tu nombre completo"
            esValido = false
        } else {
            tilNombre.error = null
        }

        // Validar Email
        if (email.isEmpty()) {
            tilEmail.error = "El correo es obligatorio"
            esValido = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Ingresa un correo electrónico válido"
            esValido = false
        } else {
            tilEmail.error = null
        }

        // Validar Contraseña
        if (password.isEmpty()) {
            tilPassword.error = "La contraseña es obligatoria"
            esValido = false
        } else if (password.length < 6) {
            tilPassword.error = "La contraseña debe tener al menos 6 caracteres"
            esValido = false
        } else {
            tilPassword.error = null
        }

        // Validar Confirmar Contraseña
        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.error = "Confirma tu contraseña"
            esValido = false
        } else if (confirmPassword != password) {
            tilConfirmPassword.error = "Las contraseñas no coinciden"
            esValido = false
        } else {
            tilConfirmPassword.error = null
        }

        return esValido
    }

    private fun registrarUsuario() {
        btnRegistrar.isEnabled = false
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Toast.makeText(this, "¡Cuenta creada exitosamente!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { error ->
                btnRegistrar.isEnabled = true
                Toast.makeText(this, "Error al registrarse: ${error.message}", Toast.LENGTH_LONG).show()
            }
    }
}
