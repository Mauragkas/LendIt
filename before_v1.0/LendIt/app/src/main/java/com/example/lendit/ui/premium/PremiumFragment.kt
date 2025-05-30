package com.example.lendit.ui.premium

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lendit.R
import com.example.lendit.data.local.managers.PremiumManager
import com.example.lendit.databinding.FragmentPremiumBinding
import com.example.lendit.ui.payment.PaymentActivity
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class PremiumFragment : Fragment() {

    private var _binding: FragmentPremiumBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PremiumViewModel
    private lateinit var paymentLauncher: ActivityResultLauncher<Intent>
    private lateinit var premiumManager: PremiumManager

    fun showProfile() {
        findNavController().navigate(R.id.navigation_profile)
    }

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

        // Initialize PremiumManager using a simple CoroutineScope
        premiumManager = PremiumManager(
            context = requireContext(),
            coroutineScope = CoroutineScope(Dispatchers.Main),
            onStatusUpdated = { isPremium ->
                updateUI(isPremium)
                if (isPremium) {
                    Toast.makeText(requireContext(), "You are now a Premium member!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Premium subscription canceled", Toast.LENGTH_SHORT).show()
                }
            },
            onError = { errorMessage ->
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        )

        // Initially hide plan selection and duration selection
        binding.planDetailsContainer.visibility = View.GONE
        binding.planDurationContainer.visibility = View.GONE

        // Update UI based on premium status
        updateUI(premiumManager.isPremiumUser())

        // Register payment result handler
        paymentLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Payment was successful
                premiumManager.activatePremium()
            } else {
                // Payment was cancelled or failed
                Toast.makeText(requireContext(), "Payment was cancelled or failed.", Toast.LENGTH_SHORT).show()
            }
            showProfile()
        }

        // Setup learn more button
        binding.learnMoreButton.setOnClickListener {
            // Show plan details
            binding.planDetailsContainer.visibility = View.VISIBLE
        }

        // Setup get premium button
        binding.getPremiumButton.setOnClickListener {
            // Show duration selection
            binding.planDurationContainer.visibility = View.VISIBLE
            binding.subscribeButton.isEnabled = false
        }

        // Setup view benefits button for premium users
        binding.viewBenefitsButton.setOnClickListener {
            binding.planDetailsContainer.visibility = View.VISIBLE
        }

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
            cancelSubscription()
        }
    }

    private fun updateUI(isPremium: Boolean) {
        if (isPremium) {
            binding.premiumStatusContainer.visibility = View.VISIBLE
            binding.nonPremiumContainer.visibility = View.GONE
            binding.subscribeButton.visibility = View.GONE
            binding.cancelSubscriptionButton.visibility = View.VISIBLE
            binding.subscriptionStatusText.text = "Είστε συνδρομητής Premium"
            binding.subscriptionStatusText.setTextColor(resources.getColor(R.color.teal_700, null))
        } else {
            binding.premiumStatusContainer.visibility = View.GONE
            binding.nonPremiumContainer.visibility = View.VISIBLE
            binding.subscribeButton.visibility = View.VISIBLE
            binding.cancelSubscriptionButton.visibility = View.GONE
            binding.subscriptionStatusText.text = "Δεν έχετε ακόμη συνδρομή Premium"
            binding.subscriptionStatusText.setTextColor(resources.getColor(R.color.black, null))
        }
    }

    private fun selectPlan(planName: String) {
        // Deselect all plans
        binding.monthlyPlanCard.strokeWidth = 0
        binding.quarterlyPlanCard.strokeWidth = 0
        binding.yearlyPlanCard.strokeWidth = 0

        // Select the plan using the manager
        val plan = premiumManager.selectPlan(planName)

        if (plan != null) {
            // Select chosen plan UI
            when (planName) {
                "monthly" -> binding.monthlyPlanCard.strokeWidth = 4
                "quarterly" -> binding.quarterlyPlanCard.strokeWidth = 4
                "yearly" -> binding.yearlyPlanCard.strokeWidth = 4
            }

            // Update ViewModel for sharing data with PaymentActivity
            viewModel.selectedPlan = plan.name
            viewModel.planPrice = plan.price

            binding.subscribeButton.isEnabled = true
        }
    }

    private fun proceedToPayment() {
        val plan = premiumManager.getSelectedPlan()

        if (plan == null) {
            Toast.makeText(requireContext(), "Please select a plan first", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Επιβεβαίωση αγοράς")
            .setMessage("Είστε βέβαιοι ότι θέλετε να αποκτήσετε το πλάνο ${plan.name} για €${plan.price}?")
            .setPositiveButton("Συνέχεια στην πληρωμή") { _, _ ->
                val intent = Intent(context, PaymentActivity::class.java).apply {
                    putExtra("selectedPlan", plan.name)
                    putExtra("planPrice", plan.price)
                }
                paymentLauncher.launch(intent)
            }
            .setNegativeButton("Άκυρο", null)
            .show()
    }

    private fun cancelSubscription() {
        AlertDialog.Builder(requireContext())
            .setTitle("Ακύρωση συνδρομής Premium")
            .setMessage("Είστε βέβαιοι ότι θέλετε να ακυρώσετε τη συνδρομή σας; Θα χάσετε όλα τα προνόμια Premium.")
            .setPositiveButton("Ακύρωση συνδρομής") { _, _ ->
                premiumManager.cancelPremium()

                // Show confirmation
                AlertDialog.Builder(requireContext())
                    .setTitle("Η συνδρομή σας ακυρώθηκε")
                    .setMessage("Η συνδρομή σας έχει ακυρώθηκε. Θα διατηρήσετε τα προνόμια Premium μέχρι το τέλος της τρέχουσας περιόδου χρέωσης.")
                    .setPositiveButton("OK") { _, _ ->
                        showProfile()
                    }
                    .show()
            }
            .setNegativeButton("Όχι, θέλω να παραμείνω Premium", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}