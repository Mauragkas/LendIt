package com.example.lendit.ui.compare

import EquipmentListing
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.lendit.databinding.FragmentCompareBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CompareFragment : Fragment() {

    private var _binding: FragmentCompareBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var listings: List<EquipmentListing>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val compareViewModel =
            ViewModelProvider(this).get(CompareViewModel::class.java)

        _binding = FragmentCompareBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        compareViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

//    private fun getFavorites() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            try {
//                listings = withContext(Dispatchers.IO) {
//                    AppDatabase.getListings(requireContext(), filters)        // <<- only context
//                }
//
//                if (listings.isNotEmpty()) {
//                    adapter.update(listings)                         // refresh rows
//                } else {
//                    Toast.makeText(context, "No listings available.", Toast.LENGTH_SHORT).show()
//                }
//
//            } catch (e: Exception) {
//                Log.e("ListingFragment", "Error fetching listings", e)
//            }
//        }
//    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load the listings
//        getFavorites()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}