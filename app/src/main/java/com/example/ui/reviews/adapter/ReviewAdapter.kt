    package com.example.ui.reviews.adapter

    import android.content.Context
    import android.graphics.Canvas
    import android.graphics.Color
    import android.graphics.Paint
    import android.graphics.Rect
    import android.graphics.Typeface
    import android.graphics.drawable.Drawable
    import android.text.TextPaint
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.ViewGroup
    import android.widget.ImageView
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.content.ContextCompat
    import androidx.recyclerview.widget.DiffUtil
    import androidx.recyclerview.widget.ListAdapter
    import androidx.recyclerview.widget.RecyclerView
    import com.bumptech.glide.Glide
    import com.bumptech.glide.request.RequestOptions
    import com.example.e_commerce.R
    import com.example.e_commerce.databinding.ItemReviewBinding
    import com.example.ui.reviews.ReviewFilter
    import com.example.ui.reviews.model.ReviewUIModel
    import com.example.utils.FullscreenImageDialog
    import com.google.android.material.imageview.ShapeableImageView


//    class ReviewAdapter : ListAdapter<ReviewUIModel, ReviewAdapter.ReviewViewHolder>(ReviewDiffCallback()) {
//
//        // Store original list for filtering
//        private var originalList: List<ReviewUIModel> = emptyList()
//
//        // Filter function that works with ListAdapter
//        fun filterReviews(rating: Int?) {
//            val filtered = ReviewFilter.filterReviews(originalList, rating)
//            submitList(filtered.toList()) // Ensures a new list instance is passed
//        }
//
//        // Update original list when new data is submitted
//        override fun submitList(list: List<ReviewUIModel>?) {
//            originalList = list ?: emptyList()
//            super.submitList(list)
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
//            val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//            return ReviewViewHolder(binding)
//        }
//
//        override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
//            val review = getItem(position)
//            holder.bind(review)
//        }
//
//        inner class ReviewViewHolder(private val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
//            fun bind(review: ReviewUIModel) {
//                binding.apply {
//                    // Set user name - ensure it's not empty
//                    userNameTextView.text = if (review.userName.isNotBlank()) review.userName else "Anonymous"
//
//                    // Set rating
//                    ratingBar.rating = review.rating.toFloat()
//
//                    // Set review text
//                    reviewTextView.text = review.reviewText
//
//                    // Set date
//                    reviewDateTextView.text =
//                        if (review.formattedDate != "No date") review.formattedDate else ""
//
//                    // Load user avatar
//                    if (review.imageUrl.isNotBlank()) {
//                        // If we have an image URL, load it with Glide
//                        Glide.with(userImg.context)
//                            .load(review.imageUrl)
//                            .apply(RequestOptions().circleCrop())
//                            .placeholder(R.drawable.ic_profile) // Replace with your default avatar icon
//                            .error(createLetterDrawable(review.userName, userImg.context))
//                            .into(userImg)
//                    } else {
//                        // If no image URL, create a letter avatar
//                        userImg.setImageDrawable(createLetterDrawable(review.userName, userImg.context))
//                    }
//
//                    // Clear previous images before adding new ones
//                    reviewImagesContainer.removeAllViews()
//
//                    // Load and display review images
//                    review.reviewImages?.forEach { imageUrl ->
//                        val imageView = ShapeableImageView(root.context).apply {
//                            layoutParams = ViewGroup.MarginLayoutParams(
//                                resources.getDimensionPixelSize(R.dimen.review_image_size),
//                                resources.getDimensionPixelSize(R.dimen.review_image_size)
//                            ).apply {
//                                marginEnd = resources.getDimensionPixelSize(R.dimen.review_image_margin)
//                            }
//                            scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
//
//                            Glide.with(this)
//                                .load(imageUrl)
//                                .error(R.drawable.error_ic)
//                                .override(resources.getDimensionPixelSize(R.dimen.review_image_size))
//                                .into(this)
//
//                            // Set click listener to open the fullscreen image dialog with animation
//                            setOnClickListener {
//                                val location = IntArray(2)
//                                getLocationOnScreen(location)
//
//                                // Create bounds array: left, top, width, height
//                                val bounds = intArrayOf(
//                                    location[0],
//                                    location[1],
//                                    width,
//                                    height
//                                )
//
//                                FullscreenImageDialog(root.context, imageUrl, bounds).show()
//                            }
//                        }
//                        reviewImagesContainer.addView(imageView)
//                    }
//                }
//            }
//
//            // Create a drawable with the first letter of the user's name
//            private fun createLetterDrawable(userName: String, context: Context): Drawable {
//                return LetterDrawable(userName, context)
//            }
//        }
//
//        // Custom drawable class to display a letter in a circle
//        private class LetterDrawable(userName: String, context: Context) : Drawable() {
//            private val letter: String
//            private val paint: Paint
//            private val textPaint: TextPaint
//            private val backgroundColor: Int
//
//            init {
//                // Get the first letter of the user name, or "?" if empty
//                letter = if (userName.isNotBlank()) {
//                    userName.trim()[0].toString().uppercase()
//                } else {
//                    "?"
//                }
//
//                // Set up the background paint
//                paint = Paint().apply {
//                    isAntiAlias = true
//                    color = getColorForLetter(letter, context)
//                    style = Paint.Style.FILL
//                }
//
//                // Set up the text paint
//                textPaint = TextPaint().apply {
//                    isAntiAlias = true
//                    color = Color.WHITE
//                    textSize = 40f
//                    typeface = Typeface.DEFAULT_BOLD
//                    textAlign = Paint.Align.CENTER
//                }
//
//                backgroundColor = getColorForLetter(letter, context)
//            }
//
//            override fun draw(canvas: Canvas) {
//                val bounds = bounds
//                val width = bounds.width()
//                val height = bounds.height()
//
//                // Draw the circle background
//                val radius = Math.min(width, height) / 2f
//                canvas.drawCircle(width / 2f, height / 2f, radius, paint)
//
//                // Draw the letter in the center
//                val textBounds = Rect()
//                textPaint.getTextBounds(letter, 0, letter.length, textBounds)
//                val textHeight = textBounds.height()
//                canvas.drawText(letter, width / 2f, height / 2f + textHeight / 2f, textPaint)
//            }
//
//            override fun setAlpha(alpha: Int) {
//                paint.alpha = alpha
//                textPaint.alpha = alpha
//                invalidateSelf()
//            }
//
//            override fun setColorFilter(colorFilter: android.graphics.ColorFilter?) {
//                paint.colorFilter = colorFilter
//                textPaint.colorFilter = colorFilter
//                invalidateSelf()
//            }
//
//            override fun getOpacity(): Int {
//                return android.graphics.PixelFormat.TRANSLUCENT
//            }
//
//            // Get a consistent color based on the letter
//            private fun getColorForLetter(letter: String, context: Context): Int {
//                val colors = arrayOf(
//                    R.color.primary_yellow,
//                    android.R.color.holo_blue_light,
//                    android.R.color.holo_green_light,
//                    android.R.color.holo_orange_light,
//                    android.R.color.holo_purple,
//                    android.R.color.holo_red_light
//                )
//
//                val index = Math.abs(letter.hashCode()) % colors.size
//                return ContextCompat.getColor(context, colors[index])
//            }
//        }
//
//        class ReviewDiffCallback : DiffUtil.ItemCallback<ReviewUIModel>() {
//            override fun areItemsTheSame(oldItem: ReviewUIModel, newItem: ReviewUIModel): Boolean {
//                return oldItem.id == newItem.id
//            }
//
//            override fun areContentsTheSame(oldItem: ReviewUIModel, newItem: ReviewUIModel): Boolean {
//                return oldItem == newItem
//            }
//        }
//    }


    class ReviewAdapter : ListAdapter<ReviewUIModel, ReviewAdapter.ReviewViewHolder>(ReviewDiffCallback()) {

        // NEW: Store original list for filtering
        private var originalList: List<ReviewUIModel> = emptyList()

        // NEW: Filter function that works with ListAdapter
        fun filterReviews(rating: Int?) {
            val filtered = ReviewFilter.filterReviews(originalList, rating)
            submitList(filtered.toList()) // Ensures a new list instance is passed
        }
        // NEW: Update original list when new data is submitted
        override fun submitList(list: List<ReviewUIModel>?) {
            originalList = list ?: emptyList()
            super.submitList(list)
        }


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
                    reviewDateTextView.text =
                        if (review.formattedDate != "No date") review.formattedDate else ""

                    // Load the user image using Glide
                    Glide.with(userImg.context)
                        .load(review.imageUrl)
                        .circleCrop()
                        .into(userImg)

//                    Glide.with(userImg.context)
//                        .load(review.imageUrl)
//                        .placeholder(R.drawable.ic_profile) // replace with your default avatar icon
////                        .error(R.drawable.ic_default_profile)       // fallback if image fails
//                        .circleCrop()
//                        .into(userImg)

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

