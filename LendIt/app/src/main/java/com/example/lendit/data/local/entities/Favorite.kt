package com.example.lendit.data.local.entities

import EquipmentListing
import android.content.Intent
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lendit.CompareResultActivity
import com.example.lendit.data.repository.ListingRepository
import com.example.lendit.data.repository.FavoriteRepository
import android.content.Context

@Entity(tableName = "favorites")  // Note: table name is "favorites" (plural)
data class Favorite(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val listingId: Int
)

class CompareManager(
    val userId: Int,
    val favoriteListings: List<EquipmentListing>,
    val selectedListings: MutableList<EquipmentListing> = mutableListOf()
) {

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

    fun createCompareIntent(context: Context): Intent {
        val listingIds = selectedListings.map { it.listingId }.toIntArray()
        return Intent(context, CompareResultActivity::class.java).apply {
            putExtra("LISTING_IDS", listingIds)
        }
    }
    fun clearSelections() {
        selectedListings.clear()
    }

    companion object {
        suspend fun loadFavorites(
            userId: Int,
            favoriteRepository: FavoriteRepository,
            listingRepository: ListingRepository
        ): CompareManager {
            val favoriteRecords = favoriteRepository.getFavorites(userId)
            val listings = favoriteRecords.mapNotNull { favorite ->
                listingRepository.getListingById(favorite.listingId)
            }
            return CompareManager(userId, listings)
        }
    }
}

