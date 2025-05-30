package com.example.lendit.data.local.entities

import EquipmentListing
import android.content.Intent
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lendit.CompareResultActivity
import com.example.lendit.data.repository.ListingRepository
import com.example.lendit.data.repository.FavoriteRepository
import android.content.Context

@Entity(tableName = "favorites")  // Note: table name is "favorites" (plural)
data class Favorite(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val listingId: Int
)


