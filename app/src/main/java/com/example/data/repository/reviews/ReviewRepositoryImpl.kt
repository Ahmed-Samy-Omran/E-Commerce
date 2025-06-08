package com.example.data.repository.reviews

import android.util.Log
import com.example.data.models.Resource
import com.example.data.models.reviews.ReviewModel
import com.example.ui.reviews.model.ReviewUIModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
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

            val reviews = snapshot.documents.mapNotNull {
                val reviewModel = it.toObject(ReviewModel::class.java)
                // Ensure the ID is set from the document ID
                reviewModel?.id = it.id
                reviewModel
            }

            Log.d(TAG, "Fetched ${reviews.size} reviews")
            emit(Resource.Success(reviews))
        } catch (e: Exception) {
            Log.d(TAG, "getReviews: ${e.message}")
            emit(Resource.Error(e))
        }
    }


    override suspend fun addReview(productId: String, review: ReviewUIModel): Resource<String> {
        return try {
            // Convert UI model to Firestore model
            val reviewModel = ReviewModel(
                id = null, // will be set later
                image = getCurrentUserImageUrl(),
                name = getCurrentUserName(),
                rate = review.rating,
                reviewImages = review.reviewImages,
                reviewText = review.reviewText,
                timeStamp = Timestamp.now(),
                userId = getCurrentUserId()
            )

            val reviewsRef = firestore.collection("products")
                .document(productId)
                .collection("reviews")

            // Check if the user already submitted a review
            val existing = reviewsRef
                .whereEqualTo("userId", getCurrentUserId())
                .get()
                .await()

            val reviewRef = if (!existing.isEmpty) {
                // User already has a review – update it
                reviewsRef.document(existing.documents.first().id)
            } else {
                // New review – create a new document
                reviewsRef.document()
            }

            val reviewWithId = reviewModel.copy(id = reviewRef.id)

            // Save or update the review
            reviewRef.set(reviewWithId).await()

            // Return the review ID
            Resource.Success(reviewRef.id)

        } catch (e: Exception) {
            Log.e(TAG, "addReview: failed to add or update review", e)
            Resource.Error(e)
        }
    }

    override suspend fun addOrUpdateReview(
        productId: String,
        review: ReviewModel
    ): Resource<Boolean> {
        return try {
            val reviewRef = if (review.id != null) {
                // Update existing review
                firestore.collection("products")
                    .document(productId)
                    .collection("reviews")
                    .document(review.id!!)
            } else {
                // Create new review
                firestore.collection("products")
                    .document(productId)
                    .collection("reviews")
                    .document()
            }

            // Ensure user data is set
            val reviewToSave = review.copy(
                id = reviewRef.id,
                image = review.image ?: getCurrentUserImageUrl(),
                name = review.name ?: getCurrentUserName(),
                userId = review.userId ?: getCurrentUserId(),
                timeStamp = review.timeStamp ?: Timestamp.now()
            )

            reviewRef.set(reviewToSave).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Log.d(TAG, "addOrUpdateReview: ${e.message}")
            Resource.Error(e)
        }
    }

    override suspend fun checkIfUserReviewed(productId: String, userId: String): QuerySnapshot {
        return firestore.collection("products")
            .document(productId)
            .collection("reviews")
            .whereEqualTo("user_id", userId)
            .limit(1)
            .get()
            .await()
    }

    override suspend fun getUserReview(productId: String, userId: String): ReviewModel? {
        val querySnapshot = checkIfUserReviewed(productId, userId)
        return if (querySnapshot.documents.isNotEmpty()) {
            val doc = querySnapshot.documents[0]
            val review = doc.toObject(ReviewModel::class.java)
            review?.id = doc.id
            review
        } else {
            null
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
                .orderBy("timestamp", Query.Direction.DESCENDING) // Use the correct field name

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

    override fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"
    }

//    override fun getCurrentUserName(): String {
//        return FirebaseAuth.getInstance().currentUser?.displayName ?: "Anonymous"
//    }

    override fun getCurrentUserName(): String {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.displayName ?: user?.email?.substringBefore('@') ?: "Anonymous"
    }

    override fun getCurrentUserImageUrl(): String {
        return FirebaseAuth.getInstance().currentUser?.photoUrl?.toString().toString()
    }
    companion object {
        private const val TAG = "ReviewRepositoryImpl"
    }
}
