package com.example.data.repository.reviews

import android.util.Log
import com.example.data.models.Resource
import com.example.data.models.reviews.ReviewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
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
            firestore.collection("products")
                .document(productId)
                .collection("reviews")
                .add(review)
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Log.d(TAG, "addReview: ${e.message}")
            Resource.Error(e)
        }
    }
    override suspend fun getAllReviewsPaging(
        productId: String, pageLimit: Long, lastDocument: DocumentSnapshot?
    ): Flow<Resource<QuerySnapshot>> = flow {
        try {
            emit(Resource.Loading())

            // Access the reviews subcollection within the specific product document
            var firstQuery = firestore.collection("products")
                .document(productId)
                .collection("reviews")
                .orderBy("date", Query.Direction.DESCENDING)

            if (lastDocument != null) {
                firstQuery = firstQuery.startAfter(lastDocument)
            }

            firstQuery = firstQuery.limit(pageLimit)

            val reviews = firstQuery.get().await()
            emit(Resource.Success(reviews))
        } catch (e: Exception) {
            Log.d(TAG, "getAllReviewsPaging: ${e.message}")
            emit(Resource.Error(e))
        }
    }


    companion object {
        private const val TAG = "ReviewRepositoryImpl"
    }

}