package com.example.lendit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainOwnerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_owner)

        // Find the button and set click listener
        val newListingButton = findViewById<MaterialButton>(R.id.new_listing_button_main)

        newListingButton.setOnClickListener {
            // Create an intent to start ListingActivity
            val intent = Intent(this, ListingActivity::class.java)
            startActivity(intent)

        }
    }
}