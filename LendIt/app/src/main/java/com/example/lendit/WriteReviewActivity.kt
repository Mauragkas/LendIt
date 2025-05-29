package com.example.lendit

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class WriteReviewActivity : AppCompatActivity() {

    private lateinit var itemTitleText: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var reviewText: TextInputEditText
    private lateinit var submitButton: Button
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_review)

        // Get data from intent
        val itemTitle = intent.getStringExtra("ITEM_TITLE") ?: "Unknown Item"
        val rentalId = intent.getIntExtra("RENTAL_ID", -1)

        // Initialize views
        itemTitleText = findViewById(R.id.itemTitleText)
        ratingBar = findViewById(R.id.ratingBar)
        reviewText = findViewById(R.id.reviewText)
        submitButton = findViewById(R.id.submitButton)
        backButton = findViewById(R.id.backButton)

        // Set item title
        itemTitleText.text = itemTitle

        // Set up back button
        backButton.setOnClickListener { finish() }

        // Set up submit button
        submitButton.setOnClickListener {
            val rating = ratingBar.rating
            val review = reviewText.text.toString().trim()

            if (rating == 0f) {
                Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (review.isEmpty()) {
                Toast.makeText(this, "Please write a review", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // This would normally submit the review to the backend
            // For UI demo, we'll just show a success message and finish
            Toast.makeText(this, "Review submitted successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
