package com.example.lendit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class Review(
        @PrimaryKey(autoGenerate = true) val reviewId: Int = 0,
        val userId: Int,
        val listingId: Int,
        val rating: Float,
        val comment: String,
        val reviewDate: Long = System.currentTimeMillis(),
        val photos: String? = null // Comma-separated list of photo URIs
)
