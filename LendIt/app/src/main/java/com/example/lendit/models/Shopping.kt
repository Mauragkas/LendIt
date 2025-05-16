package com.example.lendit.models

data class CartItem(
    val listing: EquipmentListing,
    var quantity: Int = 1,
    var rentalDuration: Int // in days
)

data class ShoppingCart(
    val cartId: String,
    val items: MutableList<CartItem> = mutableListOf(),
    var totalAmount: Float = 0f
) {
    fun addItem(item: CartItem): Boolean {
        // Implementation
        return true
    }

    fun removeItem(item: CartItem): Boolean {
        // Implementation
        return true
    }

    fun checkout(): Boolean {
        // Implementation
        return true
    }
}
