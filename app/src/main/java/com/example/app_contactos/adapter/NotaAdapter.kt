package com.example.app_contactos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_contactos.R
import com.example.app_contactos.model.Nota
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotaAdapter(
    private var notas: List<Nota>,
    private val onItemClick: (Nota) -> Unit
) : RecyclerView.Adapter<NotaAdapter.NotaViewHolder>() {

    class NotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTituloNota)
        val tvContenido: TextView = itemView.findViewById(R.id.tvContenidoNota)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFechaNota)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_nota, parent, false)
        return NotaViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotaViewHolder, position: Int) {
        val nota = notas[position]
        holder.tvTitulo.text = nota.titulo
        holder.tvContenido.text = nota.contenido

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.tvFecha.text = sdf.format(Date(nota.fecha))

        holder.itemView.setOnClickListener { onItemClick(nota) }
    }

    override fun getItemCount(): Int = notas.size

    fun actualizarLista(nuevaLista: List<Nota>) {
        notas = nuevaLista
        notifyDataSetChanged()
    }
}
