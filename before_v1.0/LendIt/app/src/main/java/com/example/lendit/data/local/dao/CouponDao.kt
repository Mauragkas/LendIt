package com.example.lendit.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.lendit.data.local.entities.Coupon

@Dao
interface CouponDao {
    @Insert
    suspend fun insert(coupon: Coupon): Long

    @Query("SELECT discountPercentage FROM coupon WHERE code = :couponCode LIMIT 1")
    suspend fun validateCoupon(couponCode: String): Int

    @Query("SELECT * FROM coupon")
    suspend fun getAllCoupons(): List<Coupon>
}