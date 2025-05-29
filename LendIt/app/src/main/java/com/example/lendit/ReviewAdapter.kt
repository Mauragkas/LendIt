package com.example.lendit

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ReviewAdapter(private val items: List<RentalItem>) :
        RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    // This class is just for UI demonstration and doesn't correspond to a real entity
    data class RentalItem(
            val id: Int,
            val title: String,
            val imageUrl: String,
            val rentalDate: String,
            val ownerName: String
    )

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemImage: ImageView = view.findViewById(R.id.itemImage)
        val itemTitle: TextView = view.findViewById(R.id.itemTitle)
        val rentalDate: TextView = view.findViewById(R.id.rentalDate)
        val ownerName: TextView = view.findViewById(R.id.ownerName)
        val reviewButton: Button = view.findViewById(R.id.reviewButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.review_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.itemTitle.text = item.title
        holder.rentalDate.text = "Rented on: ${item.rentalDate}"
        holder.ownerName.text = "Owner: ${item.ownerName}"

        // Load image with Glide
        Glide.with(holder.itemImage.context).load(item.imageUrl).centerCrop().into(holder.itemImage)

        // Set click listener for review button
        holder.reviewButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, WriteReviewActivity::class.java)
            intent.putExtra("RENTAL_ID", item.id)
            intent.putExtra("ITEM_TITLE", item.title)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = items.size
}
