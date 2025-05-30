package com.example.lendit.data.local.entities

import UserEntity
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class PaymentMethod {
    CASH_ON_DELIVERY,
    CREDIT_CARD,
    BANK_TRANSFER
}

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val orderId: Int = 0,
    val renter: Int,
    val price: Double,
    val paymentMethod: PaymentMethod,
    val listingId: Int,
    val startDate: Int,
    val endDate: Int,
    val status: String = "PENDING",
    val orderDate: Long = System.currentTimeMillis()
)

class OrderClass {
    companion object {
        // Convert Order entities to a formatted string for display
        fun formatOrderDetails(order: Order, renterName: String = "", listingTitle: String = ""): String {
            val sb = StringBuilder()
            sb.append("Order #${order.orderId}\n")
            if (renterName.isNotEmpty()) {
                sb.append("Renter: $renterName\n")
            }
            if (listingTitle.isNotEmpty()) {
                sb.append("Item: $listingTitle\n")
            }
            sb.append("Price: ${String.format("%.2f€", order.price)}\n")
            sb.append("Payment Method: ${formatPaymentMethod(order.paymentMethod)}\n")
            sb.append("Status: ${order.status}\n")

            // Format date
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dateString = sdf.format(Date(order.orderDate))
            sb.append("Order Date: $dateString\n")

            // Calculate rental duration
            val rentalDays = calculateRentalDuration(order.startDate, order.endDate)
            sb.append("Rental Duration: $rentalDays days\n")

            return sb.toString()
        }

        // Format payment method for display
        fun formatPaymentMethod(method: PaymentMethod): String {
            return when (method) {
                PaymentMethod.CASH_ON_DELIVERY -> "Cash on Delivery"
                PaymentMethod.CREDIT_CARD -> "Credit Card"
                PaymentMethod.BANK_TRANSFER -> "Bank Transfer"
            }
        }

        // Calculate rental duration in days
        fun calculateRentalDuration(startDate: Int, endDate: Int): Int {
            // This is a simplified calculation. You might need to adapt based on how your dates are stored
            return endDate - startDate
        }

        // Filter orders by status
        fun filterOrdersByStatus(orders: List<Order>, status: String?): List<Order> {
            return if (status == null) orders else orders.filter { it.status == status }
        }

        // Filter orders by payment method
        fun filterOrdersByPaymentMethod(orders: List<Order>, paymentMethod: PaymentMethod?): List<Order> {
            return if (paymentMethod == null) orders else orders.filter { it.paymentMethod == paymentMethod }
        }

        // Filter orders by price range
        fun filterOrdersByPriceRange(orders: List<Order>, minPrice: Double?, maxPrice: Double?): List<Order> {
            var filtered = orders

            minPrice?.let { min ->
                filtered = filtered.filter { it.price >= min }
            }

            maxPrice?.let { max ->
                filtered = filtered.filter { it.price <= max }
            }

            return filtered
        }

        // Sort orders by price
        fun sortOrdersByPrice(orders: List<Order>, ascending: Boolean = false): List<Order> {
            return if (ascending) orders.sortedBy { it.price } else orders.sortedByDescending { it.price }
        }

        // Sort orders by date
        fun sortOrdersByDate(orders: List<Order>, ascending: Boolean = false): List<Order> {
            return if (ascending) orders.sortedBy { it.orderDate } else orders.sortedByDescending { it.orderDate }
        }

        // Calculate total revenue from a list of orders
        fun calculateTotalRevenue(orders: List<Order>): Double {
            return orders.sumOf { it.price }
        }

        // Calculate average order value
        fun calculateAverageOrderValue(orders: List<Order>): Double {
            return if (orders.isNotEmpty()) orders.sumOf { it.price } / orders.size else 0.0
        }

        // Get top spenders (renters who spent the most)
        fun getTopSpenders(orders: List<Order>, limit: Int = 5): Map<Int, Double> {
            return orders.groupBy { it.renter }
                .mapValues { it.value.sumOf { order -> order.price } }
                .toList()
                .sortedByDescending { it.second }
                .take(limit)
                .toMap()
        }

        // Get most popular listings (most frequently ordered)
        fun getMostPopularListings(orders: List<Order>, limit: Int = 5): Map<Int, Int> {
            return orders.groupBy { it.listingId }
                .mapValues { it.value.size }
                .toList()
                .sortedByDescending { it.second }
                .take(limit)
                .toMap()
        }
    }
}
