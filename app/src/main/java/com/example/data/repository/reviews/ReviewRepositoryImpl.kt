package com.example.data.repository.reviews

import android.util.Log
import com.example.data.models.Resource
import com.example.data.models.reviews.ReviewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReviewsRepo {

    override fun getReviews(productId: String): Flow<Resource<List<ReviewModel>>> = flow {
        emit(Resource.Loading())
        try {
            // Access the reviews collection within the specific product document
            val snapshot = firestore.collection("products")
                .document(productId)
                .collection("reviews")
                .get()
                .await()

            val reviews = snapshot.documents.mapNotNull { it.toObject(ReviewModel::class.java) }

            Log.d(TAG, "Fetched ${reviews.size} reviews")
            emit(Resource.Success(reviews))
        } catch (e: Exception) {
            Log.d(TAG, "getReviews: ${e.message}")
            emit(Resource.Error(e))
        }
    }


    override suspend fun addReview(productId: String, review: ReviewModel): Resource<Boolean> {
        return try {
            firestore.collection("reviews")
                .add(review)
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Log.d(TAG, "addReview: ${e.message}")
            Resource.Error(e)
        }
    }

    companion object {
        private const val TAG = "ReviewRepositoryImpl"
    }

}