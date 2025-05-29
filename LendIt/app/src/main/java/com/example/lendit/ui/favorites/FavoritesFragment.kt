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
import com.example.lendit.R
import com.example.lendit.databinding.FragmentFavoritesBinding
import EquipmentListing
import ListingStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var availableAdapter: FavoritesAdapter
    private lateinit var unavailableAdapter: FavoritesAdapter
    private lateinit var db: AppDatabase

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

        // Initialize database
        db = AppDatabase.getInstance(requireContext())

        // Initialize adapters
        availableAdapter = FavoritesAdapter(mutableListOf())
        unavailableAdapter = FavoritesAdapter(mutableListOf())

        // Setup RecyclerViews
        binding.recyclerAvailableFavorites.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = availableAdapter
        }

        binding.recyclerUnavailableFavorites.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = unavailableAdapter
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
                val favorites = withContext(Dispatchers.IO) {
                    // 1. Get all favorite records for this user
                    val favoriteRecords = db.FavoriteDao().getFavorites(userId)

                    // 2. Get details for each favorited listing
                    favoriteRecords.mapNotNull { favorite ->
                        db.listingDao().getListingById(favorite.listingId)
                    }
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
                }
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}