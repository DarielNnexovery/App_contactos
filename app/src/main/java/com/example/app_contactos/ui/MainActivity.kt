package com.example.app_contactos.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_contactos.R
import com.example.app_contactos.adapter.ContactoAdapter
import com.example.app_contactos.model.Contacto
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var rvContactos: RecyclerView
    private lateinit var tvEmptyState: TextView
    private lateinit var fabAgregarContacto: FloatingActionButton
    private lateinit var adapter: ContactoAdapter

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvContactos = findViewById(R.id.rvContactos)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        fabAgregarContacto = findViewById(R.id.fabAgregarContacto)

        rvContactos.layoutManager = LinearLayoutManager(this)
        adapter = ContactoAdapter(emptyList()) { contacto ->
            Toast.makeText(this, "Contacto: ${contacto.nombre}", Toast.LENGTH_SHORT).show()
        }
        rvContactos.adapter = adapter

        // Navegación a la pantalla de Agregar Contacto
        fabAgregarContacto.setOnClickListener {
            val intent = Intent(this, AgregarContactoActivity::class.java)
            startActivity(intent)
        }

        cargarContactosDeFirebase()
    }

    private fun cargarContactosDeFirebase() {
        db.collection("contactos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Error al cargar contactos: ${error.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                val lista = mutableListOf<Contacto>()
                snapshot?.documents?.forEach { doc ->
                    val contacto = doc.toObject(Contacto::class.java)
                    if (contacto != null) {
                        contacto.id = doc.id
                        lista.add(contacto)
                    }
                }

                if (lista.isEmpty()) {
                    tvEmptyState.visibility = View.VISIBLE
                    rvContactos.visibility = View.GONE
                } else {
                    tvEmptyState.visibility = View.GONE
                    rvContactos.visibility = View.VISIBLE
                    adapter.actualizarLista(lista)
                }
            }
    }
}
