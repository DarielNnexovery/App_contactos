package com.example.app_contactos.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.app_contactos.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var toolbarHome: MaterialToolbar
    private lateinit var tvBienvenida: TextView
    private lateinit var cardContactos: MaterialCardView
    private lateinit var cardNotas: MaterialCardView
    private lateinit var btnIrContactos: Button
    private lateinit var btnIrNotas: Button

    private val auth = FirebaseAuth.getInstance()

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
        cardContactos = findViewById(R.id.cardContactos)
        cardNotas = findViewById(R.id.cardNotas)
        btnIrContactos = findViewById(R.id.btnIrContactos)
        btnIrNotas = findViewById(R.id.btnIrNotas)

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
}
