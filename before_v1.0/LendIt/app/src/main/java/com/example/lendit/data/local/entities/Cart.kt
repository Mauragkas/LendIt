package com.example.lendit.data.local.entities

import EquipmentListing
import android.content.Context
import android.widget.Toast
import androidx.room.Entity
import com.example.lendit.CartAdapter
import com.example.lendit.data.repository.CouponRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Entity(primaryKeys = ["userId", "listingId"])
data class UserCart(
    val userId: Int,
    val listingId: Int,
    val startDate: Int,
    val endDate: Int
)

