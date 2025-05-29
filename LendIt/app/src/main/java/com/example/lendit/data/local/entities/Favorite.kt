package com.example.lendit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")  // Note: table name is "favorites" (plural)
data class Favorite(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val listingId: Int
)