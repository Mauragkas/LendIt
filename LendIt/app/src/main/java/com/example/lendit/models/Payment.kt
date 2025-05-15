package com.example.lendit.models

import java.time.LocalDateTime

enum class PaymentMethod {
    CREDIT_CARD,
    PAYPAL,
    BANK_TRANSFER
}

enum class PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED
}

data class Payment(
    val paymentId: String,
    val amount: Float,
    val method: PaymentMethod,
    var status: PaymentStatus = PaymentStatus.PENDING,
    val date: LocalDateTime = LocalDateTime.now()
) {
    fun process(): Boolean {
        // Implementation
        return true
    }

    fun verify(): Boolean {
        // Implementation
        return true
    }
}
