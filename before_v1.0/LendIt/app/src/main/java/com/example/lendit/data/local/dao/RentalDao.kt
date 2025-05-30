package com.example.lendit.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.lendit.data.local.entities.Rental

@Dao
interface RentalDao {
    @Insert suspend fun insert(rental: Rental): Long

    @Update suspend fun update(rental: Rental)

    // Return just the IDs for now
    @Query("SELECT l.listingId FROM listing l INNER JOIN rentals r ON l.listingId = r.listingId WHERE r.userId = :userId AND r.isReviewed = 0")
    suspend fun getUnreviewedRentalsForUser(userId: Int): List<Int>

    @Query("UPDATE rentals SET isReviewed = 1 WHERE userId = :userId AND listingId = :listingId")
    suspend fun markAsReviewed(userId: Int, listingId: Int)

    @Query("SELECT * FROM rentals WHERE userId = :userId AND listingId = :listingId LIMIT 1")
    suspend fun getRental(userId: Int, listingId: Int): Rental?
}