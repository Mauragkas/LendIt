package com.example.lendit.data.repository

import com.example.lendit.data.local.dao.FavoriteDao
import com.example.lendit.data.local.entities.Favorite

class FavoriteRepository(private val favoriteDao: FavoriteDao) {

    suspend fun addToFavorites(favorite: Favorite) {
        favoriteDao.addToFavorites(favorite)
    }

    suspend fun removeFromFavorites(userId: Int, listingId: Int) {
        favoriteDao.removeFromFavorites(userId, listingId)
    }

    suspend fun isFavorite(userId: Int, listingId: Int): Boolean {
        return favoriteDao.isFavorite(userId, listingId)
    }

    suspend fun getFavorites(userId: Int): List<Favorite> {
        return favoriteDao.getFavorites(userId)
    }
}
