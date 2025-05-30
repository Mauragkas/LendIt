package com.example.lendit.data.repository

import AppDatabase
import buildQuery   // ← adjust if buildQuery lives elsewhere
import EquipmentListing
import ListingFilters   // ← your filters model
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for handling listing-related operations
 */
class ListingRepository(private val db: AppDatabase) {

    /* ────────────────────────────────  CREATE  ─────────────────────────────── */

    suspend fun addListing(listing: EquipmentListing): Long {
        return db.listingDao().insert(listing)
    }

    suspend fun addListings(listings: List<EquipmentListing>){
        db.listingDao().insertAll(listings)
    }

    /* ────────────────────────────────  READ  ──────────────────────────────── */

    suspend fun getAllListings(): List<EquipmentListing> {
        return db.listingDao().getAllListings()
    }

    suspend fun getListingById(id: Int): EquipmentListing? {
        return db.listingDao().getListingById(id)
    }

    suspend fun getCategoryById(listingId: Int): String {
        return db.listingDao().getCategoryById(listingId)
    }

    suspend fun getRelatedListings(category: String, listingId: Int): List<EquipmentListing> {
            return db.listingDao().getRelatedListings(category, listingId)
        }

    suspend fun getListingsByCategories(categories: List<String>): List<EquipmentListing> {
            return db.listingDao().getListingsByCategories(categories)
        }

    suspend fun getListingsByOwner(ownerName: String): List<EquipmentListing> {
           return db.listingDao().getListingsByOwner(ownerName)
        }

    suspend fun getMostRecentListingByOwner(ownerName: String): EquipmentListing? {
            return db.listingDao().getMostRecentListingByOwner(ownerName)
        }

    suspend fun getCount(): Int {
        return db.listingDao().getCount()
    }

    /**
     * Dynamic search with the same rules used in your UI.
     */
    suspend fun getFilteredListings(filters: ListingFilters): List<EquipmentListing> {
            val query = buildQuery(filters)
            return db.listingDao().getListings(query)
        }

    /* ───────────────────────────────  UPDATE  ─────────────────────────────── */

    suspend fun updateListing(listing: EquipmentListing) {
        return db.listingDao().updateListing(listing)
    }

    /* ───────────────────────────────  DELETE  ─────────────────────────────── */

    suspend fun deleteListing(listing: EquipmentListing) {
        return db.listingDao().deleteListing(listing)
    }

    suspend fun deleteAllListings() {
        return db.listingDao().deleteAllListings()
    }

    /* ─────────────────────────────── UTILITIES ────────────────────────────── */

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
        return db.listingDao().listingExists(
            title, description, price, location,
            category, ownerName, availableFrom, availableUntil
        )
    }
}
