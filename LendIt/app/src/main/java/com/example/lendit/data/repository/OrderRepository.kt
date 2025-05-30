package com.example.lendit.data.repository

import AppDatabase
import com.example.lendit.data.local.entities.Order
import com.example.lendit.data.local.entities.OrderOperations
import com.example.lendit.data.local.entities.PaymentMethod
import com.example.lendit.data.local.entities.Rental

class OrderRepository(private val db: AppDatabase) {
    private val orderOperations = OrderOperations()

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
        val order = Order(
            orderId = 0,
            renter = userId,
            price = totalPrice,
            paymentMethod = paymentMethod,
            listingId = if (listingIds.isNotEmpty()) listingIds[0] else -1,
            startDate = startDate,
            endDate = endDate,
            status = "PENDING",
            orderDate = System.currentTimeMillis()
        )

        val orderId = db.OrderDao().insert(order)

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

    suspend fun getAllOrders(): List<Order> {
        return db.OrderDao().getAllOrders()
    }

    suspend fun getAllOrdersSortedByPrice(ascending: Boolean = false): List<Order> {
        val orders = db.OrderDao().getAllOrders()
        return orderOperations.sortOrdersByPrice(orders, ascending)
    }

    suspend fun getAllOrdersSortedByDate(ascending: Boolean = false): List<Order> {
        val orders = db.OrderDao().getAllOrders()
        return orderOperations.sortOrdersByDate(orders, ascending)
    }

    suspend fun getOrdersFilteredByStatus(status: String?): List<Order> {
        val orders = db.OrderDao().getAllOrders()
        return orderOperations.filterOrdersByStatus(orders, status)
    }

    suspend fun getOrdersFilteredByPaymentMethod(paymentMethod: PaymentMethod?): List<Order> {
        val orders = db.OrderDao().getAllOrders()
        return orderOperations.filterOrdersByPaymentMethod(orders, paymentMethod)
    }

    suspend fun getOrdersFilteredByPriceRange(minPrice: Double?, maxPrice: Double?): List<Order> {
        val orders = db.OrderDao().getAllOrders()
        return orderOperations.filterOrdersByPriceRange(orders, minPrice, maxPrice)
    }

    suspend fun getOrdersByRenter(renterId: Int): List<Order> {
        return db.OrderDao().getOrdersByRenter(renterId)
    }

    suspend fun getOrdersForListing(listingId: Int): List<Order> {
        return db.OrderDao().getOrdersForListing(listingId)
    }

    suspend fun getOrderById(orderId: Int): Order? {
        return db.OrderDao().getOrderById(orderId)
    }

    suspend fun getFormattedOrderDetails(orderId: Int): String {
        val order = db.OrderDao().getOrderById(orderId) ?: return "Order not found"
        val renter = db.userDao().getUserById(order.renter)
        val renterName = renter?.name ?: "Unknown User"
        val listing = db.listingDao().getListingById(order.listingId)
        val listingTitle = listing?.title ?: "Unknown Listing"
        return order.formatOrderDetails(renterName, listingTitle)
    }

    suspend fun calculateTotalRevenue(): Double {
        val orders = db.OrderDao().getAllOrders()
        return orderOperations.calculateTotalRevenue(orders)
    }

    suspend fun calculateAverageOrderValue(): Double {
        val orders = db.OrderDao().getAllOrders()
        return orderOperations.calculateAverageOrderValue(orders)
    }

    suspend fun getTopSpenders(limit: Int = 5): Map<Int, Double> {
        val orders = db.OrderDao().getAllOrders()
        return orderOperations.getTopSpenders(orders, limit)
    }

    suspend fun getMostPopularListings(limit: Int = 5): Map<Int, Int> {
        val orders = db.OrderDao().getAllOrders()
        return orderOperations.getMostPopularListings(orders, limit)
    }

    suspend fun validateCoupon(couponCode: String): Int {
        return db.couponDao().validateCoupon(couponCode)
    }
}
