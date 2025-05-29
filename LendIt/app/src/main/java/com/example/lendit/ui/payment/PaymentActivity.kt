package com.example.lendit.ui.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.lendit.MainActivity
import com.example.lendit.R
import com.example.lendit.data.local.ListingManager.Companion.updateListingStatus
import com.example.lendit.data.local.entities.Order
import com.example.lendit.data.local.entities.PaymentMethod
import kotlinx.coroutines.launch

class PaymentActivity : AppCompatActivity() {

    private lateinit var paymentMethodGroup: RadioGroup
    private lateinit var cardDetailsForm: View
    private lateinit var bankDetailsForm: View
    private lateinit var completePaymentButton: Button
    private lateinit var expiryEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        expiryEditText = findViewById(R.id.edit_expiry_date)
        bankDetailsForm = findViewById<View>(R.id.bank_details_form)
        paymentMethodGroup = findViewById(R.id.payment_method_group)
        cardDetailsForm = findViewById(R.id.card_details_form)
        completePaymentButton = findViewById(R.id.button_complete_payment)


        paymentMethodGroup.setOnCheckedChangeListener { _, checkedId ->
            completePaymentButton.visibility = View.VISIBLE

            when (checkedId) {
                R.id.radio_credit_card -> {
                    cardDetailsForm.visibility = View.VISIBLE
                    bankDetailsForm.visibility = View.GONE
                }
                R.id.radio_bank_transfer -> {
                    bankDetailsForm.visibility = View.VISIBLE
                    cardDetailsForm.visibility = View.GONE
                }
                else -> { cardDetailsForm.visibility = View.GONE; bankDetailsForm.visibility = View.GONE }
            }
        }

        expiryEditText.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return

                isFormatting = true

                val text = s.toString().replace("/", "")
                if (text.length >= 2) {
                    val month = text.substring(0, 2)
                    val year = text.substring(2).take(2)
                    expiryEditText.setText(if (year.isNotEmpty()) "$month/$year" else month)
                    expiryEditText.setSelection(expiryEditText.text.length)
                }

                isFormatting = false
            }
        })

        val userId = intent.getIntExtra("userId", -1)
        val price = intent.getDoubleExtra("price", 0.0)

        fun saveTransaction(order: Order) {
            lifecycleScope.launch {
                AppDatabase.getInstance(applicationContext).OrderDao().insert(order)
                Toast.makeText(this@PaymentActivity, "Η παραγγελία καταχωρήθηκε!", Toast.LENGTH_LONG).show()
                finish() // optionally close the activity
            }
        }

        data class EncryptedCardData(
            val encryptedCardNumber: String,
            val encryptedExpiryDate: String,
            val encryptedCVV: String,
            val encryptedCardHolderName: String
        )

        fun dataEncryption(
            cardNumber: String,
            expiryDate: String,
            cvv: String,
            cardHolderName: String
        ): EncryptedCardData {
            // return dummy "encrypted" values
            return EncryptedCardData(
                encryptedCardNumber = "enc($cardNumber)",
                encryptedExpiryDate = "enc($expiryDate)",
                encryptedCVV = "enc($cvv)",
                encryptedCardHolderName = "enc($cardHolderName)"
            )
        }

        fun validatePayment (encryptedCardData: EncryptedCardData) {
            // validate payment
        }


        // payMethod()
        completePaymentButton.setOnClickListener {
            val selectedPaymentId = paymentMethodGroup.checkedRadioButtonId
            lateinit var paymentMethod: PaymentMethod

            if (selectedPaymentId == R.id.radio_credit_card) {
                // Toast.makeText(this, "Επεξεργασία πληρωμής με κάρτα", Toast.LENGTH_SHORT).show()
                val cardNumber = findViewById<EditText>(R.id.edit_card_number).text.toString()
                val cvv = findViewById<EditText>(R.id.edit_cvv).text.toString()
                val cardHolderName = findViewById<EditText>(R.id.edit_cardholder_name).text.toString()

                val encryptedData = dataEncryption(cardNumber, expiryEditText.toString(), cvv, cardHolderName)
                validatePayment(encryptedData)

                paymentMethod = PaymentMethod.CREDIT_CARD
            } else if (selectedPaymentId == R.id.radio_cash_on_delivery) {
                // Toast.makeText(this, "Επεξεργασία πληρωμής μέσω αντικαταβολής", Toast.LENGTH_SHORT).show()
                paymentMethod = PaymentMethod.CASH_ON_DELIVERY
            } else if (selectedPaymentId == R.id.radio_bank_transfer) {
                // Toast.makeText(this, "Επεξεργασία πληρωμής μέσω τραπεζικής κατάθεσης", Toast.LENGTH_SHORT).show()
                paymentMethod = PaymentMethod.BANK_TRANSFER
            }
            // Save to Room
            // saveMethod(paymentMethod)
            val newOrder = Order(
                orderId = 0,
                renter = userId,
                price = price,
                paymentMethod = paymentMethod,
                listingId = intent.getIntExtra("listingId", -1),
                startDate = intent.getIntExtra("startDate", -1),
                endDate = intent.getIntExtra("endDate", -1)
            )
            saveTransaction(newOrder)
            // sendReceipt() will go here


            val listingIds = intent.getIntegerArrayListExtra("listingIds")

            listingIds?.forEach { listingId ->
                lifecycleScope.launch {
                    updateListingStatus(applicationContext, listingId, ListingStatus.UNAVAILABLE)
                }
            }

            clearCart(userId)
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun clearCart(userId: Int) {
        lifecycleScope.launch {
            AppDatabase.getInstance(applicationContext).cartDao().deleteCart(userId)
        }
    }
}
