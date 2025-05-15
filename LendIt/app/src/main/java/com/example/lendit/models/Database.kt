package com.example.lendit.models

import java.time.LocalDateTime

object Database {
    // Collections
    private val users = mutableMapOf<String, User>()
    private val renters = mutableMapOf<String, Renter>()
    private val owners = mutableMapOf<String, Owner>()
    private val coordinators = mutableMapOf<String, Coordinator>()
    private val listings = mutableMapOf<String, EquipmentListing>()
    private val reviews = mutableMapOf<String, Review>()
    private val reports = mutableMapOf<String, Report>()
    private val premiumPlans = mutableMapOf<String, PremiumPlan>()
    private val carts = mutableMapOf<String, ShoppingCart>()
    private val payments = mutableMapOf<String, Payment>()

    init {
        seedData()
    }

    private fun seedData() {
        // Seed Users
        val users = listOf(
            User(
                userId = "user1",
                name = "John Doe",
                email = "john@example.com",
                password = "encrypted_password1",
                phoneNumber = "+306912345678",
                location = "Athens, Greece"
            ),
            User(
                userId = "user2",
                name = "Jane Smith",
                email = "jane@example.com",
                password = "encrypted_password2",
                phoneNumber = "+306923456789",
                location = "Thessaloniki, Greece"
            ),
            User(
                userId = "coordinator1",
                name = "Admin User",
                email = "admin@lendit.com",
                password = "admin_password",
                phoneNumber = "+306934567890",
                location = "Athens, Greece"
            )
        )

        // Add users to database
        users.forEach { this.users[it.userId] = it }

        // Create Renters and Owners
        val renter1 = Renter(users[0])
        val renter2 = Renter(users[1])
        val owner1 = Owner(users[0], premiumStatus = true, ratings = 4.5f)
        val owner2 = Owner(users[1])
        val coordinator = Coordinator(users[2], "COORD001")

        renters["user1"] = renter1
        renters["user2"] = renter2
        owners["user1"] = owner1
        owners["user2"] = owner2
        coordinators["coordinator1"] = coordinator

        // Seed Equipment Listings
        val sampleListings = listOf(
            EquipmentListing(
                listingId = "listing1",
                title = "Professional Mountain Bike",
                description = "High-end mountain bike perfect for weekend adventures",
                category = "Sports & Outdoors",
                location = "Athens, Greece",
                status = ListingStatus.AVAILABLE,
                price = Price(25.0f),
                photos = listOf(
                    Media("https://example.com/bike1.jpg", "image/jpeg"),
                    Media("https://example.com/bike2.jpg", "image/jpeg")
                ),
                longTermDiscount = 0.15f
            ),
            EquipmentListing(
                listingId = "listing2",
                title = "DSLR Camera Kit",
                description = "Professional camera with multiple lenses",
                category = "Electronics",
                location = "Thessaloniki, Greece",
                status = ListingStatus.AVAILABLE,
                price = Price(40.0f),
                photos = listOf(
                    Media("https://example.com/camera1.jpg", "image/jpeg")
                ),
                longTermDiscount = 0.2f
            )
        )

        sampleListings.forEach { listings[it.listingId] = it }

        // Add some listings to favorites
        renter1.favorites.add(listings["listing2"]!!)
        renter2.favorites.add(listings["listing1"]!!)

        // Seed Reviews
        val sampleReviews = listOf(
            Review(
                reviewId = "review1",
                rating = 5.0f,
                text = "Excellent bike, well maintained!",
                media = listOf(Media("https://example.com/review1.jpg", "image/jpeg")),
                date = LocalDateTime.now().minusDays(5)
            ),
            Review(
                reviewId = "review2",
                rating = 4.5f,
                text = "Great camera, all accessories included",
                date = LocalDateTime.now().minusDays(2)
            )
        )

        sampleReviews.forEach { reviews[it.reviewId] = it }

        // Seed Premium Plans
        val premiumPlanFeatures = listOf(
            Feature("Priority Listing", "Your items appear first in search results"),
            Feature("No Commission", "0% commission on your rentals"),
            Feature("Analytics", "Detailed statistics about your listings")
        )

        val premiumPlan = PremiumPlan(
            planId = "premium1",
            duration = java.time.Duration.ofDays(30),
            price = 29.99f,
            features = premiumPlanFeatures
        )

        premiumPlans[premiumPlan.planId] = premiumPlan

        // Initialize empty shopping carts for users
        users.forEach { (userId, _) ->
            carts[userId] = ShoppingCart(userId)
        }
    }

    // Database operations
    fun getUser(userId: String): User? = users[userId]
    fun getRenter(userId: String): Renter? = renters[userId]
    fun getOwner(userId: String): Owner? = owners[userId]
    fun getCoordinator(userId: String): Coordinator? = coordinators[userId]
    fun getListing(listingId: String): EquipmentListing? = listings[listingId]
    fun getReview(reviewId: String): Review? = reviews[reviewId]
    fun getCart(userId: String): ShoppingCart? = carts[userId]

    // Query operations
    fun searchListings(query: String): List<EquipmentListing> {
        return listings.values.filter { listing ->
            listing.title.contains(query, ignoreCase = true) ||
            listing.description.contains(query, ignoreCase = true) ||
            listing.category.contains(query, ignoreCase = true)
        }
    }

    fun getListingsByCategory(category: String): List<EquipmentListing> {
        return listings.values.filter { it.category == category }
    }

    fun getListingsByLocation(location: String): List<EquipmentListing> {
        return listings.values.filter { it.location == location }
    }

    fun getUserListings(userId: String): List<EquipmentListing> {
        return listings.values.filter { it.listingId.startsWith(userId) }
    }

    fun getUserReviews(userId: String): List<Review> {
        return reviews.values.filter { it.reviewId.startsWith(userId) }
    }

    // Mutation operations
    fun addUser(user: User) {
        users[user.userId] = user
        carts[user.userId] = ShoppingCart(user.userId)
    }

    fun addListing(listing: EquipmentListing) {
        listings[listing.listingId] = listing
    }

    fun addReview(review: Review) {
        reviews[review.reviewId] = review
    }

    fun addReport(report: Report) {
        reports[report.reportId] = report
    }

    fun updateListing(listing: EquipmentListing) {
        listings[listing.listingId] = listing
    }

    fun removeListing(listingId: String) {
        listings.remove(listingId)
    }

    // Helper function to reset database (useful for testing)
    fun reset() {
        users.clear()
        renters.clear()
        owners.clear()
        coordinators.clear()
        listings.clear()
        reviews.clear()
        reports.clear()
        premiumPlans.clear()
        carts.clear()
        payments.clear()
        seedData()
    }
}
