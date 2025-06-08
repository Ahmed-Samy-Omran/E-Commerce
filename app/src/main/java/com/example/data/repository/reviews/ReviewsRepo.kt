package com.example.data.repository.reviews

import com.example.data.models.Resource
import com.example.data.models.reviews.ReviewModel
import com.example.ui.reviews.model.ReviewUIModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow

interface ReviewsRepo {

    fun getReviews(productId: String): Flow<Resource<List<ReviewModel>>>
//    suspend fun addReview(productId: String, review: ReviewUIModel): Resource<Boolean>
suspend fun addReview(productId: String, review: ReviewUIModel): Resource<String>
    // ✅ New method to support editing
    suspend fun addOrUpdateReview(productId: String, review: ReviewModel): Resource<Boolean>

    suspend fun checkIfUserReviewed(productId: String, userId: String): QuerySnapshot

    // ✅ New method to check if a review by user exists
    suspend fun getUserReview(productId: String, userId: String): ReviewModel?

    suspend fun getAllReviewsPaging(
        productId: String, pageLimit: Long, lastDocument: DocumentSnapshot? = null
    ): Flow<Resource<QuerySnapshot>>

    fun getCurrentUserId(): String
    fun getCurrentUserName(): String

    fun getCurrentUserImageUrl(): String
}