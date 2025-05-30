package com.example.lendit.data.local

import android.content.Context
import com.example.lendit.data.local.entities.Favorite
import com.example.lendit.data.repository.RepositoryProvider

class FavoritesManager(private val context: Context) {

    private val favoriteRepository by lazy {
        RepositoryProvider.getFavoriteRepository(context)
    }

    suspend fun isFavorite(userId: Int, listingId: Int): Boolean {
        return favoriteRepository.isFavorite(userId, listingId)
    }

    suspend fun addToFavorites(userId: Int, listingId: Int) {
        val favorite = Favorite(userId = userId, listingId = listingId)
        favoriteRepository.addToFavorites(favorite)
    }

    suspend fun removeFromFavorites(userId: Int, listingId: Int) {
        favoriteRepository.removeFromFavorites(userId, listingId)
    }

    suspend fun getFavorites(userId: Int): List<Favorite> {
        return favoriteRepository.getFavorites(userId)
    }
}
