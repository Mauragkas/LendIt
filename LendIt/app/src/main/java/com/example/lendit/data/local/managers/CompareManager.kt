package com.example.lendit.data.local.managers

import EquipmentListing
import android.content.Context
import android.content.Intent
import com.example.lendit.CompareResultActivity
import com.example.lendit.data.repository.FavoriteRepository
import com.example.lendit.data.repository.ListingRepository
import com.example.lendit.data.repository.RepositoryProvider
import kotlinx.coroutines.Dispatchers

class CompareManager(
    private val context: Context,
    val userId: Int
) {
    // Initialize repositories internally
    private val favoriteRepository: FavoriteRepository = RepositoryProvider.getFavoriteRepository(context)
    private val listingRepository: ListingRepository = RepositoryProvider.getListingRepository(context)

    var favoriteListings: List<EquipmentListing> = emptyList()
        private set

    val selectedListings: MutableList<EquipmentListing> = mutableListOf()


    fun toggleSelection(listing: EquipmentListing, maxSelection: Int = 3): Boolean {
        val exists = selectedListings.any { it.listingId == listing.listingId }
        if (exists) {
            selectedListings.removeIf { it.listingId == listing.listingId }
            return true
        } else {
            if (selectedListings.size < maxSelection) {
                selectedListings.add(listing)
                return true
            }
            return false
        }
    }

    fun getSelectedListingsList(): List<EquipmentListing> = selectedListings.toList()

    fun createCompareIntent(): Intent {
        val listingIds = selectedListings.map { it.listingId }.toIntArray()
        return Intent(context, CompareResultActivity::class.java).apply {
            putExtra("LISTING_IDS", listingIds)
        }
    }

    fun clearSelections() {
        selectedListings.clear()
    }

    suspend fun loadFavorites() {
        val favoriteRecords = favoriteRepository.getFavorites(userId)
        favoriteListings = favoriteRecords.mapNotNull { favorite ->
            listingRepository.getListingById(favorite.listingId)
        }
    }

    companion object {
        // Factory method to create the manager and load favorites
        suspend fun create(context: Context, userId: Int): CompareManager {
            val manager = CompareManager(context, userId)
            manager.loadFavorites()
            return manager
        }
    }
}
