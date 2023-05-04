package com.example.archer.Model

data class Shift(
    val id: Int,
    var userId: Int,
    val startTime: Double,
    val endTime: Double,
    val location: String,
    var roleId: Int
)
