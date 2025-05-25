package com.example.lendit.ui.show_listings

import EquipmentListing
import ListingFilters
import SortBy
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lendit.ListingAdapter
import com.example.lendit.R
import com.example.lendit.databinding.FragmentListingsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShowListingsFragment : Fragment(R.layout.fragment_listings) {

    private var _binding: FragmentListingsBinding? = null
    private val binding get() = _binding!!
    lateinit var listings: List<EquipmentListing>

    // one adapter instance – start with an empty list
    private val adapter = ListingAdapter(mutableListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun sortList(sortBy: SortBy) {
        val sortedList = when (sortBy) {
            SortBy.ASC -> listings.sortedBy { it.price }
            SortBy.DESC -> listings.sortedByDescending { it.price }
            SortBy.SUGGESTED -> listings // or any custom sort logic here
        }

        //showFilteredList()
        adapter.update(sortedList)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val filters = arguments?.getParcelable("filters", ListingFilters::class.java)               //get filters

        // RecyclerView setup once
        binding.listingRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ShowListingsFragment.adapter
        }

        binding.priceSortGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioAscending -> {
                    // Handle ascending sort
                    sortList(SortBy.ASC)
                }
                R.id.radioDescending -> {
                    // Handle descending sort
                    sortList(SortBy.DESC)
                }
                R.id.radioSuggested -> {
                    // Handle suggested sort
                    sortList(SortBy.SUGGESTED)
                }
            }
        }

        // Load the demo listings
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                listings = withContext(Dispatchers.IO) {
                    AppDatabase.getListings(requireContext(), filters)        // <<- only context
                }

                if (listings.isNotEmpty()) {
                    adapter.update(listings)                         // refresh rows
                } else {
                    Toast.makeText(context, "No listings available.", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("ListingFragment", "Error fetching listings", e)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
