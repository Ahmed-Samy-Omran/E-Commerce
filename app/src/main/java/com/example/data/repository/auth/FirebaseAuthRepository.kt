package com.example.data.repository.auth

import com.example.data.models.Resource
import kotlinx.coroutines.flow.Flow


interface FirebaseAuthRepository {

    suspend fun loginWithEmailAndPassword(email: String, password: String)
            : Flow<Resource<String>>

    suspend fun loginWithGoogle(
       idToken: String
    ): Flow<Resource<String>>

}

