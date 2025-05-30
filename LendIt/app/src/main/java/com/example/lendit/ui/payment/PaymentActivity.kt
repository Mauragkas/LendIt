package com.example.lendit.ui.payment

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.lendit.R
import com.example.lendit.data.local.entities.PaymentMethod
import com.example.lendit.data.local.managers.PaymentManager

class PaymentActivity : AppCompatActivity() {

    private lateinit var paymentMethodGroup: RadioGroup
    private lateinit var cardDetailsForm: View
    private lateinit var bankDetailsForm: View
    private lateinit var completePaymentButton: Button
    private lateinit var expiryEditText: EditText

    // Create payment manager
    private lateinit var paymentManager: PaymentManager

    private fun sendReceipt() {
        Toast.makeText(this@PaymentActivity, "Η παραγγελία καταχωρήθηκε!", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Initialize payment manager
        paymentManager = PaymentManager(
            context = this,
            coroutineScope = lifecycleScope,
            onPaymentSuccess = {
                Toast.makeText(this, "Payment processed successfully", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            },
            onPaymentError = { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                setResult(RESULT_CANCELED)
                finish()
            }
        )

        expiryEditText = findViewById(R.id.edit_expiry_date)
        bankDetailsForm = findViewById<View>(R.id.bank_details_form)
        paymentMethodGroup = findViewById(R.id.payment_method_group)
        cardDetailsForm = findViewById(R.id.card_details_form)
        completePaymentButton = findViewById(R.id.button_complete_payment)

        fun showForm(checkedId: Int){
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
        paymentMethodGroup.setOnCheckedChangeListener { _, checkedId ->
            showForm(checkedId)
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

        // Complete payment button click handler
        completePaymentButton.setOnClickListener {
            val selectedPaymentId = paymentMethodGroup.checkedRadioButtonId

            when (selectedPaymentId) {
                R.id.radio_credit_card -> {
                    val cardNumber = findViewById<EditText>(R.id.edit_card_number).text.toString()
                    val cvv = findViewById<EditText>(R.id.edit_cvv).text.toString()
                    val cardHolderName = findViewById<EditText>(R.id.edit_cardholder_name).text.toString()
                    val expiryDate = expiryEditText.text.toString().trim()

                    paymentManager.processCreditCardPayment(cardNumber, expiryDate, cvv, cardHolderName)
                }
                R.id.radio_cash_on_delivery -> {
                    paymentManager.processCashOnDeliveryPayment()
                }
                R.id.radio_bank_transfer -> {
                    val iban = findViewById<EditText>(R.id.edit_bank_number).text.toString()
                    paymentManager.processBankTransferPayment(iban)
                }
                else -> {
                    Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}