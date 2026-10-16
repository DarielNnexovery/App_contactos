package com.example.app_contactos.ui

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app_contactos.R
import com.example.app_contactos.model.Contacto
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AgregarContactoActivity : AppCompatActivity() {

    private lateinit var etNombre: TextInputEditText
    private lateinit var etTelefono: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var btnGuardar: Button
    private lateinit var toolbar: MaterialToolbar

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_contacto)

        toolbar = findViewById(R.id.toolbarAgregar)
        etNombre = findViewById(R.id.etNombre)
        etTelefono = findViewById(R.id.etTelefono)
        etEmail = findViewById(R.id.etEmail)
        btnGuardar = findViewById(R.id.btnGuardar)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val email = etEmail.text.toString().trim()

            if (nombre.isEmpty()) {
                etNombre.error = "Ingresa el nombre"
                return@setOnClickListener
            }

            if (telefono.isEmpty()) {
                etTelefono.error = "Ingresa el teléfono"
                return@setOnClickListener
            }

            guardarEnFirebase(nombre, telefono, email)
        }
    }

    private fun guardarEnFirebase(nombre: String, telefono: String, email: String) {
        val currentUserId = auth.currentUser?.uid ?: ""
        if (currentUserId.isEmpty()) {
            Toast.makeText(this, "Sesión no válida", Toast.LENGTH_SHORT).show()
            return
        }

        btnGuardar.isEnabled = false
        val nuevoContacto = Contacto(
            userId = currentUserId,
            nombre = nombre,
            telefono = telefono,
            email = email
        )

        db.collection("contactos")
            .add(nuevoContacto)
            .addOnSuccessListener {
                Toast.makeText(this, "¡Contacto guardado con éxito!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { error ->
                btnGuardar.isEnabled = true
                Toast.makeText(this, "Error al guardar: ${error.message}", Toast.LENGTH_LONG).show()
            }
    }
}
