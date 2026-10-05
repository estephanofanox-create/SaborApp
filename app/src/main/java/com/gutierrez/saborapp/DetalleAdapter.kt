package com.gutierrez.saborapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gutierrez.saborapp.databinding.ItemDetalleBinding
import com.gutierrez.saborapp.model.DetallePedido

class DetalleAdapter(
    private var lista: List<DetallePedido>
) : RecyclerView.Adapter<DetalleAdapter.DetalleViewHolder>() {

    inner class DetalleViewHolder(val binding: ItemDetalleBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetalleViewHolder {
        val binding = ItemDetalleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DetalleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetalleViewHolder, position: Int) {
        val d = lista[position]
        holder.binding.txtDetalleNombre.text = d.nombrePlato
        holder.binding.txtDetalleCantidad.text = "x${d.cantidad}  S/ %.2f".format(d.precioUnit)
        holder.binding.txtDetalleSubtotal.text = "S/ %.2f".format(d.subtotal)
    }

    override fun getItemCount(): Int = lista.size

    fun actualizar(nuevaLista: List<DetallePedido>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}