package com.example.lendit.ui.listing

import ListingEntity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class ListingFragment : Fragment(R.layout.fragment_listings) {

    private var _binding: FragmentListingsBinding? = null
    private val binding get() = _binding!!

    // one adapter instance – start with an empty list
    private val adapter = ListingAdapter(mutableListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView setup once
        binding.listingRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ListingFragment.adapter
        }

        // Load the demo listings
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val listings: List<ListingEntity> = withContext(Dispatchers.IO) {
                    AppDatabase.getListings(requireContext())        // <<- only context
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
