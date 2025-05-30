package com.example.lendit.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lendit.LoginActivity
import com.example.lendit.R
import com.example.lendit.ReviewActivity
import com.example.lendit.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textProfile
        profileViewModel.text.observe(viewLifecycleOwner) { textView.text = it }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get user type from SharedPreferences
        val sharedPref =
                requireActivity()
                        .getSharedPreferences("MyAppPrefs", android.content.Context.MODE_PRIVATE)
        val userType = sharedPref.getString("userType", "")?.lowercase() ?: ""

        // Show Premium button only for owners
        if (userType == "owner") {
            binding.premiumButton.visibility = View.VISIBLE
            binding.reviewButton.visibility = View.GONE
        } else {
            binding.premiumButton.visibility = View.GONE
            binding.reviewButton.visibility = View.VISIBLE

            // Add click listener for review button (only for renters)
            binding.reviewButton.setOnClickListener {
                val intent = Intent(requireContext(), ReviewActivity::class.java)
                startActivity(intent)
            }
        }

        binding.logoutButtonProfile.setOnClickListener {
            with(sharedPref.edit()) {
                clear()
                apply()
            }

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        fun navigateToPremium() {
            findNavController().navigate(R.id.navigation_premium)
        }
        binding.premiumButton.setOnClickListener {
            navigateToPremium()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
