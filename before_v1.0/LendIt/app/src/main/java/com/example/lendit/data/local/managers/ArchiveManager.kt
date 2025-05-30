package com.example.lendit.ui.archive

import EquipmentListing
import android.content.Context
import com.example.lendit.data.repository.ListingRepository
import com.example.lendit.data.repository.RepositoryProvider

class ArchiveManager(context: Context) {
    var availableListings: List<EquipmentListing> = emptyList()
    var unavailableListings: List<EquipmentListing> = emptyList()
    var inactiveListings: List<EquipmentListing> = emptyList()
    private val listingRepository: ListingRepository = RepositoryProvider.getListingRepository(context)

    val availableCount get() = availableListings.size
    val unavailableCount get() = unavailableListings.size
    val inactiveCount get() = inactiveListings.size
    val isEmpty get() = availableCount + unavailableCount + inactiveCount == 0

    /**
     * Loads all listings for owner and partitions them by status.
     */
    suspend fun refresh(ownerName: String) {
        val all = listingRepository.getListingsByOwner(ownerName)

        availableListings   = all.filter { it.status == ListingStatus.AVAILABLE   }
        unavailableListings = all.filter { it.status == ListingStatus.UNAVAILABLE }
        inactiveListings    = all.filter { it.status == ListingStatus.INACTIVE    }
    }

    suspend fun getOwnersListings(ownerName: String): List<EquipmentListing> {
        return listingRepository.getListingsByOwner(ownerName)
    }
}
