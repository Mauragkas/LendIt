package com.example.lendit.data.local.entities

import androidx.room.Entity

@Entity(primaryKeys = ["userId", "listingId"])
data class UserCart(
    val userId: Int,
    val listingId: Int
)