package com.example.lendit

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lendit.databinding.ActivityReviewBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewBinding
    private lateinit var adapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the recycler view
        binding.reviewRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ReviewAdapter(mutableListOf())
        binding.reviewRecyclerView.adapter = adapter

        // Set up back button
        binding.backButton.setOnClickListener { finish() }

        // Load unreviewed rentals
        loadUnreviewedRentals()
    }

    override fun onResume() {
        super.onResume()
        // Reload unreviewed rentals whenever the activity comes back to the foreground
        loadUnreviewedRentals()
    }

    private fun loadUnreviewedRentals() {
        lifecycleScope.launch {
            try {
                // Get user ID from SharedPreferences
                val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                val userId = sharedPref.getInt("userId", -1)

                if (userId == -1) {
                    showEmptyState(true, "Please log in to see your rentals")
                    return@launch
                }

                // Get unreviewed rentals from database
                val db = AppDatabase.getInstance(this@ReviewActivity)
                val unreviewedListingIds =
                        withContext(Dispatchers.IO) {
                            db.rentalDao().getUnreviewedRentalsForUser(userId)
                        }

                if (unreviewedListingIds.isEmpty()) {
                    showEmptyState(true, "You don't have any items to review yet")
                    return@launch
                }

                // Fetch the full listing details for each ID
                val unreviewedListings =
                        withContext(Dispatchers.IO) {
                            unreviewedListingIds.mapNotNull { listingId ->
                                db.listingDao().getListingById(listingId)
                            }
                        }

                if (unreviewedListings.isEmpty()) {
                    showEmptyState(true, "You don't have any items to review yet")
                    return@launch
                }

                // Convert to RentalItem for the adapter
                val rentalItems =
                        unreviewedListings.map { listing ->
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val photoList = listing.photos.split(",").filter { it.isNotEmpty() }
                            val imageUrl = if (photoList.isNotEmpty()) photoList[0] else ""

                            RentalItem(
                                    id = listing.listingId,
                                    title = listing.title,
                                    imageUrl = imageUrl,
                                    rentalDate =
                                            dateFormat.format(
                                                    Date(System.currentTimeMillis())
                                            ), // Use current date as a placeholder
                                    ownerName = listing.ownerName
                            )
                        }

                // Update UI
                withContext(Dispatchers.Main) {
                    adapter = ReviewAdapter(rentalItems.toMutableList())
                    binding.reviewRecyclerView.adapter = adapter
                    showEmptyState(false)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                                    this@ReviewActivity,
                                    "Error loading rentals: ${e.message}",
                                    Toast.LENGTH_SHORT
                            )
                            .show()
                    showEmptyState(true, "Error loading rentals")
                }
            }
        }
    }

    private fun showEmptyState(
            isEmpty: Boolean,
            message: String = "You don't have any items to review yet"
    ) {
        if (isEmpty) {
            binding.reviewRecyclerView.visibility = View.GONE
            binding.emptyStateText.visibility = View.VISIBLE
            binding.emptyStateText.text = message
        } else {
            binding.reviewRecyclerView.visibility = View.VISIBLE
            binding.emptyStateText.visibility = View.GONE
        }
    }
}
