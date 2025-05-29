package com.example.lendit

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReviewAdapter(private val items: List<RentalItem>) :
        RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemImage: ImageView = view.findViewById(R.id.itemImage)
        val itemTitle: TextView = view.findViewById(R.id.itemTitle)
        val rentalDate: TextView = view.findViewById(R.id.rentalDate)
        val ownerName: TextView = view.findViewById(R.id.ownerName)
        val reviewButton: Button = view.findViewById(R.id.reviewButton)
        val reviewedStatusText: TextView = view.findViewById(R.id.reviewedStatusText) // Add this to your layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.review_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.itemTitle.text = item.title
        holder.rentalDate.text = "Rented on: ${item.rentalDate}"
        holder.ownerName.text = "Owner: ${item.ownerName}"

        // Load image with Glide
        if (item.imageUrl.isNotEmpty()) {
            Glide.with(holder.itemImage.context)
                    .load(item.imageUrl)
                    .centerCrop()
                    .into(holder.itemImage)
        }

        // Check if the item has already been reviewed
        (context as? AppCompatActivity)?.lifecycleScope?.launch {
            val db = AppDatabase.getInstance(context)
            val userId = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                    .getInt("userId", -1)

            val hasReviewed = withContext(Dispatchers.IO) {
                db.reviewDao().hasUserReviewedListing(userId, item.id)
            }

            withContext(Dispatchers.Main) {
                if (hasReviewed) {
                    // Item already reviewed
                    holder.reviewButton.visibility = View.GONE
                    holder.reviewedStatusText.visibility = View.VISIBLE
                    holder.reviewedStatusText.text = "✓ Reviewed"
                } else {
                    // Item not reviewed yet
                    holder.reviewButton.visibility = View.VISIBLE
                    holder.reviewedStatusText.visibility = View.GONE

                    // Set click listener for review button
                    holder.reviewButton.setOnClickListener {
                        val intent = Intent(context, WriteReviewActivity::class.java)
                        intent.putExtra("RENTAL_ID", item.id)
                        intent.putExtra("ITEM_TITLE", item.title)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    override fun getItemCount() = items.size
}
