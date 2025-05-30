package com.example.lendit.ui.cart

import EquipmentListing
import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lendit.CartAdapter
import com.example.lendit.R
import com.example.lendit.SignupActivity
import com.example.lendit.data.local.dao.CouponDao
import com.example.lendit.databinding.FragmentCartBinding
import com.example.lendit.ui.payment.PaymentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.lendit.data.local.managers.CartManager
import com.example.lendit.data.local.managers.CouponManager
import com.example.lendit.data.repository.RepositoryProvider

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    var userId by Delegates.notNull<Int>()
    private lateinit var adapter: CartAdapter
    private lateinit var cartManager: CartManager
    private lateinit var couponManager: CouponManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        adapter = CartAdapter(context, mutableListOf())
        cartManager= CartManager(requireContext(), adapter)
    }

    private val paymentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // payment succeeded – go home or refresh UI
                findNavController().navigate(R.id.navigation_home)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //get the user Name to properly save it
        val sharedPref = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userId = sharedPref.getInt("userId", -1)

        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the coupon manager
        couponManager = CouponManager(
            context = requireContext(),
            lifecycleScope = viewLifecycleOwner.lifecycleScope,
            onDiscountApplied = { discount ->
                updateTotalPrice()
            },
            onError = { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        )

        // Load the listings
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.cartRecyclerView.adapter = adapter

        // Listener for Apply Coupon button
        binding.applyCouponButton.setOnClickListener {
            val couponCode = binding.couponEditText.text.toString()
            if (couponCode.isNotBlank()) {
                couponManager.validateCoupon(couponCode)
            } else {
                Toast.makeText(requireContext(), "Please enter a coupon code", Toast.LENGTH_SHORT).show()
            }
        }

        val deliveryAddressEditText = view.findViewById<EditText>(R.id.addressEditText)
        val continueToPaymentButton = view.findViewById<Button>(R.id.continueToPaymentButton)

        continueToPaymentButton.isEnabled = false // Disable by default

        // Listener for Continue to Payment button
        binding.continueToPaymentButton.setOnClickListener {
            displayPayment()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                cartManager.loadCartForUser(userId)

                deliveryAddressEditText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if(cartManager.listings.isNotEmpty())
                            continueToPaymentButton.isEnabled = !s.isNullOrBlank()
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })

                if (cartManager.listings.isNotEmpty()) {
                    adapter.update(cartManager.listings)                         // refresh rows
                    updateTotalPrice()
                } else {
                    Toast.makeText(context, "No listings available.", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("ListingFragment", "Error fetching listings", e)
            }
        }
    }

    private fun updateTotalPrice() {
        val basePrice = adapter.getTotalPrice()
        val finalPrice = couponManager.calculateDiscountedPrice(basePrice)
        binding.totalPriceTextView.text = "Total: %.2f€".format(finalPrice)

        // If there's a discount, show it
        if (couponManager.getCurrentDiscount() > 0) {
            binding.discountTextView.visibility = View.VISIBLE
            binding.discountTextView.text = "Discount: ${couponManager.getFormattedDiscountAmount(basePrice)}"
        } else {
            binding.discountTextView.visibility = View.GONE
        }
    }

    private fun displayPayment() {
        val basePrice = adapter.getTotalPrice()
        val finalPrice = couponManager.calculateDiscountedPrice(basePrice)

        val intent = Intent(requireContext(), PaymentActivity::class.java).apply {
            putExtra("userId", userId)
            putExtra("price", finalPrice)
            putIntegerArrayListExtra("listingIds", ArrayList(cartManager.listings.map { it.listingId }))
            putExtra("discountPercentage", couponManager.getCurrentDiscount())
        }
        paymentLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
