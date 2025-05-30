package com.example.lendit.data.repository

import AppDatabase
import com.example.lendit.data.local.entities.Order
import com.example.lendit.data.local.entities.OrderClass
import com.example.lendit.data.local.entities.PaymentMethod
import com.example.lendit.data.local.entities.Rental

class OrderRepository(private val db: AppDatabase) {
    suspend fun createOrder(order: Order): Long {
        return db.OrderDao().insert(order)
    }

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

    suspend fun getAllOrders(): List<OrderClass> {
        val orders = db.OrderDao().getAllOrders()
        return OrderClass.convertToOrderClassList(orders)
    }

    suspend fun getOrdersByRenter(renterId: Int): List<OrderClass> {
        val orders = db.OrderDao().getOrdersByRenter(renterId)
        return OrderClass.convertToOrderClassList(orders)
    }

    suspend fun getOrdersForListing(listingId: Int): List<OrderClass> {
        val orders = db.OrderDao().getOrdersForListing(listingId)
        return OrderClass.convertToOrderClassList(orders)
    }

    suspend fun validateCoupon(couponCode: String): Int {
        return db.couponDao().validateCoupon(couponCode)
    }
}
