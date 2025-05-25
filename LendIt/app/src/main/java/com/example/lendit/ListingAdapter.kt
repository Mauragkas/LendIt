package com.example.lendit

import Converters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import EquipmentListing
import android.content.Intent
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import androidx.appcompat.app.AlertDialog
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.lendit.data.local.ListingManager
import com.example.lendit.ui.archive.ArchiveFragment
import kotlinx.coroutines.launch

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
        val imageView: ImageView = itemView.findViewById(R.id.listingImage)
        val creator: TextView = itemView.findViewById(R.id.listingCreator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false) // Use your actual layout file name
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = items[position]

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

        // Add long click listener for context menu
        holder.itemView.setOnLongClickListener { view ->
            val context = view.context
            val popup = PopupMenu(context, view)

            val inflater = popup.menuInflater

            when (currentItem.status) {
                ListingStatus.AVAILABLE -> {
                    inflater.inflate(R.menu.available_listing_menu, popup.menu)
                }
                ListingStatus.UNAVAILABLE -> {
                    inflater.inflate(R.menu.unavailable_listing_menu, popup.menu)
                }
                ListingStatus.INACTIVE -> {
                    inflater.inflate(R.menu.inactive_listing_menu, popup.menu)
                }
            }

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> {
                        val intent = Intent(context, ListingActivity::class.java)
                        intent.putExtra("edit_listing_id", currentItem.listingId)
                        context.startActivity(intent)
                        true
                    }
                    R.id.action_deactivate -> {
                        // Show confirmation dialog
                        AlertDialog.Builder(context)
                            .setTitle("Απενεργοποίηση Αγγελίας")
                            .setMessage("Είστε βέβαιοι ότι θέλετε να απενεργοποιήσετε αυτήν την αγγελία;")
                            .setPositiveButton("Ναι") { _, _ ->
                                (context as? AppCompatActivity)?.lifecycleScope?.launch {
                                    if (ListingManager.updateListingStatus(context, currentItem.listingId, ListingStatus.INACTIVE)) {
                                        Toast.makeText(context, "Η αγγελία απενεργοποιήθηκε", Toast.LENGTH_SHORT).show()
                                        (context.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
                                            ?.childFragmentManager?.fragments?.firstOrNull() as? ArchiveFragment)
                                            ?.refreshListings()
                                    }
                                }
                            }
                            .setNegativeButton("Όχι", null)
                            .show()
                        true
                    }
                    R.id.action_activate -> {
                        // Show confirmation dialog
                        AlertDialog.Builder(context)
                            .setTitle("Ενεργοποίηση Αγγελίας")
                            .setMessage("Είστε βέβαιοι ότι θέλετε να ενεργοποιήσετε αυτήν την αγγελία;")
                            .setPositiveButton("Ναι") { _, _ ->
                                (context as? AppCompatActivity)?.lifecycleScope?.launch {
                                    if (ListingManager.updateListingStatus(context, currentItem.listingId, ListingStatus.AVAILABLE)) {
                                        Toast.makeText(context, "Η αγγελία ενεργοποιήθηκε", Toast.LENGTH_SHORT).show()
                                        (context.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
                                            ?.childFragmentManager?.fragments?.firstOrNull() as? ArchiveFragment)
                                            ?.refreshListings()
                                    }
                                }
                            }
                            .setNegativeButton("Όχι", null)
                            .show()
                        true
                    }
                    else -> false
                }
            }

            popup.show()
            true
        }

        // Visual indication for inactive listings
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
