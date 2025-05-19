package com.example.lendit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
// Make sure ListingEntity is accessible

class ListingActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var listingAdapter: ListingAdapter // Declare adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_listings) // Make sure you have this layout file

        recyclerView = findViewById(R.id.listingRecyclerView) // Replace with your RecyclerView's ID
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize your adapter with an empty list or initial data
        listingAdapter = ListingAdapter(mutableListOf())
        recyclerView.adapter = listingAdapter

    }
}