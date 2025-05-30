package com.example.lendit.data.repository

import AppDatabase
import com.example.lendit.data.local.entities.Rental
import com.example.lendit.data.local.entities.Review

/**
 * Repository for handling review-related operations
 */
class ReviewRepository(private val db: AppDatabase) {

    /**
     * Inserts a new review into the database
     */
    suspend fun addReview(review: Review): Long {
        return db.reviewDao().insert(review)
    }

    /**
     * Gets all reviews for a specific listing
     */
    suspend fun getReviewsForListing(listingId: Int): List<Review> {
        return db.reviewDao().getReviewsForListing(listingId)
    }

    /**
     * Gets all reviews written by a specific user
     */
    suspend fun getReviewsByUser(userId: Int): List<Review> {
        return db.reviewDao().getReviewsByUser(userId)
    }

    /**
     * Checks if a user has already reviewed a listing
     */
    suspend fun hasUserReviewedListing(userId: Int, listingId: Int): Boolean {
        return db.reviewDao().hasUserReviewedListing(userId, listingId)
    }

    /**
     * Gets a list of listing IDs the user rented but hasn't reviewed yet
     */
    suspend fun getUnreviewedRentalListingIds(userId: Int): List<Int> {
        return db.rentalDao().getUnreviewedRentalsForUser(userId)
    }

    /**
     * Marks a rental as reviewed for a user and listing
     */
    suspend fun markRentalAsReviewed(userId: Int, listingId: Int) {
        return db.rentalDao().markAsReviewed(userId, listingId)
    }

    /**
     * Gets the rental record for a specific user and listing
     */
    suspend fun getRental(userId: Int, listingId: Int): Rental? {
        return db.rentalDao().getRental(userId, listingId)
    }
}
