package com.example.lendit.data.repository

import android.content.Context
import AppDatabase

/**
 * Singleton that provides access to all repositories in the application.
 * This class ensures we only create one instance of each repository.
 */
object RepositoryProvider {
    private var orderRepository: OrderRepository? = null

    /**
     * Gets or creates the OrderRepository instance
     */
    fun getOrderRepository(context: Context): OrderRepository {
        return orderRepository ?: OrderRepository(AppDatabase.getInstance(context)).also {
            orderRepository = it
        }
    }
}
