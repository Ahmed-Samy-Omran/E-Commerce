package com.example.data.models.search_filter


data class FilterSortOptions(
    val filterAvailable: Boolean = false,
    val selectedMinPrice: Int = 0,
    val filterRatingLess3: Boolean = false,
    val filterRatingAbove3: Boolean = false,
    val sortLowToHigh: Boolean = false,
    val sortHighToLow: Boolean = false,
    val filterPrice: Boolean = false,
    val selectedMaxPrice: Int = Int.MAX_VALUE
)