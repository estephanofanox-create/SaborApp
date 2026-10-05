package com.gutierrez.saborapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gutierrez.saborapp.databinding.ItemMesaBinding
import com.gutierrez.saborapp.model.Mesa

class MesaAdapter(
    private var lista: List<Mesa>,
    private val onClick: (Mesa) -> Unit
) : RecyclerView.Adapter<MesaAdapter.MesaViewHolder>() {

    inner class MesaViewHolder(val binding: ItemMesaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesaViewHolder {
        val binding = ItemMesaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MesaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MesaViewHolder, position: Int) {
        val mesa = lista[position]
        holder.binding.txtNumeroMesa.text = "Mesa ${mesa.numero}"
        holder.binding.txtCapacidad.text = "${mesa.capacidad} pers."
        holder.binding.txtEstado.text = mesa.estado

        if (mesa.estado == "OCUPADA") {
            holder.binding.txtEstado.setTextColor(Color.parseColor("#BF360C"))
            holder.binding.root.setCardBackgroundColor(Color.parseColor("#FFE0B2"))
        } else {
            holder.binding.txtEstado.setTextColor(Color.parseColor("#2E7D32"))
            holder.binding.root.setCardBackgroundColor(Color.WHITE)
        }

        holder.binding.root.setOnClickListener { onClick(mesa) }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizar(nuevaLista: List<Mesa>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}