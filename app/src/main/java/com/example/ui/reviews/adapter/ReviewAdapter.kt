package com.example.ui.reviews.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_commerce.R
import com.example.e_commerce.databinding.ItemReviewBinding
import com.example.ui.reviews.model.ReviewUIModel


class ReviewAdapter : ListAdapter<ReviewUIModel, ReviewAdapter.ReviewViewHolder>(ReviewDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = getItem(position)
        holder.bind(review)
    }

    class ReviewViewHolder(private val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: ReviewUIModel) {
            binding.userNameTextView.text = review.userName
            binding.ratingBar.rating = review.rating.toFloat()
            binding.reviewTextView.text = review.reviewText
            binding.reviewDateTextView.text = review.formattedDate

            // Load the user image using Glide
            Glide.with(binding.userImg.context)
                .load(review.imageUrl)
                .circleCrop()
                .into(binding.userImg)


            // Clear previous images before adding new ones
            binding.reviewImagesContainer.removeAllViews()

            // Load and display review images
            review.reviewImages?.forEach { imageUrl ->
                val imageView = com.google.android.material.imageview.ShapeableImageView(binding.root.context).apply {
                    layoutParams = ViewGroup.MarginLayoutParams(160, 160).apply {
                        marginEnd = 16 // Add space between images
                    }
                    scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                    Glide.with(this).load(imageUrl).into(this)
                }
                binding.reviewImagesContainer.addView(imageView)
                Log.d("ReviewAdapter", "Image URL: $imageUrl")
            }
        }
    }

    class ReviewDiffCallback : DiffUtil.ItemCallback<ReviewUIModel>() {
        override fun areItemsTheSame(oldItem: ReviewUIModel, newItem: ReviewUIModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ReviewUIModel, newItem: ReviewUIModel): Boolean {
            return oldItem == newItem
        }
    }
}