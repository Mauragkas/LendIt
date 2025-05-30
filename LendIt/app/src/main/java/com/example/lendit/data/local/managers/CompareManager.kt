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
    val userId: Int,
    val favoriteListings: List<EquipmentListing>,
    val selectedListings: MutableList<EquipmentListing> = mutableListOf()
) {
    // Initialize repositories internally
    val favoriteRepository = RepositoryProvider.getFavoriteRepository(context)
    val listingRepository = RepositoryProvider.getListingRepository(context)

    val favoriteRecords = favoriteRepository.getFavorites(userId)
    val listings = favoriteRecords.mapNotNull { favorite ->
        listingRepository.getListingById(favorite.listingId)
    }

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

    companion object {
        suspend fun loadFavorites(context: Context, userId: Int): CompareManager {

            return CompareManager(context, userId, listings)
        }
    }
}
