package com.example.lendit.ui.favorites

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
import com.example.lendit.databinding.FragmentFavoritesBinding
import com.example.lendit.data.local.managers.FavoritesManager
import com.example.lendit.data.repository.RepositoryProvider
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var availableAdapter: FavoritesAdapter
    private lateinit var unavailableAdapter: FavoritesAdapter
    private lateinit var similarAdapter: ListingAdapter

    private lateinit var favoritesManager: FavoritesManager


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

        favoritesManager = FavoritesManager(requireContext())

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
        val sharedPref = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)
        if (userId == -1) return

        lifecycleScope.launch {
            try {
                favoritesManager.refresh(userId)

                availableAdapter.update(favoritesManager.available)
                unavailableAdapter.update(favoritesManager.unavailable)
                similarAdapter.update(favoritesManager.similar)

                binding.recyclerAvailableFavorites.visibility =
                    if (!favoritesManager.hasFavorites) View.GONE else View.VISIBLE
                binding.recyclerUnavailableFavorites.visibility =
                    if (!favoritesManager.hasFavorites) View.GONE else View.VISIBLE

                binding.textViewSimilar.visibility =
                    if (!favoritesManager.hasSimilar) View.VISIBLE else View.GONE
                binding.recyclerSimilar.visibility =
                    if (!favoritesManager.hasSimilar) View.VISIBLE else View.GONE

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

