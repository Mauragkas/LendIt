package com.example.lendit

import EquipmentListing
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.lendit.data.local.managers.CompareManager
import com.example.lendit.data.local.managers.CompareResultsManager
import com.example.lendit.data.repository.RepositoryProvider
import com.example.lendit.databinding.ActivityCompareResultBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CompareResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompareResultBinding
    private var listings = mutableListOf<EquipmentListing>()
    private lateinit var compareResultsManager: CompareResultsManager
    private val listingRepository by lazy {
        RepositoryProvider.getListingRepository(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompareResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Set up back button
        binding.backButton.setOnClickListener { finish() }

        compareResultsManager  = CompareResultsManager()

        // Get the listing IDs from the intent
        val listingIds = intent.getIntArrayExtra("LISTING_IDS") ?: return

        // Load the listings
        lifecycleScope.launch { loadListings(listingIds.toList()) }
    }

    private suspend fun loadListings(listingIds: List<Int>) {
        try {
            val comparisonResult = compareResultsManager.loadAndCompareListings(listingIds, listingRepository)

            listings = comparisonResult.listings.toMutableList()

            if (comparisonResult.summary == "Not enough listings to compare.") {
                binding.errorMessage.visibility = View.VISIBLE
                binding.errorMessage.text = comparisonResult.summary
                binding.comparisonContent.visibility = View.GONE
                return
            }

            // Update UI with listings and summary
            displayListings()
            binding.aiComparisonText.text = comparisonResult.summary
            binding.errorMessage.visibility = View.GONE
            binding.comparisonContent.visibility = View.VISIBLE

        } catch (e: Exception) {
            binding.errorMessage.visibility = View.VISIBLE
            binding.errorMessage.text = "Error loading listings: ${e.message}"
            binding.comparisonContent.visibility = View.GONE
        }
    }


    private fun displayListings() {
        // Make sure the content is visible
        binding.errorMessage.visibility = View.GONE
        binding.comparisonContent.visibility = View.VISIBLE

        // Bind the listings UI (can be kept as is)

        // Display first listing
        val listing1 = listings[0]
        binding.title1.text = listing1.title
        binding.price1.text = "${listing1.price}€"
        binding.category1.text = listing1.category.toString()
        binding.location1.text = listing1.location.toString()
        binding.description1.text = listing1.description
        binding.viewButton1.setOnClickListener { selectTool(listing1.listingId) }

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
        binding.viewButton2.setOnClickListener { selectTool(listing2.listingId) }

        val photoList2 = listing2.photos.split(",").filter { it.isNotEmpty() }
        if (photoList2.isNotEmpty()) {
            Glide.with(this)
                .load(photoList2[0])
                .apply(RequestOptions().centerCrop())
                .into(binding.image2)
        }

        // Handle third listing if present
        if (listings.size < 3) {
            binding.column3.visibility = View.GONE
        } else {
            binding.column3.visibility = View.VISIBLE
            val listing3 = listings[2]
            binding.title3.text = listing3.title
            binding.price3.text = "${listing3.price}€"
            binding.category3.text = listing3.category.toString()
            binding.location3.text = listing3.location.toString()
        }
    }

    private fun selectTool(listingId: Int) {
        val intent = Intent(this, ListingDetailsActivity::class.java)
        intent.putExtra("listing_id", listingId)
        startActivity(intent)
    }
}
