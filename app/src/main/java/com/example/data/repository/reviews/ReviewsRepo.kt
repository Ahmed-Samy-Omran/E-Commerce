package com.example.data.repository.reviews

import com.example.data.models.Resource
import com.example.data.models.reviews.ReviewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow

interface ReviewsRepo {

    fun getReviews(productId: String): Flow<Resource<List<ReviewModel>>>
    suspend fun addReview(productId: String, review: ReviewModel): Resource<Boolean>

    suspend fun getAllReviewsPaging(
        productId: String, pageLimit: Long, lastDocument: DocumentSnapshot? = null
    ): Flow<Resource<QuerySnapshot>>

}