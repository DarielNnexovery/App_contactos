package com.example.app_contactos.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.app_contactos.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var toolbarHome: MaterialToolbar
    private lateinit var tvBienvenida: TextView
    private lateinit var tvCountContactos: TextView
    private lateinit var tvCountNotas: TextView
    private lateinit var cardContactos: MaterialCardView
    private lateinit var cardNotas: MaterialCardView
    private lateinit var btnIrContactos: Button
    private lateinit var btnIrNotas: Button
    private lateinit var btnCerrarSesion: MaterialButton

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (auth.currentUser == null) {
            irALogin()
            return
        }

        setContentView(R.layout.activity_home)

        toolbarHome = findViewById(R.id.toolbarHome)
        toolbarHome.subtitle = auth.currentUser?.email
        setSupportActionBar(toolbarHome)

        tvBienvenida = findViewById(R.id.tvBienvenida)
        tvCountContactos = findViewById(R.id.tvCountContactos)
        tvCountNotas = findViewById(R.id.tvCountNotas)
        cardContactos = findViewById(R.id.cardContactos)
        cardNotas = findViewById(R.id.cardNotas)
        btnIrContactos = findViewById(R.id.btnIrContactos)
        btnIrNotas = findViewById(R.id.btnIrNotas)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

        val email = auth.currentUser?.email ?: ""
        val usuario = if (email.contains("@")) email.substringBefore("@") else email
        if (usuario.isNotEmpty()) {
            tvBienvenida.text = "¡Hola, $usuario!"
        }

        val abrirContactos = {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        cardContactos.setOnClickListener { abrirContactos() }
        btnIrContactos.setOnClickListener { abrirContactos() }

        val abrirNotas = {
            val intent = Intent(this, NotasActivity::class.java)
            startActivity(intent)
        }
        cardNotas.setOnClickListener { abrirNotas() }
        btnIrNotas.setOnClickListener { abrirNotas() }

        btnCerrarSesion.setOnClickListener {
            confirmarCerrarSesion()
        }

        cargarContadores()
    }

    private fun cargarContadores() {
        val userId = auth.currentUser?.uid ?: return

        // Contador de Contactos
        db.collection("contactos")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, _ ->
                val count = snapshot?.size() ?: 0
                tvCountContactos.text = if (count == 1) "1 contacto guardado" else "$count contactos guardados"
            }

        // Contador de Notas
        db.collection("notas")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, _ ->
                val count = snapshot?.size() ?: 0
                tvCountNotas.text = if (count == 1) "1 nota guardada" else "$count notas guardadas"
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            confirmarCerrarSesion()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmarCerrarSesion() {
        AlertDialog.Builder(this)
            .setTitle(R.string.confirmar_cerrar_sesion_titulo)
            .setMessage(R.string.confirmar_cerrar_sesion_mensaje)
            .setPositiveButton(R.string.cerrar_sesion) { _, _ ->
                cerrarSesion()
            }
            .setNegativeButton(R.string.cancelar, null)
            .show()
    }

    private fun cerrarSesion() {
        auth.signOut()
        Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
        irALogin()
    }

    private fun irALogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}