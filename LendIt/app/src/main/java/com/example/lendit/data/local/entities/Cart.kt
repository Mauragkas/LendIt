package com.example.lendit.data.local.entities

import EquipmentListing
import android.content.Context
import android.widget.Toast
import androidx.room.Entity
import com.example.lendit.CartAdapter
import com.example.lendit.data.repository.CouponRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Entity(primaryKeys = ["userId", "listingId"])
data class UserCart(
    val userId: Int,
    val listingId: Int,
    val startDate: Int,
    val endDate: Int
)

class CartManager(
    private val context: Context,
    private val adapter: CartAdapter,
    private val couponRepository: CouponRepository
) {
    internal var listings: List<EquipmentListing> = emptyList()
    internal var discount: Int = 0

    suspend fun validateCoupon(coupon: String): Boolean {
        val availableCoupons = couponRepository.getAllCoupons()
        val matchedCoupon = availableCoupons.find { it.code.equals(coupon, ignoreCase = true) }

        return withContext(Dispatchers.Main) {
            if (matchedCoupon != null) {
                Toast.makeText(context, "Coupon is valid!", Toast.LENGTH_SHORT).show()
                discount = matchedCoupon.discountPercentage
                true
            } else {
                Toast.makeText(context, "Invalid coupon code.", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }

    fun setListings(newListings: List<EquipmentListing>) {
        listings = newListings
        adapter.update(listings)
    }

    fun calculateTotal(): Double {
        val basePrice = adapter.getTotalPrice()
        return basePrice * (1 - discount / 100.0)
    }

    fun getFormattedTotal(): String {
        return "Total: %.2f€".format(calculateTotal())
    }

    fun getDiscount(): Int = discount
}
