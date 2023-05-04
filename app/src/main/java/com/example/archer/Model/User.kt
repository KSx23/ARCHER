package com.example.archer.Model

data class User(
    val id: Int,
    val username: String,
    val password: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val roleId: Int
)
