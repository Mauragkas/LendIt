package com.example.lendit.ui.cart

import EquipmentListing
import android.app.Activity
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


class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var listings: List<EquipmentListing>
    var userId by Delegates.notNull<Int>()
    private val adapter = CartAdapter(mutableListOf())
    private var total = 0.0

    private lateinit var couponDao: CouponDao

    // CartFragment.kt
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
        val root: View = binding.root

        return root
    }

    private suspend fun validateCoupon(coupon: String): Int {
        var discount = 0
        val availableCoupons = couponDao.getAllCoupons()
        val matchedCoupon = availableCoupons.find { it.code.equals(coupon, ignoreCase = true) }

        if (matchedCoupon != null) {
            withContext(Dispatchers.Main) {
                // informUser()
                Toast.makeText(requireContext(), "Coupon is valid!", Toast.LENGTH_SHORT).show()
            }
            discount = matchedCoupon.discountPercentage
        } else {
            withContext(Dispatchers.Main) {
                // informUser()
                Toast.makeText(requireContext(), "Invalid coupon code.", Toast.LENGTH_SHORT).show()
            }
        }
        return discount
    }

    private fun calculateCost(discount: Int): Double {
        adapter.update(listings) // refresh rows
        total = adapter.getTotalPrice() * (1 - discount * 0.01)
        return total
    }

    private fun displayPayment() {
        val intent = Intent(requireContext(), PaymentActivity::class.java).apply {
            putExtra("userId", userId)
            putExtra("price", total)
            putIntegerArrayListExtra("listingIds", ArrayList(listings.map { it.listingId }))
        }
        paymentLauncher.launch(intent)   // ✅ use the already-registered launcher
    }


    private fun getProduct() {
        displayPayment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Load the listings
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.cartRecyclerView.adapter = adapter

        couponDao = AppDatabase.getInstance(requireContext()).couponDao()

        // Listener for Apply Coupon button
        binding.applyCouponButton.setOnClickListener {
            val couponCode = binding.couponEditText.text.toString()
            if (couponCode.isNotBlank()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val discount = validateCoupon(couponCode)
                    val total = calculateCost(discount)
                    binding.totalPriceTextView.text = "Total: ${"%.2f€".format(total)}"
                }
            }
        }
        val deliveryAddressEditText = view.findViewById<EditText>(R.id.addressEditText)
        val continueToPaymentButton = view.findViewById<Button>(R.id.continueToPaymentButton)

        continueToPaymentButton.isEnabled = false // Disable by default


        // Placeholder listener for Continue to Payment button
        binding.continueToPaymentButton.setOnClickListener {
            getProduct()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                listings = withContext(Dispatchers.IO) {
                    AppDatabase.showCart(requireContext(), userId)
                }
                deliveryAddressEditText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if(listings.isNotEmpty())
                            continueToPaymentButton.isEnabled = !s.isNullOrBlank()
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })

                if (listings.isNotEmpty()) {
                    adapter.update(listings)                         // refresh rows
                    val total = adapter.getTotalPrice()
                    binding.totalPriceTextView.text = "Total: ${"%.2f€".format(total)}"

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