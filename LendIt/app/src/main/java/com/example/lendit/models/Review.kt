package com.example.lendit.models

import java.time.LocalDateTime

data class Review(
    val reviewId: String,
    val rating: Float,
    val text: String,
    val media: List<Media> = listOf(),
    val date: LocalDateTime = LocalDateTime.now()
) {
    fun submitReview(): Boolean {
        // Implementation
        return true
    }
}
