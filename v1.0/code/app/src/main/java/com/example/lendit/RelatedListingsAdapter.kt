package com.example.lendit

import EquipmentListing
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.view.LayoutInflater
import android.view.ViewGroup


class RelatedListingsAdapter(
    private var listings: List<EquipmentListing>,
    private val onItemClick: (EquipmentListing) -> Unit
) : RecyclerView.Adapter<RelatedListingsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.listingImage)
        val title = view.findViewById<TextView>(R.id.listingTitle)

        init {
            view.setOnClickListener {
                onItemClick(listings[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_related_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = listings.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listing = listings[position]
        holder.title.text = listing.title
        val photoList = listing.photos.split(",").filter { it.isNotEmpty() }
        Glide.with(holder.image.context)
            .load(photoList.firstOrNull())
            .into(holder.image)
    }
    fun update(newListings: List<EquipmentListing>) {
        listings = newListings
        notifyDataSetChanged()
    }

}