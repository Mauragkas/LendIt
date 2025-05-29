package com.example.lendit.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.lendit.data.local.entities.Order

@Dao
interface OrderDao {
    @Insert
    suspend fun insert(orders: Order): Long

    @Query("SELECT * FROM orders")
    suspend fun getAllOrders(): List<Order>

    @Query("SELECT * FROM orders WHERE renter = :renterId")
    suspend fun getOrdersByRenter(renterId: Int): List<Order>

    @Query("SELECT * FROM orders WHERE listingId = :listingId")
    suspend fun getOrdersForListing(listingId: Int): List<Order>
}
