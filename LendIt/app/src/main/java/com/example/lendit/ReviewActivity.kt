package com.example.lendit

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lendit.databinding.ActivityReviewBinding

class ReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the recycler view
        binding.reviewRecyclerView.layoutManager = LinearLayoutManager(this)

        // For demonstration purposes, we're showing empty state
        showEmptyState(true)

        // Set up back button
        binding.backButton.setOnClickListener { finish() }
    }

    private fun showEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.reviewRecyclerView.visibility = View.GONE
            binding.emptyStateText.visibility = View.VISIBLE
        } else {
            binding.reviewRecyclerView.visibility = View.VISIBLE
            binding.emptyStateText.visibility = View.GONE
        }
    }
}
