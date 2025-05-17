package com.example.ui.reviews

import com.example.ui.reviews.model.ReviewUIModel


object ReviewFilter {
    fun filterReviews(reviews: List<ReviewUIModel>, rating: Int?): List<ReviewUIModel> {
        return if (rating == null) {
            reviews // Return all reviews when no rating is selected
        } else {
            reviews.filter { it.rating == rating } // Filter by selected rating
        }
    }
}