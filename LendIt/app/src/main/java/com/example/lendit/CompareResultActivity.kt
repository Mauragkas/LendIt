package com.example.lendit

import EquipmentListing
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.lendit.databinding.ActivityCompareResultBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CompareResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompareResultBinding
    private var listings = mutableListOf<EquipmentListing>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompareResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up back button
        binding.backButton.setOnClickListener { finish() }

        // Get the listing IDs from the intent
        val listingIds = intent.getIntArrayExtra("LISTING_IDS") ?: return

        // Load the listings
        lifecycleScope.launch { loadListings(listingIds.toList()) }
    }

    private suspend fun loadListings(listingIds: List<Int>) {
        try {
            val db = AppDatabase.getInstance(this)

            // Load each listing - explicitly specifying the return type
            listings =
                    withContext(Dispatchers.IO) {
                        val resultList = mutableListOf<EquipmentListing>()
                        for (id in listingIds) {
                            val listing = db.listingDao().getListingById(id)
                            if (listing != null) {
                                resultList.add(listing)
                            }
                        }
                        resultList
                    }

            if (listings.size < 2) {
                binding.errorMessage.visibility = View.VISIBLE
                binding.errorMessage.text = "Not enough listings to compare"
                binding.comparisonContent.visibility = View.GONE
                return
            }

            displayComparison()
        } catch (e: Exception) {
            binding.errorMessage.visibility = View.VISIBLE
            binding.errorMessage.text = "Error loading listings: ${e.message}"
            binding.comparisonContent.visibility = View.GONE
        }
    }

    private fun displayComparison() {
        // Make sure the content is visible
        binding.errorMessage.visibility = View.GONE
        binding.comparisonContent.visibility = View.VISIBLE

        // Display first listing
        val listing1 = listings[0]
        binding.title1.text = listing1.title
        binding.price1.text = "${listing1.price}€"
        binding.category1.text = listing1.category.toString()
        binding.location1.text = listing1.location.toString()
        binding.description1.text = listing1.description
        binding.viewButton1.setOnClickListener { openListingDetails(listing1.listingId) }

        // Use the photos string directly without Converters
        val photoList1 = listing1.photos.split(",").filter { it.isNotEmpty() }
        if (photoList1.isNotEmpty()) {
            Glide.with(this)
                    .load(photoList1[0])
                    .apply(RequestOptions().centerCrop())
                    .into(binding.image1)
        }

        // Display second listing
        val listing2 = listings[1]
        binding.title2.text = listing2.title
        binding.price2.text = "${listing2.price}€"
        binding.category2.text = listing2.category.toString()
        binding.location2.text = listing2.location.toString()
        binding.description2.text = listing2.description
        binding.viewButton2.setOnClickListener { openListingDetails(listing2.listingId) }

        val photoList2 = listing2.photos.split(",").filter { it.isNotEmpty() }
        if (photoList2.isNotEmpty()) {
            Glide.with(this)
                    .load(photoList2[0])
                    .apply(RequestOptions().centerCrop())
                    .into(binding.image2)
        }

        // Hide third column if there's no third listing
        if (listings.size < 3) {
            binding.column3.visibility = View.GONE
        } else {
            binding.column3.visibility = View.VISIBLE

            // Display third listing
            val listing3 = listings[2]
            binding.title3.text = listing3.title
            binding.price3.text = "${listing3.price}€"
            binding.category3.text = listing3.category.toString()
            binding.location3.text = listing3.location.toString()
            binding.description3.text = listing3.description
            binding.viewButton3.setOnClickListener { openListingDetails(listing3.listingId) }

            val photoList3 = listing3.photos.split(",").filter { it.isNotEmpty() }
            if (photoList3.isNotEmpty()) {
                Glide.with(this)
                        .load(photoList3[0])
                        .apply(RequestOptions().centerCrop())
                        .into(binding.image3)
            }
        }

        // For now, just show a placeholder AI comparison text
        binding.aiComparisonText.text =
                "AI Comparison: Based on your selected items, the ${listings[0].title} " +
                        "appears to be more suitable for professional use, while the ${listings[1].title} " +
                        "might be better for casual users. The first option has better specifications but " +
                        "costs more, while the second option offers better value for money for occasional use."
    }

    private fun openListingDetails(listingId: Int) {
        val intent = Intent(this, ListingDetailsActivity::class.java)
        intent.putExtra("listing_id", listingId)
        startActivity(intent)
    }
}
