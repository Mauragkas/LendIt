package com.example.lendit.data.local.entities

import UserEntity
import androidx.room.Entity
import androidx.room.PrimaryKey

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
    val endDate: Int
)

class OrderClass(
    val orderId: Int = 0,
    val renter: Int,
    val price: Double,
    val paymentMethod: PaymentMethod,
    val listingId: Int,
    val startDate: Int,
    val endDate: Int
) {
    companion object {
        fun convertToOrderClassList(orders: List<Order>): List<OrderClass> {
            return orders.map { o ->
                OrderClass(
                    orderId = o.orderId,
                    renter = o.renter,
                    price = o.price,
                    paymentMethod = o.paymentMethod,
                    listingId = o.listingId,
                    startDate = o.startDate,
                    endDate = o.endDate
                )
            }
        }

        fun convertToOrderEntityList(orderClasses: List<OrderClass>): List<Order> {
            return orderClasses.map { oc ->
                Order(
                    orderId = oc.orderId,
                    renter = oc.renter,
                    price = oc.price,
                    paymentMethod = oc.paymentMethod,
                    listingId = oc.listingId,
                    startDate = oc.startDate,
                    endDate = oc.endDate
                )
            }
        }

        // Filter orders by payment method
        fun filterOrders(
            orders: List<OrderClass>,
            paymentMethod: PaymentMethod?
        ): List<OrderClass> = when (paymentMethod) {
            PaymentMethod.CASH_ON_DELIVERY -> orders.filter { it.paymentMethod == PaymentMethod.CASH_ON_DELIVERY }
            PaymentMethod.CREDIT_CARD -> orders.filter { it.paymentMethod == PaymentMethod.CREDIT_CARD }
            PaymentMethod.BANK_TRANSFER -> orders.filter { it.paymentMethod == PaymentMethod.BANK_TRANSFER }
            null -> orders
        }

        // Get the most recent order for each renter
        fun getUniqueLatestOrdersByRenter(orders: List<OrderClass>): List<OrderClass> =
            orders.groupBy { it.renter }
                .map { it.value.maxByOrNull { order -> order.orderId }!! }

        // Sort orders by price (descending)
        fun sortOrdersByPrice(orders: List<OrderClass>): List<OrderClass> =
            orders.sortedByDescending { it.price }
    }
}
