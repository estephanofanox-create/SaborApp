package com.gutierrez.saborapp.model

data class Pedido(
    val id: Int = 0,
    val idMesa: Int,
    val fecha: String,
    val estado: String,
    val total: Double
)