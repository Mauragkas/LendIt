package com.example.lendit.data.local.entities

import UserEntity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")  // Note: table name is "favorites" (plural)
data class Order(
    @PrimaryKey(autoGenerate = true) val orderId: Int,
    val renter: Int,
    val price: Int,
    val paymentMethod: Int,

    // if method is card (in production would be encrypted)
    val cardNumber: Int,
    val cvv: Int,
    val cardHolderName: String,

    // if method is bank transfer
    val iban: String
)