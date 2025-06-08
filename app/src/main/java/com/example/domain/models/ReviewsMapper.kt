package com.example.domain.models

import com.example.data.models.reviews.ReviewModel
import com.example.ui.reviews.model.ReviewUIModel
import com.example.utils.formatTimestamp
import com.example.utils.parseFormattedDate

fun ReviewUIModel.toReviewModel(): ReviewModel {
    return ReviewModel(
        id = id,
        image = imageUrl,
        name = userName,
        rate = rating,
        reviewImages = reviewImages,
        reviewText = reviewText,
         timeStamp = parseFormattedDate(formattedDate),  // Convert formatted date to Timestamp
        userId = null // UI model doesn't contain user ID directly
    )
}

fun ReviewModel.toReviewUIModel(): ReviewUIModel {
    return ReviewUIModel(
        id = id ?: throw IllegalArgumentException("Review ID is missing"),
        imageUrl = image ?: "",
        userName = name ?: "Unknown",
        rating = rate ?: 0,
        reviewImages = reviewImages ?: emptyList(),
        reviewText = reviewText ?: "No review",
        formattedDate = formatTimestamp(timeStamp),  // Format the Timestamp to a readable date
        userId = userId ?: "unknown_user"  // Default value if userId is null
    )
}
