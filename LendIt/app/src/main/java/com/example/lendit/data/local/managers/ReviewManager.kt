package com.example.lendit.data.local.managers

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.example.lendit.data.local.entities.Review
import com.example.lendit.data.repository.RepositoryProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReviewManager(
    private val context: Context,
    private val lifecycleScope: CoroutineScope,
    private val listingId: Int,
    private val onReviewSubmitted: (Int) -> Unit,
    private val onError: (String) -> Unit
) {
    private val selectedPhotos = mutableListOf<Uri>()

    private val reviewRepository by lazy {
        RepositoryProvider.getReviewRepository(context)
    }

    // For identifying inappropriate content
    private val inappropriateWords =
            listOf("shit", "fuck", "ass", "dick", "bitch", "bastard", "cunt")

    fun getPhotos(): MutableList<Uri> = selectedPhotos

    fun getPhotoCount(): Int = selectedPhotos.size

    fun addPhoto(uri: Uri): Boolean {
        return if (selectedPhotos.size < 5) {
            selectedPhotos.add(uri)
            true
        } else {
            onError("Maximum 5 photos allowed")
            false
        }
    }

    fun removePhoto(position: Int): Boolean {
        return if (position >= 0 && position < selectedPhotos.size) {
            selectedPhotos.removeAt(position)
            true
        } else {
            false
        }
    }

    private fun containsInappropriateContent(text: String): Boolean {
        val lowerText = text.lowercase()
        return inappropriateWords.any { lowerText.contains(it) }
    }

    fun submitReview(rating: Float, comment: String) {
        // Check for inappropriate content
        if (containsInappropriateContent(comment)) {
            onError("Your review contains inappropriate content. Please revise it.")
            return
        }

        lifecycleScope.launch {
            try {
                // Get user ID from SharedPreferences
                val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val userId = sharedPref.getInt("userId", -1)

                if (userId == -1) {
                    onError("Please log in to submit a review")
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
                reviewRepository.addReview(review)
                reviewRepository.markRentalAsReviewed(userId, listingId)

                withContext(Dispatchers.Main) {
                    onReviewSubmitted(listingId)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Error submitting review: ${e.message}")
                }
            }
        }
    }
}
