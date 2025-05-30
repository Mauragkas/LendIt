package com.example.lendit.data.local.managers

import EquipmentListing
import ListingCategory
import ListingFilters
import ListingStatus
import android.content.Context
import com.example.lendit.data.repository.RepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ListingManager(private val context: Context) {

    private val listingRepository by lazy {
        RepositoryProvider.getListingRepository(context)
    }

    suspend fun getListingById(id: Int): EquipmentListing? {
        return listingRepository.getListingById(id)
    }

    suspend fun getAllListings(): List<EquipmentListing> {
        return listingRepository.getAllListings()
    }

    suspend fun addListing(listing: EquipmentListing): Long {
        return listingRepository.addListing(listing)
    }

    suspend fun updateListing(listing: EquipmentListing) {
        return listingRepository.updateListing(listing)
    }

    suspend fun getListingsByOwner(ownerName: String): List<EquipmentListing> {
        return listingRepository.getListingsByOwner(ownerName)
    }

    suspend fun getMostRecentListingByOwner(ownerName: String): EquipmentListing? {
        return listingRepository.getMostRecentListingByOwner(ownerName)
    }

    suspend fun getCategoryById(listingId: Int): String {
        return listingRepository.getCategoryById(listingId)
    }

    suspend fun getRelatedListings(category: String, listingId: Int): List<EquipmentListing> {
        return listingRepository.getRelatedListings(category, listingId)
    }

    suspend fun listingExists(
        title: String,
        description: String,
        price: Double,
        location: String,
        category: String,
        ownerName: String,
        availableFrom: Int?,
        availableUntil: Int?
    ): Boolean {
        return listingRepository.listingExists(
            title, description, price, location,
            category, ownerName, availableFrom, availableUntil
        )
    }

    suspend fun getFilteredListings(filters: ListingFilters): List<EquipmentListing> {
        return listingRepository.getFilteredListings(filters)
    }

    suspend fun updateListingStatus(listingId: Int, newStatus: ListingStatus): Boolean {
        try {
            val listing = listingRepository.getListingById(listingId)

            if (listing != null) {
                listing.status = newStatus
                listingRepository.updateListing(listing)
                return true
            }
            return false
        } catch (e: Exception) {
            return false
        }
    }

    companion object {
        // Static method for backward compatibility
        suspend fun updateListingStatus(context: Context, listingId: Int, newStatus: ListingStatus): Boolean {
            return ListingManager(context).updateListingStatus(listingId, newStatus)
        }
    }
}
