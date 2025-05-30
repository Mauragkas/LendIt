package com.example.lendit.data.local.managers

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.lendit.data.local.entities.PaymentMethod
import com.example.lendit.data.repository.RepositoryProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Manager class for handling payment operations
 */
class PaymentManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val onPaymentSuccess: () -> Unit,
    private val onPaymentError: (String) -> Unit
) {
    private val orderRepository by lazy {
        RepositoryProvider.getOrderRepository(context)
    }

    private val cartRepository by lazy {
        RepositoryProvider.getCartRepository(context)
    }

    data class EncryptedCardData(
        val encryptedCardNumber: String,
        val encryptedExpiryDate: String,
        val encryptedCVV: String,
        val encryptedCardHolderName: String
    )

    data class EncryptediBan(
        val encryptedIban: String
    )

    /**
     * Process a credit card payment
     */
    fun processCreditCardPayment(cardNumber: String, expiryDate: String, cvv: String, cardHolderName: String) {
        // Validate inputs
        if (cardNumber.isBlank() || expiryDate.isBlank() || cvv.isBlank() || cardHolderName.isBlank()) {
            onPaymentError("Please fill in all card details")
            return
        }

        // Log the process
        Log.d("PaymentManager", "Processing credit card payment")

        // Encrypt sensitive data
        val encryptedData = encryptCardData(cardNumber, expiryDate, cvv, cardHolderName)

        // Validate with payment gateway
        val isValid = validateCardPayment(encryptedData)

        if (isValid) {
            // Get payment method
            val paymentMethod = PaymentMethod.CREDIT_CARD

            // Process order after successful payment validation
            processOrderWithPaymentMethod(paymentMethod)
        } else {
            onPaymentError("Credit card payment failed. Please check your details and try again.")
        }
    }

    /**
     * Process a bank transfer payment
     */
    fun processBankTransferPayment(iban: String) {
        // Validate input
        if (iban.isBlank()) {
            onPaymentError("Please enter your IBAN")
            return
        }

        // Log the process
        Log.d("PaymentManager", "Processing bank transfer payment")

        // Encrypt sensitive data
        val encryptedData = encryptIban(iban)

        // Validate with banking gateway
        val isValid = validateBankPayment(encryptedData)

        if (isValid) {
            // Get payment method
            val paymentMethod = PaymentMethod.BANK_TRANSFER

            // Process order after successful payment validation
            processOrderWithPaymentMethod(paymentMethod)
        } else {
            onPaymentError("Bank transfer failed. Please check your details and try again.")
        }
    }

    /**
     * Process an order with the given payment method
     */
    private fun processOrderWithPaymentMethod(paymentMethod: PaymentMethod) {
        // Get data from the PaymentActivity
        val activity = context as? Activity
        val intent = activity?.intent

        if (intent != null) {
            val userId = intent.getIntExtra("userId", -1)
            val price = intent.getDoubleExtra("price", 0.0)
            val listingIds = intent.getIntegerArrayListExtra("listingIds") ?: arrayListOf()
            val discountPercentage = intent.getIntExtra("discountPercentage", 0)

            processOrder(
                userId = userId,
                price = price,
                listingIds = listingIds.toList(),
                paymentMethod = paymentMethod,
                discountPercentage = discountPercentage,
                startDate = intent.getIntExtra("startDate", -1),
                endDate = intent.getIntExtra("endDate", -1)
            )
        } else {
            onPaymentError("Error processing payment: Invalid payment data")
        }
    }

    /**
     * Process an order with the specified parameters
     */
    fun processOrder(
        userId: Int,
        price: Double,
        listingIds: List<Int>,
        paymentMethod: PaymentMethod,
        discountPercentage: Int = 0,
        startDate: Int,
        endDate: Int
    ) {
        coroutineScope.launch {
            try {
                // Create order using repository
                val orderId = orderRepository.createOrderFromCart(
                    userId = userId,
                    listingIds = listingIds,
                    totalPrice = price,
                    paymentMethod = paymentMethod,
                    startDate = startDate,
                    endDate = endDate
                )

                // Clear cart
                clearCart(userId)

                // Notify success
                onPaymentSuccess()
            } catch (e: Exception) {
                Log.e("PaymentManager", "Error processing order", e)
                onPaymentError("Error processing payment: ${e.message}")
            }
        }
    }

    /**
     * Clear the user's cart after successful payment
     */
    private suspend fun clearCart(userId: Int) {
        try {
            cartRepository.deleteCart(userId)
        } catch (e: Exception) {
            Log.e("PaymentManager", "Error clearing cart", e)
            // Don't fail the whole transaction if cart clearing fails
        }
    }

    /**
     * Encrypt credit card data for secure transmission
     */
    private fun encryptCardData(
        cardNumber: String,
        expiryDate: String,
        cvv: String,
        cardHolderName: String
    ): EncryptedCardData {
        Log.d("PaymentManager", "Encrypting card data")
        // In a real app, this would use actual encryption
        return EncryptedCardData(
            encryptedCardNumber = "enc($cardNumber)",
            encryptedExpiryDate = "enc($expiryDate)",
            encryptedCVV = "enc($cvv)",
            encryptedCardHolderName = "enc($cardHolderName)"
        )
    }

    /**
     * Encrypt IBAN for secure transmission
     */
    private fun encryptIban(iban: String): EncryptediBan {
        Log.d("PaymentManager", "Encrypting IBAN")
        // In a real app, this would use actual encryption
        return EncryptediBan(
            encryptedIban = "enc($iban)"
        )
    }

    /**
     * Validate credit card payment with payment gateway
     */
    private fun validateCardPayment(encryptedCardData: EncryptedCardData): Boolean {
        Log.d("PaymentManager", "Validating card payment")

        // In a real app, this would make an API call to a payment gateway
        // For demo purposes, we'll validate test card data
        val validCard = (
                encryptedCardData.encryptedCardNumber == "enc(0000000000000000)" &&
                        encryptedCardData.encryptedExpiryDate == "enc(12/30)" &&
                        encryptedCardData.encryptedCVV == "enc(123)" &&
                        encryptedCardData.encryptedCardHolderName == "enc(JOHN DOE)"
                )

        Toast.makeText(context, "Επικοινωνία με την τράπεζα", Toast.LENGTH_SHORT).show()

        if (validCard) {
            Log.d("PaymentManager", "Card payment validated")
            Toast.makeText(context, "Payment validated", Toast.LENGTH_SHORT).show()
            return true
        } else {
            Log.d("PaymentManager", "Card payment invalid")
            return false
        }
    }

    /**
     * Validate bank transfer with banking gateway
     */
    private fun validateBankPayment(encryptedIban: EncryptediBan): Boolean {
        Log.d("PaymentManager", "Validating bank payment")

        // In a real app, this would make an API call to a banking system
        // For demo purposes, we'll validate test IBAN data
        val validIban = (encryptedIban.encryptedIban == "enc(000000000000000000000000000)")

        Toast.makeText(context, "Επικοινωνία με την τράπεζα", Toast.LENGTH_SHORT).show()

        if (validIban) {
            Log.d("PaymentManager", "Bank payment validated")
            Toast.makeText(context, "Payment validated", Toast.LENGTH_SHORT).show()
            return true
        } else {
            Log.d("PaymentManager", "Bank payment invalid")
            return false
        }
    }

}