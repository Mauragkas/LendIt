package com.example.lendit

// This class represents a rental item in the UI
data class RentalItem(
        val id: Int,
        val title: String,
        val imageUrl: String,
        val rentalDate: String,
        val ownerName: String
)
