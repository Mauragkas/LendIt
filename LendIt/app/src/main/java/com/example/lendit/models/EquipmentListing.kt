package com.example.lendit.models

import java.time.LocalDateTime

enum class ListingStatus {
    AVAILABLE,
    UNAVAILABLE,
    INACTIVE
}

data class Media(
    val url: String,
    val type: String
)

data class Price(
    val amount: Float,
    val currency: String = "EUR"
)

data class EquipmentListing(
    val listingId: String,
    var title: String,
    var description: String,
    var category: String,
    var location: String,
    var status: ListingStatus = ListingStatus.AVAILABLE,
    var price: Price,
    var photos: List<Media> = listOf(),
    val creationDate: LocalDateTime = LocalDateTime.now(),
    var longTermDiscount: Float = 0f
) {
    fun createListing(): Boolean {
        // Implementation
        return true
    }

    fun updateListing(): Boolean {
        // Implementation
        return true
    }

    fun deactivateListing() {
        status = ListingStatus.INACTIVE
    }
}
