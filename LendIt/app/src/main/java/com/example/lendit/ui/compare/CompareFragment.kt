package com.example.lendit.ui.compare

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
import com.example.lendit.SearchActivity
import com.example.lendit.data.local.entities.CompareManager
import com.example.lendit.data.repository.RepositoryProvider
import com.example.lendit.databinding.FragmentCompareBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CompareFragment : Fragment() {

    private var _binding: FragmentCompareBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CompareListingAdapter

    private val getFavoriteRepository by lazy {
        RepositoryProvider.getFavoriteRepository(requireContext())
    }
    private val getListingRepository by lazy {
        RepositoryProvider.getListingRepository(requireContext())
    }

    private var favorites: CompareManager? = null

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

        adapter = CompareListingAdapter(
            mutableListOf(),
            onItemSelected = { listing -> modifyCompare(listing) }
        )

        binding.favoritesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@CompareFragment.adapter
        }

        binding.compareButton.setOnClickListener {
            val selectedCount = favorites?.selectedListings?.size ?: 0
            if (selectedCount < 2) {
                Toast.makeText(
                    context,
                    "Please select at least 2 items to compare",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                performComparison()
            }
        }

        binding.clearButton.setOnClickListener {
            favorites?.clearSelections()
            updateSelectionUI()
            adapter.notifyDataSetChanged()
        }

        getFavorites()
        updateSelectionUI()
    }

    private fun getFavorites() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val sharedPref = context?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val userId = sharedPref?.getInt("user_id", -1) ?: -1

                if (userId == -1) {
                    Toast.makeText(requireContext(), "Please Log in", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                favorites = CompareManager.loadFavorites(userId, getFavoriteRepository, getListingRepository)

                if (favorites?.favoriteListings.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "No favorites found. Add some items to your favorites first!", Toast.LENGTH_SHORT).show()
                    showSearchActivity()
                } else {
                    binding.emptyStateContainer.visibility = View.GONE
                    binding.contentContainer.visibility = View.VISIBLE
                    adapter.update(favorites!!.favoriteListings)
                }
                updateSelectionUI()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error Loading Favorites!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSearchActivity() {
        val intent = Intent(requireContext(), SearchActivity::class.java)
        startActivity(intent)
    }

    private fun modifyCompare(listing: EquipmentListing) {
        favorites?.toggleSelection(listing)
        updateSelectionUI()
        adapter.notifyDataSetChanged()
    }

    private fun updateSelectionUI() {
        val selected = favorites?.selectedListings ?: emptyList()

        binding.selectedCount.text = "${selected.size} items selected"
        binding.compareButton.isEnabled = selected.size >= 2

        binding.selection1.text = selected.getOrNull(0)?.title ?: "Selection 1"
        binding.selection2.text = selected.getOrNull(1)?.title ?: "Selection 2"
        binding.selection3.text = selected.getOrNull(2)?.title ?: "Selection 3 (optional)"
    }

    private fun performComparison() {
        favorites?.let {
            val intent = it.createCompareIntent(requireContext())
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

