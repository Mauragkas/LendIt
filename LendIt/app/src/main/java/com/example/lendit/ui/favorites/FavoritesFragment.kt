package com.example.lendit.ui.favorites

import AppDatabase
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lendit.FavoritesAdapter
import com.example.lendit.ListingAdapter
import com.example.lendit.R
import com.example.lendit.databinding.FragmentFavoritesBinding
import EquipmentListing
import ListingStatus
import com.example.lendit.data.repository.RepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var availableAdapter: FavoritesAdapter
    private lateinit var unavailableAdapter: FavoritesAdapter
    private lateinit var similarAdapter: ListingAdapter


    private val getFavoriteRepository by lazy {
        RepositoryProvider.getFavoriteRepository(requireContext())
    }
    private val getListingRepository by lazy {
        RepositoryProvider.getListingRepository(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize adapters
        availableAdapter = FavoritesAdapter(requireContext(), mutableListOf())
        unavailableAdapter = FavoritesAdapter(requireContext(), mutableListOf())
        similarAdapter = ListingAdapter(requireContext(), mutableListOf())

        // Setup RecyclerViews
        binding.recyclerAvailableFavorites.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = availableAdapter
        }

        binding.recyclerUnavailableFavorites.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = unavailableAdapter
        }

        binding.recyclerSimilar.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = similarAdapter
        }

        loadFavorites()
    }

    private fun loadFavorites() {
        lifecycleScope.launch {
            val sharedPref = context?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userId = sharedPref?.getInt("user_id", -1) ?: -1

            if (userId == -1) {
                // Handle case where user is not logged in
                return@launch
            }

            try {
                val favoriteRecords = getFavoriteRepository.getFavorites(userId)
                val favorites = favoriteRecords.mapNotNull { favorite ->
                    getListingRepository.getListingById(favorite.listingId)
                }


                // Separate into available and unavailable
                val available = favorites.filter { it.status == ListingStatus.AVAILABLE }
                val unavailable = favorites.filter { it.status != ListingStatus.AVAILABLE }

                // Update adapters
                withContext(Dispatchers.Main) {
                    availableAdapter.update(available)
                    unavailableAdapter.update(unavailable)

                    // Show/hide sections based on content
                    binding.recyclerAvailableFavorites.visibility =
                        if (available.isEmpty()) View.GONE else View.VISIBLE
                    binding.recyclerUnavailableFavorites.visibility =
                        if (unavailable.isEmpty()) View.GONE else View.VISIBLE

                    // Load similar listings if we have favorites
                    if (favorites.isNotEmpty()) {
                        loadSimilarListings(favorites)
                    } else {
                        binding.textViewSimilar.visibility = View.GONE
                        binding.recyclerSimilar.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    private fun loadSimilarListings(favorites: List<EquipmentListing>) {
        lifecycleScope.launch {
            try {
                // Extract categories from favorites
                val favoriteCategories = favorites.map { it.category }.distinct()
                val favoriteCategoryNames = favoriteCategories.map { it.name } // Convert to strings
                val favoriteIds = favorites.map { it.listingId }

                // Find similar listings based on categories but exclude favorites
                val similarListings = getListingRepository.getListingsByCategories(favoriteCategoryNames)
                        .filter { it.listingId !in favoriteIds } // Exclude items already in favorites
                        .filter { it.status == ListingStatus.UNAVAILABLE } // Only show available items
                        .take(10) // Limit to 10 items

                withContext(Dispatchers.Main) {
                    // Update UI
                    if (similarListings.isNotEmpty()) {
                        similarAdapter.update(similarListings)
                        binding.textViewSimilar.visibility = View.VISIBLE
                        binding.recyclerSimilar.visibility = View.VISIBLE
                    } else {
                        binding.textViewSimilar.visibility = View.GONE
                        binding.recyclerSimilar.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                binding.textViewSimilar.visibility = View.GONE
                binding.recyclerSimilar.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

