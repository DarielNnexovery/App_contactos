package com.example.app_contactos.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_contactos.R
import com.example.app_contactos.adapter.NotaAdapter
import com.example.app_contactos.model.Nota
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotasActivity : AppCompatActivity() {

    private lateinit var toolbarNotas: MaterialToolbar
    private lateinit var rvNotas: RecyclerView
    private lateinit var tvEmptyStateNotas: TextView
    private lateinit var fabAgregarNota: FloatingActionButton
    private lateinit var adapter: NotaAdapter

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (auth.currentUser == null) {
            finish()
            return
        }

        setContentView(R.layout.activity_notas)

        toolbarNotas = findViewById(R.id.toolbarNotas)
        toolbarNotas.subtitle = auth.currentUser?.email
        toolbarNotas.setNavigationOnClickListener {
            finish()
        }

        rvNotas = findViewById(R.id.rvNotas)
        tvEmptyStateNotas = findViewById(R.id.tvEmptyStateNotas)
        fabAgregarNota = findViewById(R.id.fabAgregarNota)

        rvNotas.layoutManager = LinearLayoutManager(this)
        adapter = NotaAdapter(emptyList()) { nota ->
            mostrarDetalleNotaDialog(nota)
        }
        rvNotas.adapter = adapter

        fabAgregarNota.setOnClickListener {
            val intent = Intent(this, AgregarNotaActivity::class.java)
            startActivity(intent)
        }

        cargarNotasDeFirebase()
    }

    private fun cargarNotasDeFirebase() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("notas")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Error al cargar notas: ${error.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                val lista = mutableListOf<Nota>()
                snapshot?.documents?.forEach { doc ->
                    val nota = doc.toObject(Nota::class.java)
                    if (nota != null) {
                        nota.id = doc.id
                        lista.add(nota)
                    }
                }

                // Ordenar notas de la más reciente a la más antigua
                lista.sortByDescending { it.fecha }

                if (lista.isEmpty()) {
                    tvEmptyStateNotas.visibility = View.VISIBLE
                    rvNotas.visibility = View.GONE
                } else {
                    tvEmptyStateNotas.visibility = View.GONE
                    rvNotas.visibility = View.VISIBLE
                    adapter.actualizarLista(lista)
                }
            }
    }

    private fun mostrarDetalleNotaDialog(nota: Nota) {
        val fechaFormateada = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(nota.fecha))

        AlertDialog.Builder(this)
            .setTitle(nota.titulo)
            .setMessage("Fecha: $fechaFormateada\n\n${nota.contenido}")
            .setPositiveButton("Cerrar", null)
            .setNegativeButton("Eliminar") { _, _ ->
                eliminarNota(nota.id)
            }
            .show()
    }

    private fun eliminarNota(notaId: String) {
        if (notaId.isEmpty()) return

        db.collection("notas").document(notaId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Nota eliminada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al eliminar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
