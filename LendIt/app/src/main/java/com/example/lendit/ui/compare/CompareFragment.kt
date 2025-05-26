package com.example.lendit.ui.compare

import AppDatabase
import EquipmentListing
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lendit.CompareResultActivity
import com.example.lendit.databinding.FragmentCompareBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CompareFragment : Fragment() {

    private var _binding: FragmentCompareBinding? = null
    private val binding
        get() = _binding!!
    private lateinit var adapter: CompareListingAdapter
    private val selectedListings = mutableListOf<EquipmentListing>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup recycler view with adapter
        adapter =
                CompareListingAdapter(
                        mutableListOf(),
                        onItemSelected = { listing -> addToCompare(listing) }
                )
        binding.favoritesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@CompareFragment.adapter
        }

        // Set up compare button
        binding.compareButton.setOnClickListener {
            if (selectedListings.size < 2) {
                Toast.makeText(
                                context,
                                "Please select at least 2 items to compare",
                                Toast.LENGTH_SHORT
                        )
                        .show()
            } else {
                startComparison()
            }
        }

        // Set up clear selection button
        binding.clearButton.setOnClickListener {
            selectedListings.clear()
            updateSelectionUI()
            adapter.clearSelections()
        }

        // Load favorite listings
        loadFavorites()

        // Initialize UI
        updateSelectionUI()
    }

    private fun loadFavorites() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val sharedPref = context?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val userId = sharedPref?.getInt("user_id", -1) ?: -1

                if (userId == -1) {
                    showEmptyState("Please log in to view favorites")
                    return@launch
                }

                val db = AppDatabase.getInstance(requireContext())
                val favorites =
                        withContext(Dispatchers.IO) {
                            val favoriteRecords = db.FavoriteDao().getFavorites(userId)
                            favoriteRecords.mapNotNull { favorite ->
                                db.listingDao().getListingById(favorite.listingId)
                            }
                        }

                if (favorites.isEmpty()) {
                    showEmptyState("No favorites found. Add some items to your favorites first!")
                } else {
                    binding.emptyStateContainer.visibility = View.GONE
                    binding.contentContainer.visibility = View.VISIBLE
                    adapter.update(favorites)
                }
            } catch (e: Exception) {
                showEmptyState("Error loading favorites: ${e.message}")
            }
        }
    }

    private fun showEmptyState(message: String) {
        binding.emptyStateContainer.visibility = View.VISIBLE
        binding.contentContainer.visibility = View.GONE
        binding.emptyStateMessage.text = message
    }

    private fun addToCompare(listing: EquipmentListing) {
        if (selectedListings.any { it.listingId == listing.listingId }) {
            // If already selected, remove it
            selectedListings.removeIf { it.listingId == listing.listingId }
        } else {
            // If not selected and we have less than 3 items, add it
            if (selectedListings.size < 3) {
                selectedListings.add(listing)
            } else {
                Toast.makeText(
                                context,
                                "You can compare up to 3 items at a time",
                                Toast.LENGTH_SHORT
                        )
                        .show()
            }
        }
        updateSelectionUI()
    }

    private fun updateSelectionUI() {
        // Update the selected count and enable/disable compare button
        binding.selectedCount.text = "${selectedListings.size} items selected"
        binding.compareButton.isEnabled = selectedListings.size >= 2

        // Update the selection slots
        binding.selection1.text =
                if (selectedListings.size > 0) selectedListings[0].title else "Selection 1"
        binding.selection2.text =
                if (selectedListings.size > 1) selectedListings[1].title else "Selection 2"
        binding.selection3.text =
                if (selectedListings.size > 2) selectedListings[2].title
                else "Selection 3 (optional)"
    }

    private fun startComparison() {
        // Store the IDs to pass to the comparison activity
        val listingIds = selectedListings.map { it.listingId }.toIntArray()

        // Launch the comparison activity
        val intent = Intent(requireContext(), CompareResultActivity::class.java)
        intent.putExtra("LISTING_IDS", listingIds)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
