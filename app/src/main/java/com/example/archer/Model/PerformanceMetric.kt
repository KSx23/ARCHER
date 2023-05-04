package com.example.archer.Model

data class PerformanceMetric(
    val id: Int,
    val userId: Int,
    var metricName: String,
    var metricValue: Double,
    var goalValue: Double
)

