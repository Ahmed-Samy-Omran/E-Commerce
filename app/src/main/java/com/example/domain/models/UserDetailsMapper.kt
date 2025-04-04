package com.example.domain.models

import com.example.data.models.user.CountryData
import com.example.data.models.user.UserDetailsModel
import com.example.data.models.user.UserDetailsPreferences

fun UserDetailsPreferences.toUserDetailsModel(): UserDetailsModel {
    return UserDetailsModel(
        id = id,
        email = email,
        name = name,
        reviews = reviewsList
    )
}
fun UserDetailsModel.toUserDetailsPreferences(countryData: CountryData): UserDetailsPreferences {
    return UserDetailsPreferences.newBuilder()
        .setId(id)
        .setEmail(email)
        .setName(name)
        .addAllReviews(reviews?.toList() ?: emptyList())
        .setCountry(countryData)
        .build()
}