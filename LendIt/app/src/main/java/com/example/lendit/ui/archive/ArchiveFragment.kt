package com.example.lendit.ui.archive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
<<<<<<< Updated upstream
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lendit.ListingAdapter
import com.example.lendit.databinding.FragmentArchiveBinding
import EquipmentListing
import ListingStatus
import AppDatabase
import com.example.lendit.R
import kotlinx.coroutines.launch
=======
import androidx.lifecycle.ViewModelProvider
import com.example.lendit.databinding.FragmentArchiveBinding
import com.example.lendit.databinding.FragmentCartBinding
>>>>>>> Stashed changes

class ArchiveFragment : Fragment() {

    private var _binding: FragmentArchiveBinding? = null
<<<<<<< Updated upstream
=======

    // This property is only valid between onCreateView and
    // onDestroyView.
>>>>>>> Stashed changes
    private val binding get() = _binding!!

    private val availableAdapter = ListingAdapter(mutableListOf())
    private val unavailableAdapter = ListingAdapter(mutableListOf())
    private val inactiveAdapter = ListingAdapter(mutableListOf())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArchiveBinding.inflate(inflater, container, false)
        return binding.root
    }

<<<<<<< Updated upstream
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
=======
        _binding = FragmentArchiveBinding.inflate(inflater, container, false)
        val root: View = binding.root
>>>>>>> Stashed changes

        // Get owner name from shared preferences
        val sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", 0)
        val ownerName = sharedPref.getString("userName", "") ?: ""

        // Setup RecyclerViews
        setupRecyclerViews()

        // Load listings based on owner name
        loadListingsByOwner(ownerName)

        // Set up tab selection listeners
        setupTabListeners()

        // Set up empty state button
        binding.emptyStateButton.setOnClickListener {
            // Navigate to ListingActivity
            activity?.let {
                val intent = android.content.Intent(it, com.example.lendit.ListingActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setupRecyclerViews() {
        // Available listings
        binding.availableRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = availableAdapter
        }

        // Unavailable listings
        binding.unavailableRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = unavailableAdapter
        }

        // Inactive listings
        binding.inactiveRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = inactiveAdapter
        }
    }

    private fun setupTabListeners() {
        binding.availableTab.setOnClickListener {
            showTab(0)
        }

        binding.unavailableTab.setOnClickListener {
            showTab(1)
        }

        binding.inactiveTab.setOnClickListener {
            showTab(2)
        }

        // Show available tab by default
        showTab(0)
    }

    private fun showTab(tabIndex: Int) {
        // Hide all recycler views first
        binding.availableRecyclerView.visibility = View.GONE
        binding.unavailableRecyclerView.visibility = View.GONE
        binding.inactiveRecyclerView.visibility = View.GONE

        // Reset all tab styles
        binding.availableTab.isSelected = false
        binding.unavailableTab.isSelected = false
        binding.inactiveTab.isSelected = false

        // Show selected tab content
        when (tabIndex) {
            0 -> {
                binding.availableRecyclerView.visibility = View.VISIBLE
                binding.availableTab.isSelected = true
            }
            1 -> {
                binding.unavailableRecyclerView.visibility = View.VISIBLE
                binding.unavailableTab.isSelected = true
            }
            2 -> {
                binding.inactiveRecyclerView.visibility = View.VISIBLE
                binding.inactiveTab.isSelected = true
            }
        }
    }

    private fun loadListingsByOwner(ownerName: String) {
        lifecycleScope.launch {
            try {
                val db = AppDatabase.getInstance(requireContext())
                val allListings = db.listingDao().getListingsByOwner(ownerName)

                // Filter listings by status
                val availableListings = allListings.filter { it.status == ListingStatus.AVAILABLE }
                val unavailableListings = allListings.filter { it.status == ListingStatus.UNAVAILABLE }
                val inactiveListings = allListings.filter { it.status == ListingStatus.INACTIVE }

                // Update adapters
                availableAdapter.update(availableListings)
                unavailableAdapter.update(unavailableListings)
                inactiveAdapter.update(inactiveListings)

                // Update counters
                binding.availableCount.text = availableListings.size.toString()
                binding.unavailableCount.text = unavailableListings.size.toString()
                binding.inactiveCount.text = inactiveListings.size.toString()

                // Show empty state if needed
                binding.emptyStateGroup.visibility = if (allListings.isEmpty()) View.VISIBLE else View.GONE

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading listings: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun refreshListings() {
        val sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", 0)
        val ownerName = sharedPref.getString("userName", "") ?: ""
        loadListingsByOwner(ownerName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
