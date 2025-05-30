package com.example.lendit.data.local

import EquipmentListing
import ListingStatus
import AppDatabase
import android.content.Context
import com.example.lendit.data.repository.ListingRepository
import com.example.lendit.data.repository.RepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ListingManager {
    companion object {
        suspend fun updateListingStatus(context: Context, listingId: Int, newStatus: ListingStatus): Boolean {
            val listingRepository by lazy {
                RepositoryProvider.getListingRepository(context)
            }

            return try {
                withContext(Dispatchers.IO) {
                    val listing = listingRepository.getListingById(listingId)

                    if (listing != null) {
                        listing.status = newStatus
                        listingRepository.updateListing(listing)
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
