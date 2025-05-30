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
import com.example.lendit.data.local.managers.ReviewManager
import com.google.android.material.textfield.TextInputEditText

class WriteReviewActivity : AppCompatActivity() {

    private lateinit var itemTitleText: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var reviewText: TextInputEditText
    private lateinit var submitButton: Button
    private lateinit var backButton: ImageButton
    private lateinit var addPhotoButton: Button
    private lateinit var photosRecyclerView: RecyclerView
    private lateinit var reviewManager: ReviewManager
    private lateinit var photoAdapter: PhotoAdapter

    private val getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.data?.let { uri ->
                        reviewManager.addPhoto(uri)
                        updatePhotosList()
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

        // Initialize ReviewManager
        reviewManager = ReviewManager(
            context = this,
            lifecycleScope = lifecycleScope,
            listingId = rentalId,
            onReviewSubmitted = { listingId ->
                Toast.makeText(this, "Review submitted successfully", Toast.LENGTH_SHORT).show()
                // Navigate to listing details
                val intent = Intent(this, ListingDetailsActivity::class.java)
                intent.putExtra("listing_id", listingId)
                startActivity(intent)
                finish()
            },
            onError = { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        )

        // Setup photos recycler view
        setupPhotosRecyclerView()

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

            // Submit through manager
            reviewManager.submitReview(rating, review)
        }
    }

    private fun setupPhotosRecyclerView() {
        photosRecyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        photoAdapter =
                PhotoAdapter(reviewManager.getPhotos()) { position ->
                    reviewManager.removePhoto(position)
                    updatePhotosList()
                }
        photosRecyclerView.adapter = photoAdapter
    }

    private fun updatePhotosList() {
        photoAdapter.notifyDataSetChanged()
        Toast.makeText(
                this,
                "Photos: ${reviewManager.getPhotoCount()}/5",
                Toast.LENGTH_SHORT
        ).show()
    }
}
