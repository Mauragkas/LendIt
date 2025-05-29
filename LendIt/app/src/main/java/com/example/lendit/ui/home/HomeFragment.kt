package com.example.lendit.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lendit.SearchActivity
import com.example.lendit.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private fun displaySearch() {
        val intent = Intent(requireContext(), SearchActivity::class.java)
        startActivity(intent)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchButton = binding.searchInputLayoutMain

        val searchEditText = binding.searchEditTextMain

        val sharedPref = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userType = sharedPref.getString("userType", "")?.lowercase()


        if (userType == "renter") {
            searchEditText.visibility = View.VISIBLE
            searchButton.visibility = View.VISIBLE
            searchEditText.setOnClickListener {
                displaySearch()
            }
        } else {
            searchButton.visibility = View.GONE
            searchEditText.visibility = View.GONE

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}