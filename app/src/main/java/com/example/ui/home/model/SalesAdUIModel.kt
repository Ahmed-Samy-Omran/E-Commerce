package com.example.ui.home.model

import android.provider.ContactsContract.Data
import java.util.Date

data class SalesAdUIModel(
    val title: String? = null, val description: String? = null,

    var imageUrl: String? = null, val type: String? = null,

    var productId: String? = null,

    var categoryId: String? = null,

    var externalLink: String? = null,

    var endAt: Date? = null
)
enum class SalesAdType {
    PRODUCT,
    CATEGORY,
    EXTERNAL_LINK,
    EMPTY
}