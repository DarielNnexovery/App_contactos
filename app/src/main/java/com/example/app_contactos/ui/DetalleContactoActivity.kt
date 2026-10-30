package com.example.app_contactos.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.app_contactos.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore

class DetalleContactoActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvInicial: TextView
    private lateinit var tvNombre: TextView
    private lateinit var tvTelefono: TextView
    private lateinit var tvEmail: TextView
    private lateinit var cardEmail: MaterialCardView

    private lateinit var btnLlamar: Button
    private lateinit var btnMensaje: Button
    private lateinit var btnEmail: Button
    private lateinit var btnCompartir: Button
    private lateinit var btnEditar: Button
    private lateinit var btnEliminar: Button

    private val db = FirebaseFirestore.getInstance()

    private var contactoId: String = ""
    private var nombre: String = ""
    private var telefono: String = ""
    private var email: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_contacto)

        toolbar = findViewById(R.id.toolbarDetalleContacto)
        tvInicial = findViewById(R.id.tvInicialDetalle)
        tvNombre = findViewById(R.id.tvNombreDetalle)
        tvTelefono = findViewById(R.id.tvTelefonoDetalle)
        tvEmail = findViewById(R.id.tvEmailDetalle)
        cardEmail = findViewById(R.id.cardEmailDetalle)

        btnLlamar = findViewById(R.id.btnLlamarDetalle)
        btnMensaje = findViewById(R.id.btnMensajeDetalle)
        btnEmail = findViewById(R.id.btnEmailDetalle)
        btnCompartir = findViewById(R.id.btnCompartirDetalle)
        btnEditar = findViewById(R.id.btnEditarDetalle)
        btnEliminar = findViewById(R.id.btnEliminarDetalle)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        contactoId = intent.getStringExtra("EXTRA_CONTACTO_ID") ?: ""
        nombre = intent.getStringExtra("EXTRA_CONTACTO_NOMBRE") ?: ""
        telefono = intent.getStringExtra("EXTRA_CONTACTO_TELEFONO") ?: ""
        email = intent.getStringExtra("EXTRA_CONTACTO_EMAIL") ?: ""

        tvNombre.text = nombre
        tvTelefono.text = telefono
        tvInicial.text = if (nombre.isNotEmpty()) nombre.take(1).uppercase() else "C"

        if (email.isNotEmpty()) {
            cardEmail.visibility = View.VISIBLE
            tvEmail.text = email
        } else {
            cardEmail.visibility = View.GONE
        }

        // Copiar número al mantener presionado
        tvTelefono.setOnLongClickListener {
            copiarAlPortapapeles("Teléfono", telefono)
            true
        }

        // 1. Llamada Telefónica
        btnLlamar.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$telefono"))
            startActivity(intent)
        }

        // 2. Mensaje de Texto (SMS)
        btnMensaje.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$telefono"))
            startActivity(intent)
        }

        // 3. Enviar Correo
        btnEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
            startActivity(intent)
        }

        // 4. Compartir Contacto
        btnCompartir.setOnClickListener {
            val textoContacto = "Contacto: $nombre\nTeléfono: $telefono" + if (email.isNotEmpty()) "\nEmail: $email" else ""
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, textoContacto)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, "Compartir contacto via"))
        }

        // 5. Editar Contacto
        btnEditar.setOnClickListener {
            val intent = Intent(this, AgregarContactoActivity::class.java).apply {
                putExtra("EXTRA_CONTACTO_ID", contactoId)
                putExtra("EXTRA_CONTACTO_NOMBRE", nombre)
                putExtra("EXTRA_CONTACTO_TELEFONO", telefono)
                putExtra("EXTRA_CONTACTO_EMAIL", email)
            }
            startActivity(intent)
            finish()
        }

        // 6. Eliminar Contacto
        btnEliminar.setOnClickListener {
            confirmarEliminar()
        }
    }

    private fun copiarAlPortapapeles(etiqueta: String, texto: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(etiqueta, texto)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "$etiqueta copiado al portapapeles", Toast.LENGTH_SHORT).show()
    }

    private fun confirmarEliminar() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar contacto")
            .setMessage("¿Deseas eliminar a '$nombre'?")
            .setPositiveButton("Sí, eliminar") { _, _ ->
                eliminarContacto()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarContacto() {
        if (contactoId.isEmpty()) return

        db.collection("contactos").document(contactoId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al eliminar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
