package com.example.lendit

import Converters
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import EquipmentListing
import ListingStatus
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.lendit.data.local.entities.Favorite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesAdapter(private val items: MutableList<EquipmentListing>) :
    RecyclerView.Adapter<FavoritesAdapter.MyViewHolder>() {

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
        val favoriteButton: ImageButton = itemView.findViewById(R.id.imageFavoriteButtonListings)
        val cartButton : ImageButton = itemView.findViewById(R.id.removeFromCartButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = items[position]
        val context = holder.itemView.context

        val db = AppDatabase.getDatabase(context)
        val favoriteDao = db.FavoriteDao()
        val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)

        (context as AppCompatActivity).lifecycleScope.launch(Dispatchers.IO) {
            val isFavorite = favoriteDao.isFavorite(userId, currentItem.listingId)
            withContext(Dispatchers.Main) {
                holder.favoriteButton.setImageResource(
                    if (isFavorite) R.drawable.ic_favorite_filled
                    else R.drawable.ic_favorite_border
                )
            }
        }

        holder.favoriteButton.setOnClickListener {
            (context as AppCompatActivity).lifecycleScope.launch(Dispatchers.IO) {
                val isFavorite = favoriteDao.isFavorite(userId, currentItem.listingId)
                if (isFavorite) {
                    favoriteDao.removeFromFavorites(userId, currentItem.listingId)
                    withContext(Dispatchers.Main) {
                        holder.favoriteButton.setImageResource(R.drawable.ic_favorite_border)
                        Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    favoriteDao.addToFavorites(Favorite(userId = userId, listingId = currentItem.listingId))
                    withContext(Dispatchers.Main) {
                        holder.favoriteButton.setImageResource(R.drawable.ic_favorite_filled)
                        Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        holder.titleTextView.text = currentItem.title
        holder.descriptionTextView.text = currentItem.description
        holder.creator.text = "Creator: ${currentItem.ownerName}"
        holder.categoryTextView.text = "Category: ${currentItem.category}"
        holder.locationTextView.text = "Location: ${currentItem.location}"
        holder.statusTextView.text = "Status: ${currentItem.status.name}"
        holder.priceTextView.text = "${currentItem.price}€"

        val photoList = Converters().fromString(currentItem.photos)
        if (photoList.isNotEmpty()) {
            Glide.with(holder.imageView.context)
                .load(photoList[0])
                .centerCrop()
                .into(holder.imageView)
        }

        holder.creationDateTextView.text = if (currentItem.creationDate != null) {
            "Listed on: ${currentItem.creationDate}"
        } else {
            "Date not available"
        }

        holder.cartButton.visibility = View.GONE
        // Fade out inactive items
        holder.itemView.alpha = if (currentItem.status == ListingStatus.INACTIVE) 0.7f else 1.0f

        // On click -> go to listing details
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ListingDetailsActivity::class.java)
            intent.putExtra("listing_id", currentItem.listingId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size

    fun update(newItems: List<EquipmentListing>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}