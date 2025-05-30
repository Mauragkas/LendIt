package com.example.lendit.data.local.managers

import EquipmentListing
import android.content.Context
import android.widget.Toast
import com.example.lendit.CartAdapter
import com.example.lendit.data.repository.CouponRepository
import com.example.lendit.data.repository.RepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.text.equals

class CartManager(
    private val context: Context,
    internal val adapter: CartAdapter)
{
    internal val couponRepository = RepositoryProvider.getCouponRepository(context)
    internal val cartRepository = RepositoryProvider.getCartRepository(context)
    internal var listings: MutableList<EquipmentListing> = mutableListOf()
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

    fun setListings(newListings: MutableList<EquipmentListing>) {
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

    suspend fun loadCartForUser(userId: Int) {
        val cartListings = withContext(Dispatchers.IO) {
            AppDatabase.showCart(context, userId)  // or cartRepository.getCartItems(userId)
        }
        listings.clear()
        listings.addAll(cartListings)
        withContext(Dispatchers.Main) {
            adapter.update(listings)
        }
    }

    suspend fun removeFromCart(userId: Int, listingId: Int): Boolean {
        val isInCart = cartRepository.checkCart(userId, listingId)
        if (isInCart) {
            cartRepository.deleteFromCart(userId, listingId)
            return true
        }
        return false
    }

}