package com.example.lendit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lendit.data.local.entities.Review
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WriteReviewActivity : AppCompatActivity() {

    private lateinit var itemTitleText: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var reviewText: TextInputEditText
    private lateinit var submitButton: Button
    private lateinit var backButton: ImageButton
    private lateinit var addPhotoButton: Button
    private lateinit var photosRecyclerView: RecyclerView

    private val selectedPhotos = mutableListOf<Uri>()
    private lateinit var photoAdapter: PhotoAdapter

    // For identifying inappropriate content
    private val inappropriateWords =
            listOf("shit", "fuck", "ass", "dick", "bitch", "bastard", "cunt")

    private val getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.data?.let { uri ->
                        if (selectedPhotos.size < 5) {
                            selectedPhotos.add(uri)
                            photoAdapter.updatePhotos(selectedPhotos)
                        } else {
                            Toast.makeText(this, "Maximum 5 photos allowed", Toast.LENGTH_SHORT)
                                    .show()
                        }
                    }
                }
            }

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
        addPhotoButton = findViewById(R.id.addPhotoButton)
        photosRecyclerView = findViewById(R.id.photosRecyclerView)

        // Set item title
        itemTitleText.text = itemTitle

        // Setup photos recycler view
        photosRecyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        photoAdapter =
                PhotoAdapter(selectedPhotos) { position ->
                    if (position >= 0 && position < selectedPhotos.size) {
                        selectedPhotos.removeAt(position)
                        photoAdapter.notifyDataSetChanged()
                    }
                }
        photosRecyclerView.adapter = photoAdapter

        // Set up back button
        backButton.setOnClickListener { finish() }

        // Set up add photo button
        addPhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getContent.launch(intent)
        }

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

            // Check for inappropriate content
            if (containsInappropriateContent(review)) {
                Toast.makeText(
                                this,
                                "Your review contains inappropriate content. Please revise it.",
                                Toast.LENGTH_LONG
                        )
                        .show()
                return@setOnClickListener
            }

            // Submit review
            submitReview(rentalId, rating, review)
        }
    }

    private fun containsInappropriateContent(text: String): Boolean {
        val lowerText = text.lowercase()
        return inappropriateWords.any { lowerText.contains(it) }
    }

    private fun submitReview(listingId: Int, rating: Float, comment: String) {
        lifecycleScope.launch {
            try {
                // Get user ID from SharedPreferences
                val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                val userId = sharedPref.getInt("userId", -1)

                if (userId == -1) {
                    Toast.makeText(
                                    this@WriteReviewActivity,
                                    "Please log in to submit a review",
                                    Toast.LENGTH_SHORT
                            )
                            .show()
                    return@launch
                }

                // Convert photo URIs to string
                val photoUrisString = selectedPhotos.joinToString(",") { it.toString() }

                // Create review object
                val review =
                        Review(
                                userId = userId,
                                listingId = listingId,
                                rating = rating,
                                comment = comment,
                                photos = photoUrisString
                        )

                // Save to database
                withContext(Dispatchers.IO) {
                    val db = AppDatabase.getInstance(this@WriteReviewActivity)
                    db.reviewDao().insert(review)
                    db.rentalDao().markAsReviewed(userId, listingId)
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                                    this@WriteReviewActivity,
                                    "Review submitted successfully",
                                    Toast.LENGTH_SHORT
                            )
                            .show()

                    // Navigate back to previous screen
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                                    this@WriteReviewActivity,
                                    "Error submitting review: ${e.message}",
                                    Toast.LENGTH_SHORT
                            )
                            .show()
                }
            }
        }
    }
}
