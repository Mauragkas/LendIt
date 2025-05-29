package com.example.lendit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rentals")
data class Rental(
        @PrimaryKey(autoGenerate = true) val rentalId: Int = 0,
        val userId: Int,
        val listingId: Int,
        val rentalDate: Long = System.currentTimeMillis(),
        val isReviewed: Boolean = false,
        val orderDate: Long = System.currentTimeMillis()
)
