package com.example.data.models.reviews

import android.os.Parcelable
import androidx.annotation.Keep
import com.example.utils.formatTimestamp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class ReviewModel(
    var id: String? = null,
    var image: String? = null,
    var name: String? = null,
    var rate:Int? = null,
    var reviewImages: List<String>? = null,
    var reviewText:  String? = null,

    @get:PropertyName("timestamp")
    @set:PropertyName("timestamp")
    var timeStamp: com.google.firebase.Timestamp? = null,  // Firebase timestamp

    @get:PropertyName("user_id")
    @set:PropertyName("user_id")
    var userId: String? = null

):Parcelable{
    fun Timestamp?.toFormattedDate(): String {
        return formatTimestamp(this) // Your existing function
    }
}