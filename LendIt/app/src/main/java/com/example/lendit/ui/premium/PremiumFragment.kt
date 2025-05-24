package com.example.lendit.ui.premium

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lendit.R
import com.example.lendit.databinding.FragmentPremiumBinding

class PremiumFragment : Fragment() {

    private var _binding: FragmentPremiumBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PremiumViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPremiumBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(PremiumViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get premium status from SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", 0)
        val isPremium = sharedPref.getBoolean("isPremium", false)

        // Update UI based on premium status
        updateUI(isPremium)

        // Set up subscription buttons
        binding.monthlyPlanCard.setOnClickListener { selectPlan("monthly") }
        binding.quarterlyPlanCard.setOnClickListener { selectPlan("quarterly") }
        binding.yearlyPlanCard.setOnClickListener { selectPlan("yearly") }

        // Set up subscribe button
        binding.subscribeButton.setOnClickListener {
            proceedToPayment()
        }

        // Set up cancel button
        binding.cancelSubscriptionButton.setOnClickListener {
            showCancellationDialog()
        }
    }

    private fun updateUI(isPremium: Boolean) {
        if (isPremium) {
            binding.subscribeButton.visibility = View.GONE
            binding.cancelSubscriptionButton.visibility = View.VISIBLE
            binding.subscriptionStatusText.text = "Είστε συνδρομητής Premium"
            binding.subscriptionStatusText.setTextColor(resources.getColor(R.color.teal_700, null))
        } else {
            binding.subscribeButton.visibility = View.VISIBLE
            binding.cancelSubscriptionButton.visibility = View.GONE
            binding.subscriptionStatusText.text = "Δεν έχετε ακόμη συνδρομή Premium"
            binding.subscriptionStatusText.setTextColor(resources.getColor(R.color.black, null))
        }
    }

    private fun selectPlan(plan: String) {
        // Deselect all plans
        binding.monthlyPlanCard.strokeWidth = 0
        binding.quarterlyPlanCard.strokeWidth = 0
        binding.yearlyPlanCard.strokeWidth = 0

        // Select chosen plan
        when (plan) {
            "monthly" -> {
                binding.monthlyPlanCard.strokeWidth = 4
                viewModel.selectedPlan = "monthly"
                viewModel.planPrice = 9.99
            }
            "quarterly" -> {
                binding.quarterlyPlanCard.strokeWidth = 4
                viewModel.selectedPlan = "quarterly"
                viewModel.planPrice = 24.99
            }
            "yearly" -> {
                binding.yearlyPlanCard.strokeWidth = 4
                viewModel.selectedPlan = "yearly"
                viewModel.planPrice = 89.99
            }
        }

        binding.subscribeButton.isEnabled = true
    }

    private fun proceedToPayment() {
        // In a real app, this would navigate to a payment screen
        // For this demo, we'll simulate a successful payment

        AlertDialog.Builder(requireContext())
            .setTitle("Επιβεβαίωση αγοράς")
            .setMessage("Είστε βέβαιοι ότι θέλετε να αποκτήσετε το πλάνο ${viewModel.selectedPlan} για €${viewModel.planPrice}?")
            .setPositiveButton("Συνέχεια") { _, _ ->
                // Simulate payment success
                simulateSuccessfulPayment()
            }
            .setNegativeButton("Άκυρο", null)
            .show()
    }

    private fun simulateSuccessfulPayment() {
        // Update shared preferences
        val sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", 0)
        with(sharedPref.edit()) {
            putBoolean("isPremium", true)
            putString("premiumPlan", viewModel.selectedPlan)
            apply()
        }

        // Show success message
        AlertDialog.Builder(requireContext())
            .setTitle("Επιτυχής αναβάθμιση!")
            .setMessage("Συγχαρητήρια! Έχετε αναβαθμιστεί σε Premium χρήστη.")
            .setPositiveButton("OK") { _, _ ->
                // Navigate back to profile
                findNavController().navigateUp()
            }
            .show()
    }

    private fun showCancellationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Ακύρωση συνδρομής Premium")
            .setMessage("Είστε βέβαιοι ότι θέλετε να ακυρώσετε τη συνδρομή σας; Θα χάσετε όλα τα προνόμια Premium.")
            .setPositiveButton("Ακύρωση συνδρομής") { _, _ ->
                cancelSubscription()
            }
            .setNegativeButton("Όχι, θέλω να παραμείνω Premium", null)
            .show()
    }

    private fun cancelSubscription() {
        // Update shared preferences
        val sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", 0)
        with(sharedPref.edit()) {
            putBoolean("isPremium", false)
            remove("premiumPlan")
            apply()
        }

        // Update UI
        updateUI(false)

        // Show confirmation
        AlertDialog.Builder(requireContext())
            .setTitle("Η συνδρομή σας ακυρώθηκε")
            .setMessage("Η συνδρομή σας έχει ακυρωθεί. Θα διατηρήσετε τα προνόμια Premium μέχρι το τέλος της τρέχουσας περιόδου χρέωσης.")
            .setPositiveButton("OK") { _, _ ->
                findNavController().navigateUp()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
