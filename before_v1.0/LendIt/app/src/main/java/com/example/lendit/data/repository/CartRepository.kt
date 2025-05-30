package com.example.lendit.data.repository

import CartDao
import EquipmentListing
import com.example.lendit.data.local.entities.UserCart

class CartRepository(private val cartDao: CartDao) {

    suspend fun insert(userCart: UserCart): Long {
        return cartDao.insert(userCart)
    }

    suspend fun checkCart(userId: Int, listingId: Int): Boolean {
        return cartDao.checkCart(userId, listingId)
    }

    suspend fun deleteFromCart(userId: Int, listingId: Int) {
        cartDao.deleteFromCart(userId, listingId)
    }

    suspend fun deleteCart(userId: Int) {
        cartDao.deleteCart(userId)
    }

    suspend fun getCartById(userId: Int): List<EquipmentListing> {
        return cartDao.getCartById(userId)
    }

    suspend fun getCartCount(userId: Int): Int {
        return cartDao.getCartCount(userId)
    }
}
