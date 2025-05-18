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

        // Load or observe your data and update the adapter
        // For example:
        // val sampleListings = getSampleData()
        // listingAdapter.update(sampleListings)
    }

    // Example function to get sample data (replace with your actual data source)
    // private fun getSampleData(): List<ListingEntity> {
    //     return listOf(
    //         ListingEntity(1, "Item 1", "Desc 1", "Electronics", "Location A", ListingStatus.AVAILABLE, 100.0, "2023-01-01"),
    //         ListingEntity(2, "Item 2", "Desc 2", "Books", "Location B", ListingStatus.SOLD, 25.0, "2023-01-05")
    //     )
    // }
}