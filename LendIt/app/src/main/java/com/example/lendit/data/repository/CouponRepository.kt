package com.example.lendit.data.repository

import com.example.lendit.data.local.dao.CouponDao
import com.example.lendit.data.local.entities.Coupon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for handling coupon-related operations.
 */
class CouponRepository(private val couponDao: CouponDao) {

    suspend fun insertCoupon(coupon: Coupon): Long {
        return couponDao.insert(coupon)
    }

    suspend fun validateCoupon(code: String): Int? {
        return try {
            couponDao.validateCoupon(code)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAllCoupons(): List<Coupon> {
        return couponDao.getAllCoupons()
    }
}


