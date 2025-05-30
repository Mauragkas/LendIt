package com.example.lendit.data.local.managers

import android.content.Context
import android.util.Log
import com.example.lendit.data.local.entities.PaymentMethod
import com.example.lendit.data.repository.RepositoryProvider

data class EncryptedCardData(
    val encryptedCardNumber: String,
    val encryptedExpiryDate: String,
    val encryptedCVV: String,
    val encryptedCardHolderName: String
)

data class EncryptediBan(val encryptedIban: String)

class PaymentManager {
    suspend fun processPaymentAndCreateOrder(
        context: Context,
        userId: Int,
        listingIds: List<Int>,
        totalPrice: Double,
        paymentMethod: PaymentMethod,
        startDate: Int,
        endDate: Int
    ): Boolean {
        val orderRepository = RepositoryProvider.getOrderRepository(context)
        val cartRepository = RepositoryProvider.getCartRepository(context)

        return try {
            orderRepository.createOrderFromCart(
                userId = userId,
                listingIds = listingIds,
                totalPrice = totalPrice,
                paymentMethod = paymentMethod,
                startDate = startDate,
                endDate = endDate
            )
            cartRepository.deleteCart(userId)
            true
        } catch (e: Exception) {
            Log.e("PaymentManager", "Failed to create order: ${e.message}")
            false
        }
    }


    fun encryptCardData(
        cardNumber: String,
        expiryDate: String,
        cvv: String,
        cardHolderName: String
    ): EncryptedCardData {
        Log.d("PaymentManager", "Encrypting card data")
        return EncryptedCardData(
            encryptedCardNumber = "enc($cardNumber)",
            encryptedExpiryDate = "enc($expiryDate)",
            encryptedCVV = "enc($cvv)",
            encryptedCardHolderName = "enc($cardHolderName)"
        )
    }

    fun encryptIban(iBan: String): EncryptediBan {
        Log.d("PaymentManager", "Encrypting IBAN")
        return EncryptediBan(encryptedIban = "enc($iBan)")
    }

    fun validateCardData(data: EncryptedCardData): Boolean {
        Log.d("PaymentManager", "Validating card data: $data")
        return data.encryptedCardNumber == "enc(0000000000000000)" &&
                data.encryptedExpiryDate == "enc(12/30)" &&
                data.encryptedCVV == "enc(123)" &&
                data.encryptedCardHolderName == "enc(JOHN DOE)"
    }

    fun validateIban(data: EncryptediBan): Boolean {
        Log.d("PaymentManager", "Validating IBAN data: $data")
        return data.encryptedIban == "enc(000000000000000000000000000)"
    }

    fun shouldSendReceipt(isValid: Boolean): Boolean {
        return isValid
    }
}
