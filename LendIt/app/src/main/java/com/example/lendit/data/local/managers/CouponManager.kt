package com.example.lendit.data.local.managers

import android.content.Context
import android.widget.Toast
import com.example.lendit.data.repository.CouponRepository
import com.example.lendit.data.repository.RepositoryProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CouponManager(
    private val context: Context,
    private val lifecycleScope: CoroutineScope,
    private val onDiscountApplied: (Int) -> Unit,
    private val onError: (String) -> Unit
) {
    private val couponRepository by lazy {
        RepositoryProvider.getCouponRepository(context)
    }

    private var appliedDiscount: Int = 0

    fun getCurrentDiscount(): Int = appliedDiscount

    fun validateCoupon(couponCode: String) {
        if (couponCode.isBlank()) {
            onError("Please enter a coupon code")
            return
        }

        lifecycleScope.launch {
            try {
                val availableCoupons = couponRepository.getAllCoupons()
                val matchedCoupon = availableCoupons.find { it.code.equals(couponCode, ignoreCase = true) }

                withContext(Dispatchers.Main) {
                    if (matchedCoupon != null) {
                        appliedDiscount = matchedCoupon.discountPercentage
                        Toast.makeText(context, "Coupon is valid! ${appliedDiscount}% discount applied", Toast.LENGTH_SHORT).show()
                        onDiscountApplied(appliedDiscount)
                    } else {
                        Toast.makeText(context, "Invalid coupon code.", Toast.LENGTH_SHORT).show()
                        onError("Invalid coupon code")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Error validating coupon: ${e.message}")
                }
            }
        }
    }

    fun calculateDiscountedPrice(originalPrice: Double): Double {
        return originalPrice * (1 - appliedDiscount / 100.0)
    }

    fun clearDiscount() {
        appliedDiscount = 0
        onDiscountApplied(0)
    }

    fun getFormattedDiscountAmount(originalPrice: Double): String {
        val discountAmount = originalPrice * (appliedDiscount / 100.0)
        return "-%.2f€".format(discountAmount)
    }
}
