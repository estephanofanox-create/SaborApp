package com.gutierrez.saborapp.model

data class Usuario(
    val id: Int = 0,
    val usuario: String,
    val clave: String,
    val rol: String
)