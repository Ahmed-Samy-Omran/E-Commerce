package com.example.data.repository.auth

import com.example.data.models.Resource
import com.example.data.models.user.UserDetailsModel
import kotlinx.coroutines.flow.Flow


interface FirebaseAuthRepository {

    suspend fun loginWithEmailAndPassword(email: String, password: String)
            : Flow<Resource<UserDetailsModel>>

    suspend fun loginWithGoogle(
       idToken: String
    ):  Flow<Resource<UserDetailsModel>>

    suspend fun loginWithFacebook(token: String):  Flow<Resource<UserDetailsModel>>

    fun logout()

}

