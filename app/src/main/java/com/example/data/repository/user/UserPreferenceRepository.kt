package com.example.data.repository.user

import kotlinx.coroutines.flow.Flow

interface UserPreferenceRepository {

     suspend fun saveLoginState(isLoggedIn: Boolean)
     suspend fun isUserLoggedIn(): Flow<Boolean>
     suspend fun saveUserID(userID: String)

     fun getUserID(): Flow<String?>
}