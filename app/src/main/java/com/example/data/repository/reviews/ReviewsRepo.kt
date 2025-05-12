package com.example.data.repository.reviews

import com.example.data.models.Resource
import com.example.data.models.reviews.ReviewModel
import kotlinx.coroutines.flow.Flow

interface ReviewsRepo {

    fun getReviews(productId: String): Flow<Resource<List<ReviewModel>>>
    suspend fun addReview(productId: String, review: ReviewModel): Resource<Boolean>

}