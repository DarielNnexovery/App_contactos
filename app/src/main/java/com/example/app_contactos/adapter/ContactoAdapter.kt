package com.example.app_contactos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_contactos.R
import com.example.app_contactos.model.Contacto

class ContactoAdapter(
    private var contactos: List<Contacto>,
    private val onItemClick: (Contacto) -> Unit
) : RecyclerView.Adapter<ContactoAdapter.ContactoViewHolder>() {

    class ContactoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreContacto)
        val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefonoContacto)
        val tvInicial: TextView = itemView.findViewById(R.id.tvInicialContacto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contacto, parent, false)
        return ContactoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactoViewHolder, position: Int) {
        val contacto = contactos[position]
        holder.tvNombre.text = contacto.nombre
        holder.tvTelefono.text = contacto.telefono
        holder.tvInicial.text = if (contacto.nombre.isNotEmpty()) contacto.nombre.take(1).uppercase() else "C"

        holder.itemView.setOnClickListener { onItemClick(contacto) }
    }

    override fun getItemCount(): Int = contactos.size

    fun actualizarLista(nuevaLista: List<Contacto>) {
        contactos = nuevaLista
        notifyDataSetChanged()
    }
}
