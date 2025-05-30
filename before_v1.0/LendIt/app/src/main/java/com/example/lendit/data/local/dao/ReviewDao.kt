package com.example.lendit.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.lendit.data.local.entities.Review

@Dao
interface ReviewDao {
    @Insert
    suspend fun insert(review: Review): Long
    
    @Query("SELECT * FROM reviews WHERE listingId = :listingId")
    suspend fun getReviewsForListing(listingId: Int): List<Review>
    
    @Query("SELECT * FROM reviews WHERE userId = :userId")
    suspend fun getReviewsByUser(userId: Int): List<Review>
    
    @Query("SELECT EXISTS(SELECT 1 FROM reviews WHERE userId = :userId AND listingId = :listingId)")
    suspend fun hasUserReviewedListing(userId: Int, listingId: Int): Boolean
}
