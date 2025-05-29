package com.example.lendit.data.local.entities

import UserEntity
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PaymentMethod {
    CASH_ON_DELIVERY,
    CREDIT_CARD,
    BANK_TRANSFER
}


@Entity(tableName = "orders")  // Note: table name is "favorites" (plural)
data class Order(
    @PrimaryKey(autoGenerate = true) val orderId: Int,
    val renter: Int,
    val price: Double,
    val paymentMethod: PaymentMethod,
    val listingId: Int,
    val startDate: Int,
    val endDate: Int
)