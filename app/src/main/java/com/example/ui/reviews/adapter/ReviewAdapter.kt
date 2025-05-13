    package com.example.ui.reviews.adapter

    import android.content.Context
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.ViewGroup
    import android.widget.ImageView
    import androidx.appcompat.app.AppCompatActivity
    import androidx.recyclerview.widget.DiffUtil
    import androidx.recyclerview.widget.ListAdapter
    import androidx.recyclerview.widget.RecyclerView
    import com.bumptech.glide.Glide
    import com.example.e_commerce.R
    import com.example.e_commerce.databinding.ItemReviewBinding
    import com.example.ui.reviews.model.ReviewUIModel
    import com.example.utils.FullscreenImageDialog
    import com.google.android.material.imageview.ShapeableImageView


    class ReviewAdapter : ListAdapter<ReviewUIModel, ReviewAdapter.ReviewViewHolder>(ReviewDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
            val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ReviewViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
            val review = getItem(position)
            holder.bind(review)
        }

        inner class ReviewViewHolder(private val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(review: ReviewUIModel) {
                binding.apply {
                    userNameTextView.text = review.userName
                    ratingBar.rating = review.rating.toFloat()
                    reviewTextView.text = review.reviewText
                    reviewDateTextView.text = review.formattedDate

                    // Load the user image using Glide
                    Glide.with(userImg.context)
                        .load(review.imageUrl)
                        .circleCrop()
                        .into(userImg)

                    // Clear previous images before adding new ones
                    reviewImagesContainer.removeAllViews()

                    // Load and display review images
                    review.reviewImages?.forEach { imageUrl ->
                        val imageView = ShapeableImageView(root.context).apply {
                            layoutParams = ViewGroup.MarginLayoutParams(
                                resources.getDimensionPixelSize(R.dimen.review_image_size),
                                resources.getDimensionPixelSize(R.dimen.review_image_size)
                            ).apply {
                                marginEnd = resources.getDimensionPixelSize(R.dimen.review_image_margin)
                            }
                            scaleType = android.widget.ImageView.ScaleType.CENTER_CROP


                            Glide.with(this)
                                .load(imageUrl)
                                .error(R.drawable.error_ic)
                                .override(resources.getDimensionPixelSize(R.dimen.review_image_size))
                                .into(this)

                            // Set click listener to open the fullscreen image dialog with animation
                            setOnClickListener {
                                val location = IntArray(2)
                                getLocationOnScreen(location)

                                // Create bounds array: left, top, width, height
                                val bounds = intArrayOf(
                                    location[0],
                                    location[1],
                                    width,
                                    height
                                )

                                FullscreenImageDialog(root.context, imageUrl, bounds).show()
                            }
                        }
                        reviewImagesContainer.addView(imageView)
                    }
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

