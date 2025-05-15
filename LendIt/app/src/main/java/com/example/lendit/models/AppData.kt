package com.example.lendit.models

object AppData {
    var currentUser: User? = null
    var currentRenter: Renter? = null
    var currentOwner: Owner? = null
    var currentCart: ShoppingCart? = null

    fun initializeSampleData() {
        val userId = "user1"
        currentUser = Database.getUser(userId)
        currentRenter = Database.getRenter(userId)
        currentOwner = Database.getOwner(userId)
        currentCart = Database.getCart(userId)
    }

    fun clearData() {
        currentUser = null
        currentRenter = null
        currentOwner = null
        currentCart = null
    }

    fun isUserLoggedIn(): Boolean = currentUser != null

    // Helper functions to access database
    fun getAllListings(): List<EquipmentListing> = Database.searchListings("")
    fun searchListings(query: String): List<EquipmentListing> = Database.searchListings(query)
    fun getListingsByCategory(category: String): List<EquipmentListing> = Database.getListingsByCategory(category)
    fun getUserListings(userId: String): List<EquipmentListing> = Database.getUserListings(userId)
    fun getUserReviews(userId: String): List<Review> = Database.getUserReviews(userId)
}
