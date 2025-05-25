package com.example.lendit.data.local.dao

import androidx.room.*
import com.example.lendit.data.local.entities.Favorite

@Dao
interface FavoriteDao {
    @Insert
    suspend fun addToFavorites(favorite: Favorite)

    @Query("DELETE FROM favorites WHERE userId = :userId AND listingId = :listingId")
    suspend fun removeFromFavorites(userId: Int, listingId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND listingId = :listingId)")
    suspend fun isFavorite(userId: Int, listingId: Int): Boolean

    @Query("SELECT * FROM favorites WHERE userId = :userId")
    suspend fun getFavorites(userId: Int): List<Favorite>
}