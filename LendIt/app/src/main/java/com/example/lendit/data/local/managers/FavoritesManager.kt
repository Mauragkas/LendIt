package com.example.lendit.data.local.managers

import EquipmentListing
import android.content.Context
import com.example.lendit.data.repository.RepositoryProvider

class FavoritesManager(private val context: Context) {

    val favoriteRepository by lazy {
        RepositoryProvider.getFavoriteRepository(context)
    }
    val listingRepository by lazy {
        RepositoryProvider.getListingRepository(context)
    }
    var available: List<EquipmentListing>   = emptyList(); private set
    var unavailable: List<EquipmentListing> = emptyList(); private set
    var similar: List<EquipmentListing>     = emptyList(); private set

    suspend fun refresh(userId: Int) {
        val favRecords = favoriteRepository.getFavorites(userId)
        val favorites  = favRecords.mapNotNull { listingRepository.getListingById(it.listingId) }

        available   = favorites.filter { it.status == ListingStatus.AVAILABLE }
        unavailable = favorites.filter { it.status != ListingStatus.AVAILABLE }

        similar = if (favorites.isNotEmpty()) {
            val categories = favorites.map { it.category.name }.distinct()
            listingRepository.getListingsByCategories(categories)
                .filter { it.listingId !in favorites.map { f -> f.listingId } }
                .filter { it.status == ListingStatus.AVAILABLE }
                .take(10)
        } else emptyList()
    }

    val hasFavorites   get() = available.isNotEmpty() || unavailable.isNotEmpty()
    val hasSimilar     get() = similar.isNotEmpty()
}
