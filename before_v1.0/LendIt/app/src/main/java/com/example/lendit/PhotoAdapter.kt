package com.example.lendit

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class PhotoAdapter(
    private val photoUris: MutableList<Uri>,
    private val onRemoveClick: (Int) -> Unit
) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val photoImageView: ImageView = view.findViewById(R.id.photoImageView)
        val removeButton: ImageButton = view.findViewById(R.id.removePhotoButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uri = photoUris[position]
        holder.photoImageView.setImageURI(uri)

        // Use final position to avoid issues with RecyclerView position changes
        val finalPosition = position
        holder.removeButton.setOnClickListener {
            onRemoveClick(finalPosition)
        }
    }

    override fun getItemCount() = photoUris.size

    fun updatePhotos(photos: List<Uri>) {
        photoUris.clear()
        photoUris.addAll(photos)
        notifyDataSetChanged()
    }
}
