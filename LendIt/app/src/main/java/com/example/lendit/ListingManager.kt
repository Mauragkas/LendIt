package com.example.lendit.data.local

import EquipmentListing
import ListingStatus
import AppDatabase
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ListingManager {
    companion object {
        suspend fun updateListingStatus(context: Context, listingId: Int, newStatus: ListingStatus): Boolean {
            return try {
                withContext(Dispatchers.IO) {
                    val db = AppDatabase.getInstance(context)
                    val listing = db.listingDao().getListingById(listingId)

                    if (listing != null) {
                        listing.status = newStatus
                        db.listingDao().updateListing(listing)
                        true
                    } else {
                        false
                    }
                }
            } catch (e: Exception) {
                false
            }
        }
    }
}
