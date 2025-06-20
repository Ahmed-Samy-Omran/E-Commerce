package com.example.ui.reviews.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.R
import com.example.e_commerce.databinding.ItemAddReviewImageBinding
import com.example.e_commerce.databinding.ItemReviewImageBinding

// Adapter for displaying selected review images + an "Add Image" button at the end
class ReviewImageAdapter(
    private val imageUris: MutableList<Uri>,   // List of selected image URIs
    private val onAddClick: () -> Unit         // Lambda to trigger image picker when "+" is clicked
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_IMAGE = 1        // View type for regular image item
        private const val TYPE_ADD_BUTTON = 2   // View type for the "+" add button
    }

    // Decide which view type to show based on position
    override fun getItemViewType(position: Int): Int {
        return if (position == imageUris.size) TYPE_ADD_BUTTON else TYPE_IMAGE
        // Last item is always the add button
    }

    // Create the appropriate ViewHolder based on view type
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ADD_BUTTON) {
            // Inflate layout for the "+" button
            val binding= ItemAddReviewImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            AddViewHolder(binding.root)
        } else {
            // Inflate layout for a selected image
            val binding= ItemReviewImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ImageViewHolder(binding.root)


        }
    }

    // Total items = number of selected images + 1 (for the "+" button)
    override fun getItemCount(): Int = imageUris.size + 1

    // Bind the data to the correct ViewHolder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImageViewHolder) {
            // Show the image at this position
            holder.bind(imageUris[position])
        } else if (holder is AddViewHolder) {
            // Set click listener to open image picker
            holder.itemView.setOnClickListener { onAddClick() }
        }
    }

    // ViewHolder to show selected image
    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image: ImageView = view.findViewById(R.id.imageItem)
        fun bind(uri: Uri) {
            image.setImageURI(uri)  // Display image from URI
        }
    }

    // ViewHolder for the "+" add button (no data binding needed)
    inner class AddViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
