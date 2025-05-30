package com.example.lendit.data.repository

import AppDatabase
import com.example.lendit.data.local.entities.Order
import com.example.lendit.data.local.entities.PaymentMethod
import com.example.lendit.data.local.entities.Rental

/**
 * Repository for handling order-related operations
 */
class OrderRepository(private val db: AppDatabase) {

    /**
     * Creates a new order in the database
     */
    suspend fun createOrder(order: Order): Long {
        return db.OrderDao().insert(order)
    }

    /**
     * Creates an order and related rental records from cart items
     */
    suspend fun createOrderFromCart(
        userId: Int,
        listingIds: List<Int>,
        totalPrice: Double,
        paymentMethod: PaymentMethod,
        startDate: Int,
        endDate: Int
    ): Long {
        // Create the order
        val order = Order(
            orderId = 0, // Auto-generated
            renter = userId,
            price = totalPrice,
            paymentMethod = paymentMethod,
            listingId = if (listingIds.isNotEmpty()) listingIds[0] else -1, // Use first listing ID or -1
            startDate = startDate,
            endDate = endDate
        )

        val orderId = db.OrderDao().insert(order)

        // Create rental records for each listing
        listingIds.forEach { listingId ->
            val rental = Rental(
                userId = userId,
                listingId = listingId,
                rentalDate = System.currentTimeMillis(),
                isReviewed = false
            )
            db.rentalDao().insert(rental)
        }

        return orderId
    }

    /**
     * Validates a coupon code
     */
    suspend fun validateCoupon(couponCode: String): Int {
        return db.couponDao().validateCoupon(couponCode)
    }
}
