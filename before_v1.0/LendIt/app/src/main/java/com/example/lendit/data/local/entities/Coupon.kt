package com.example.lendit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coupon")  // Note: table name is "favorites" (plural)
data class Coupon(
    @PrimaryKey val code: String,
    val discountPercentage: Int
)