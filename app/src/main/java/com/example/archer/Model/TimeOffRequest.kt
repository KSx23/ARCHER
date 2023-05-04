package com.example.archer.Model

data class TimeOffRequest(
    val id: Int,
    val userId: Int,
    val startDate: Long,
    val endDate: Long,
    val status: String
)
