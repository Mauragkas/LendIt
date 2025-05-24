package com.example.lendit

import AppDatabase
import Converters
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class ListingDetailsActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing_details)

        val listingId = intent.getIntExtra("listing_id", -1)
        val db = AppDatabase.getInstance(applicationContext)


        lifecycleScope.launch {
            val listing = db.listingDao().getListingById(listingId)

            if (listing != null) {
                // Populate UI
                val photoList = Converters().fromString(listing.photos)

                // image
                if (photoList.isNotEmpty()) {
                    val imageView = findViewById<ImageView>(R.id.listingImage)

                    Glide.with(this@ListingDetailsActivity)
                        .load(photoList[0])
                        .centerCrop()
                        .into(imageView)
                }

                //text fields
                findViewById<TextView>(R.id.listingTitle).text = listing.title
                findViewById<TextView>(R.id.listingDescription).text = listing.description
                findViewById<TextView>(R.id.listingCategory).text = listing.category.name
                findViewById<TextView>(R.id.listingLocation).text = listing.location.name
                findViewById<TextView>(R.id.listingStatus).text = listing.status.name

                findViewById<TextView>(R.id.listingPrice).text = buildString {
                    append(listing.price)
                    append("€")
                }

                findViewById<TextView>(R.id.listingCreationDate).text = listing.creationDate.toString()
            } else {
                // Listing not found
            }
        }
    }
}
