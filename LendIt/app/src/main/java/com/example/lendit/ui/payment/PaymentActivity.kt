package com.example.lendit.ui.payment

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.lendit.R

class PaymentActivity : AppCompatActivity() {

    private lateinit var paymentMethodGroup: RadioGroup
    private lateinit var cardDetailsForm: View
    private lateinit var completePaymentButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        paymentMethodGroup = findViewById(R.id.payment_method_group)
        cardDetailsForm = findViewById(R.id.card_details_form)
        completePaymentButton = findViewById(R.id.button_complete_payment)


        paymentMethodGroup.setOnCheckedChangeListener { _, checkedId ->
            completePaymentButton.visibility = View.VISIBLE

            when (checkedId) {
                R.id.radio_credit_card -> cardDetailsForm.visibility = View.VISIBLE
                else -> cardDetailsForm.visibility = View.GONE
            }
        }

        completePaymentButton.setOnClickListener {
            val selectedPaymentId = paymentMethodGroup.checkedRadioButtonId
            if (selectedPaymentId == -1) {
                Toast.makeText(this, "Παρακαλώ επιλέξτε τρόπο πληρωμής", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedPaymentId == R.id.radio_credit_card) {
                Toast.makeText(this, "Επεξεργασία πληρωμής με κάρτα", Toast.LENGTH_SHORT).show()
            } else if (selectedPaymentId == R.id.radio_cash_on_delivery) {
                Toast.makeText(this, "Επεξεργασία πληρωμής μέσω αντικαταβολής", Toast.LENGTH_SHORT).show()
            } else if (selectedPaymentId == R.id.radio_bank_transfer) {
                Toast.makeText(this, "Επεξεργασία πληρωμής μέσω τραπεζικής κατάθεσης", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
