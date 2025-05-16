package com.example.lendit.models

import java.time.Duration

data class Feature(
    val name: String,
    val description: String
)

data class PremiumPlan(
    val planId: String,
    val duration: Duration,
    val price: Float,
    val features: List<Feature>
) {
    fun subscribe(): Boolean {
        // Implementation
        return true
    }

    fun cancel(): Boolean {
        // Implementation
        return true
    }
}
