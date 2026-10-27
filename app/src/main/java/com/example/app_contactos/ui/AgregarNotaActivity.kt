package com.example.app_contactos.ui

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app_contactos.R
import com.example.app_contactos.model.Nota
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AgregarNotaActivity : AppCompatActivity() {

    private lateinit var etTituloNota: TextInputEditText
    private lateinit var etContenidoNota: TextInputEditText
    private lateinit var btnGuardarNota: Button
    private lateinit var btnCancelarNota: Button
    private lateinit var toolbarAgregarNota: MaterialToolbar

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private var notaIdEdicion: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_nota)

        toolbarAgregarNota = findViewById(R.id.toolbarAgregarNota)
        etTituloNota = findViewById(R.id.etTituloNota)
        etContenidoNota = findViewById(R.id.etContenidoNota)
        btnGuardarNota = findViewById(R.id.btnGuardarNota)
        btnCancelarNota = findViewById(R.id.btnCancelarNota)

        // Verificar si se abrió en modo edición
        notaIdEdicion = intent.getStringExtra("EXTRA_NOTA_ID")
        if (notaIdEdicion != null) {
            toolbarAgregarNota.title = "Editar Nota"
            btnGuardarNota.text = "Actualizar Nota"
            etTituloNota.setText(intent.getStringExtra("EXTRA_NOTA_TITULO"))
            etContenidoNota.setText(intent.getStringExtra("EXTRA_NOTA_CONTENIDO"))
        }

        toolbarAgregarNota.setNavigationOnClickListener {
            finish()
        }

        btnCancelarNota.setOnClickListener {
            finish()
        }

        btnGuardarNota.setOnClickListener {
            val titulo = etTituloNota.text.toString().trim()
            val contenido = etContenidoNota.text.toString().trim()

            if (titulo.isEmpty()) {
                etTituloNota.error = "Ingresa el título de la nota"
                return@setOnClickListener
            }

            if (contenido.isEmpty()) {
                etContenidoNota.error = "Ingresa el contenido de la nota"
                return@setOnClickListener
            }

            if (notaIdEdicion != null) {
                actualizarNotaEnFirebase(notaIdEdicion!!, titulo, contenido)
            } else {
                guardarNotaEnFirebase(titulo, contenido)
            }
        }
    }

    private fun guardarNotaEnFirebase(titulo: String, contenido: String) {
        val currentUserId = auth.currentUser?.uid ?: ""
        if (currentUserId.isEmpty()) {
            Toast.makeText(this, "Sesión no válida", Toast.LENGTH_SHORT).show()
            return
        }

        btnGuardarNota.isEnabled = false
        val nuevaNota = Nota(
            userId = currentUserId,
            titulo = titulo,
            contenido = contenido
        )

        db.collection("notas")
            .add(nuevaNota)
            .addOnSuccessListener {
                Toast.makeText(this, "¡Nota guardada con éxito!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { error ->
                btnGuardarNota.isEnabled = false
                Toast.makeText(this, "Error al guardar nota: ${error.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun actualizarNotaEnFirebase(id: String, titulo: String, contenido: String) {
        btnGuardarNota.isEnabled = false

        val datos = mapOf(
            "titulo" to titulo,
            "contenido" to contenido,
            "fecha" to System.currentTimeMillis()
        )

        db.collection("notas").document(id)
            .update(datos)
            .addOnSuccessListener {
                Toast.makeText(this, "¡Nota actualizada con éxito!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { error ->
                btnGuardarNota.isEnabled = true
                Toast.makeText(this, "Error al actualizar nota: ${error.message}", Toast.LENGTH_LONG).show()
            }
    }
}
