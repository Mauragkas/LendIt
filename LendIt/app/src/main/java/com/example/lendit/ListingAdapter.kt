package com.example.lendit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import EquipmentListing
import android.util.Log

// Make sure EquipmentListing is accessible. If it's in the same package, no explicit import is needed.
// If EquipmentListing is in a different package, you would import it here.

class ListingAdapter(private val items: MutableList<EquipmentListing>) :
    RecyclerView.Adapter<ListingAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.listingTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.listingDescription)
        val categoryTextView: TextView = itemView.findViewById(R.id.listingCategory)
        val locationTextView: TextView = itemView.findViewById(R.id.listingLocation)
        val statusTextView: TextView = itemView.findViewById(R.id.listingStatus)
        val priceTextView: TextView = itemView.findViewById(R.id.listingPrice)
        val creationDateTextView: TextView = itemView.findViewById(R.id.listingCreationDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false) // Use your actual layout file name
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = items[position]

        Log.d("ListingAdapter", "onBindViewHolder for position $position, title: ${currentItem.title}") // <-- ADD LOG

        holder.titleTextView.text = currentItem.title
        holder.descriptionTextView.text = currentItem.description
        holder.categoryTextView.text = "Category: ${currentItem.category}"
        holder.locationTextView.text = "Location: ${currentItem.location}"
        holder.statusTextView.text = "Status: ${currentItem.status.name}"
        holder.priceTextView.text = String.format("$%.2f", currentItem.price)

        holder.creationDateTextView.text = if (currentItem.creationDate != null) {
            "Listed on: ${currentItem.creationDate}"
        } else {
            "Date not available"
        }
    }

    override fun getItemCount() = items.size

    fun update(newItems: List<EquipmentListing>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}