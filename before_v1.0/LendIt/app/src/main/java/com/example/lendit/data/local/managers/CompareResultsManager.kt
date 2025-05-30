package com.example.lendit.data.local.managers

import EquipmentListing
import com.example.lendit.data.repository.ListingRepository

data class ComparisonResult(
    val summary: String,
    val listings: List<EquipmentListing>
)

class CompareResultsManager {

    suspend fun loadAndCompareListings(
        listingIds: List<Int>,
        listingRepository: ListingRepository
    ): ComparisonResult {
        val listings = mutableListOf<EquipmentListing>()

        // Load listings
        for (id in listingIds) {
            val listing = listingRepository.getListingById(id)
            if (listing != null) {
                listings.add(listing)
            }
        }

        // Check if enough listings to compare
        if (listings.size < 2) {
            return ComparisonResult("Not enough listings to compare.", emptyList())
        }

        // Generate summary or any other comparison logic
        val summary = generateComparisonSummary(listings)

        return ComparisonResult(summary, listings)
    }

    private fun generateComparisonSummary(listings: List<EquipmentListing>): String {
        // Simple placeholder logic for the summary
        return "AI Comparison: Based on your selected items, the ${listings[0].title} " +
                "appears to be more suitable for professional use, while the ${listings[1].title} " +
                "might be better for casual users. The first option has better specs but costs more, " +
                "while the second offers better value for occasional use."
    }
}
