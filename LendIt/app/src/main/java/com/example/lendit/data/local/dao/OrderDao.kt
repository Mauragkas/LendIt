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
}