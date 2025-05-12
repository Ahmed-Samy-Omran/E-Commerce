package com.example.ui.reviews.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ReviewUIModel(
    val id: String,
    val imageUrl: String,
    val userName: String,
    val rating: Int,
    val reviewImages: List<String>?, // Optional list of review images
    val reviewText: String,
    val formattedDate: String
) : Parcelable {

    fun getFirstReviewImage(): String {
        return reviewImages?.firstOrNull() ?: ""  // Handle optional list
    }

    fun getFormattedRating(): String {
        return "★".repeat(rating)  // Representing rate as stars
    }

    override fun toString(): String {
        return "ReviewUIModel(id='$id', imageUrl='$imageUrl', userName='$userName', rating=$rating, reviewText='$reviewText', formattedDate='$formattedDate', reviewImages=$reviewImages)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReviewUIModel

        return id == other.id &&
                userName == other.userName &&
                rating == other.rating &&
                reviewText == other.reviewText &&
                formattedDate == other.formattedDate &&
                reviewImages == other.reviewImages
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + userName.hashCode()
        result = 31 * result + rating
        result = 31 * result + reviewText.hashCode()
        result = 31 * result + formattedDate.hashCode()
        result = 31 * result + (reviewImages?.hashCode() ?: 0)
        return result
    }
}