package com.example.data.repository.auth

import com.example.data.models.Resource
import com.example.data.models.auth.RegisterRequestModel
import com.example.data.models.auth.RegisterResponseModel
import com.example.data.models.user.UserDetailsModel
import kotlinx.coroutines.flow.Flow


interface FirebaseAuthRepository {

    suspend fun loginWithEmailAndPassword(email: String, password: String)
            : Flow<Resource<UserDetailsModel>>

    suspend fun loginWithGoogle(
       idToken: String
    ):  Flow<Resource<UserDetailsModel>>

    suspend fun loginWithFacebook(token: String):  Flow<Resource<UserDetailsModel>>

    suspend fun registerWithFacebookWithAPI(token: String): Flow<Resource<RegisterResponseModel>>
    suspend fun registerWithGoogleWithAPI(
        idToken: String
    ): Flow<Resource<RegisterResponseModel>>
    suspend fun registerEmailAndPasswordWithAPI(registerRequestModel: RegisterRequestModel): Flow<Resource<RegisterResponseModel>>



    suspend fun registerWithEmailAndPassword(
        email: String,
        password: String,
        name: String
    ): Flow<Resource<UserDetailsModel>>

    suspend fun registerWithGoogle(
        idToken: String
    ):  Flow<Resource<UserDetailsModel>>


    suspend fun registerWithFacebook(token: String):  Flow<Resource<UserDetailsModel>>

    suspend fun sendUpdatePasswordEmail(email: String):Flow<Resource<String>>

    fun logout()

}

