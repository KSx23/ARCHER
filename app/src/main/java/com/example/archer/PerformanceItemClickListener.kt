package com.example.archer

import com.example.archer.Model.PerformanceMetric

interface PerformanceItemClickListener {
    fun onEditClick(performanceMetric: PerformanceMetric)
    fun onDeleteClick(performanceMetric: PerformanceMetric)
}
