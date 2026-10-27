package com.example.app_contactos.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_contactos.R
import com.example.app_contactos.adapter.ContactoAdapter
import com.example.app_contactos.model.Contacto
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var etBuscarContacto: TextInputEditText
    private lateinit var rvContactos: RecyclerView
    private lateinit var tvEmptyState: TextView
    private lateinit var fabAgregarContacto: FloatingActionButton
    private lateinit var adapter: ContactoAdapter

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private var listaContactosCompleta = listOf<Contacto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (auth.currentUser == null) {
            irALogin()
            return
        }

        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        toolbar.subtitle = auth.currentUser?.email
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        etBuscarContacto = findViewById(R.id.etBuscarContacto)
        rvContactos = findViewById(R.id.rvContactos)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        fabAgregarContacto = findViewById(R.id.fabAgregarContacto)

        rvContactos.layoutManager = LinearLayoutManager(this)
        adapter = ContactoAdapter(emptyList()) { contacto ->
            abrirDetalleContacto(contacto)
        }
        rvContactos.adapter = adapter

        fabAgregarContacto.setOnClickListener {
            val intent = Intent(this, AgregarContactoActivity::class.java)
            startActivity(intent)
        }

        etBuscarContacto.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarContactos(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        cargarContactosDeFirebase()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            auth.signOut()
            irALogin()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun irALogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun cargarContactosDeFirebase() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("contactos")
            .whereEqualTo("userId", userId)
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

                lista.sortBy { it.nombre.lowercase() }
                listaContactosCompleta = lista

                filtrarContactos(etBuscarContacto.text.toString())
            }
    }

    private fun filtrarContactos(query: String) {
        val q = query.trim().lowercase()
        val listaFiltrada = if (q.isEmpty()) {
            listaContactosCompleta
        } else {
            listaContactosCompleta.filter {
                it.nombre.lowercase().contains(q) || it.telefono.contains(q)
            }
        }

        if (listaFiltrada.isEmpty()) {
            tvEmptyState.visibility = View.VISIBLE
            rvContactos.visibility = View.GONE
        } else {
            tvEmptyState.visibility = View.GONE
            rvContactos.visibility = View.VISIBLE
            adapter.actualizarLista(listaFiltrada)
        }
    }

    private fun abrirDetalleContacto(contacto: Contacto) {
        val intent = Intent(this, DetalleContactoActivity::class.java).apply {
            putExtra("EXTRA_CONTACTO_ID", contacto.id)
            putExtra("EXTRA_CONTACTO_NOMBRE", contacto.nombre)
            putExtra("EXTRA_CONTACTO_TELEFONO", contacto.telefono)
            putExtra("EXTRA_CONTACTO_EMAIL", contacto.email)
        }
        startActivity(intent)
    }
}
