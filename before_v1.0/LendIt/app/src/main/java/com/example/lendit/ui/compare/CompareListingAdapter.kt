package com.example.lendit.ui.compare

import Converters
import EquipmentListing
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lendit.R

class CompareListingAdapter(
        private val items: MutableList<EquipmentListing>,
        private val onItemSelected: (EquipmentListing) -> Unit
) : RecyclerView.Adapter<CompareListingAdapter.ViewHolder>() {

    private val selectedItems = mutableSetOf<Int>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.compareItemTitle)
        val priceTextView: TextView = view.findViewById(R.id.compareItemPrice)
        val categoryTextView: TextView = view.findViewById(R.id.compareItemCategory)
        val imageView: ImageView = view.findViewById(R.id.compareItemImage)
        val checkbox: CheckBox = view.findViewById(R.id.compareItemCheckbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_compare_listing, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items[position]

        holder.titleTextView.text = currentItem.title
        holder.priceTextView.text = "${currentItem.price}€"
        holder.categoryTextView.text = "Category: ${currentItem.category}"

        // Set checkbox state
        holder.checkbox.isChecked = selectedItems.contains(currentItem.listingId)

        // Load image
        val photoList = Converters().fromString(currentItem.photos)
        if (photoList.isNotEmpty()) {
            Glide.with(holder.imageView.context)
                    .load(photoList[0])
                    .centerCrop()
                    .into(holder.imageView)
        }

        // Set up click listeners
        holder.itemView.setOnClickListener {
            toggleSelection(currentItem)
            holder.checkbox.isChecked = selectedItems.contains(currentItem.listingId)
            onItemSelected(currentItem)
        }

        holder.checkbox.setOnClickListener {
            toggleSelection(currentItem)
            holder.checkbox.isChecked = selectedItems.contains(currentItem.listingId)
            onItemSelected(currentItem)
        }
    }

    private fun toggleSelection(listing: EquipmentListing) {
        if (selectedItems.contains(listing.listingId)) {
            selectedItems.remove(listing.listingId)
        } else {
            selectedItems.add(listing.listingId)
        }
    }

    fun clearSelections() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size


    fun update(newItems: List<EquipmentListing>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
