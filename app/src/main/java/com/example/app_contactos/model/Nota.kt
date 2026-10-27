package com.example.app_contactos.model

data class Nota(
    var id: String = "",
    val userId: String = "",
    val titulo: String = "",
    val contenido: String = "",
    val fecha: Long = System.currentTimeMillis()
)
