package com.example.ui.home.model

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Date

data class SalesAdUIModel(
    val title: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val endAt: Long? = null,
)
enum class SalesAdType {
    PRODUCT,
    CATEGORY,
    EXTERNAL_LINK,
    EMPTY
}