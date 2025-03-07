package com.example.data.repository.user

import com.example.data.models.user.CountryData
import com.example.data.models.user.UserDetailsPreferences
import com.example.ui.auth.models.CountryUIModel
import kotlinx.coroutines.flow.Flow

interface UserPreferenceRepository {

     fun getUserDetails(): Flow<UserDetailsPreferences>
     suspend fun updateUserId(userId: String)
     suspend fun getUserId(): Flow<String>
     suspend fun clearUserPreferences()
     suspend fun updateUserDetails(userDetailsPreferences: UserDetailsPreferences)
     suspend fun saveUserCountry(countryId: CountryUIModel)
     suspend fun getUserCountry():  Flow<CountryData>
}