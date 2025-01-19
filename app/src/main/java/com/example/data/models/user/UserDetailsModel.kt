package com.example.data.models.user

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class UserDetailsModel(
    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    var id: String? = null,
    var email: String? = null,
    var name: String? = null,
    var reviews: List<String>? = null,
    var disabled: Boolean? = null,
    var createdAt: Long? = null,
) : Parcelable