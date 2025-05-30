package com.example.lendit

import Converters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import EquipmentListing
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import androidx.appcompat.app.AlertDialog
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.example.lendit.data.local.ListingManager
import com.example.lendit.ui.archive.ArchiveFragment
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.lendit.data.local.entities.Favorite
import com.example.lendit.data.local.entities.UserCart
import com.example.lendit.data.local.managers.CartManager
import com.example.lendit.data.repository.RepositoryProvider
import com.example.lendit.ui.cart.CartFragment


class CartAdapter(
    private val context: Context,

    private val items: MutableList<EquipmentListing>) :
    RecyclerView.Adapter<CartAdapter.MyViewHolder>() {

    private var cartManager = CartManager(context, this)

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.listingTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.listingDescription)
        val categoryTextView: TextView = itemView.findViewById(R.id.listingCategory)
        val locationTextView: TextView = itemView.findViewById(R.id.listingLocation)
        val statusTextView: TextView = itemView.findViewById(R.id.listingStatus)
        val priceTextView: TextView = itemView.findViewById(R.id.listingPrice)
        val creationDateTextView: TextView = itemView.findViewById(R.id.listingCreationDate)
        val imageView: ImageView = itemView.findViewById(R.id.listingImage)
        val creator: TextView = itemView.findViewById(R.id.listingCreator)
        val favoriteButton : ImageButton = itemView.findViewById(R.id.imageFavoriteButtonListings)
        val cartButton : ImageButton = itemView.findViewById(R.id.removeFromCartButton)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false) // Use your actual layout file name
        return MyViewHolder(view)
    }

    fun getTotalPrice(): Double {
        return items.sumOf {
            it.price
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = items[position]
        val context = holder.itemView.context


        // Load other item data
        holder.titleTextView.text = currentItem.title


        val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)

        if (userId == -1) {
            Log.e("ListingAdapter", "User ID not found in SharedPreferences")
            return
        }

        Log.d("ListingAdapter", "onBindViewHolder for position $position, title: ${currentItem.title}") // <-- ADD LOG

        val photoList = Converters().fromString(currentItem.photos)

        if (photoList.isNotEmpty()) {
            Glide.with(holder.imageView.context)
                .load(photoList[0])
                .centerCrop()
                .into(holder.imageView)
        }

        holder.titleTextView.text = currentItem.title
        holder.descriptionTextView.text = currentItem.description
        holder.creator.text = "Creator: ${currentItem.ownerName}"
        holder.categoryTextView.text = "Category: ${currentItem.category}"
        holder.locationTextView.text = "Location: ${currentItem.location}"
        holder.statusTextView.text = "Status: ${currentItem.status.name}"
        holder.priceTextView.text = buildString {
            append(currentItem.price)
            append("€")
        }
        // Hide favorite button
        holder.favoriteButton.visibility = View.GONE
        holder.creationDateTextView.text = if (currentItem.creationDate != null) {
            "Listed on: ${currentItem.creationDate}"
        } else {
            "Date not available"
        }

        // Update the click behavior
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ListingDetailsActivity::class.java)
            intent.putExtra("listing_id", currentItem.listingId)
            context.startActivity(intent)
        }

        // Set click listener
        holder.cartButton.setOnClickListener {
            (context as AppCompatActivity).lifecycleScope.launch {
                val removed = cartManager.removeFromCart(userId, currentItem.listingId)
                withContext(Dispatchers.Main) {
                    if (removed) {
                        val index = cartManager.listings.indexOfFirst { it.listingId == currentItem.listingId }
                        if (index >= 0) {
                            cartManager.listings.removeAt(index)
                            cartManager.adapter.update(cartManager.listings)
                        }
                        Toast.makeText(context, "Removed from cart", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Item not in cart!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        if (currentItem.status == ListingStatus.INACTIVE) {
            holder.itemView.alpha = 0.7f
        } else {
            holder.itemView.alpha = 1.0f
        }
    }

    override fun getItemCount() = items.size

    fun update(newItems: List<EquipmentListing>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
