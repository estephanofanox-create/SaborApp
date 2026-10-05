package com.gutierrez.saborapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gutierrez.saborapp.databinding.ItemPlatoBinding
import com.gutierrez.saborapp.model.Plato

class PlatoAdapter(
    private var lista: List<Plato>,
    private val onClick: (Plato) -> Unit
) : RecyclerView.Adapter<PlatoAdapter.PlatoViewHolder>() {

    inner class PlatoViewHolder(val binding: ItemPlatoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatoViewHolder {
        val binding = ItemPlatoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PlatoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlatoViewHolder, position: Int) {
        val plato = lista[position]
        holder.binding.txtNombre.text = plato.nombre
        holder.binding.txtCategoria.text = plato.categoria
        holder.binding.txtPrecio.text = "S/ %.2f".format(plato.precio)
        holder.binding.txtDisponible.text =
            if (plato.disponible == 1) "Disponible" else "No disponible"
        holder.binding.root.setOnClickListener { onClick(plato) }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizar(nuevaLista: List<Plato>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}