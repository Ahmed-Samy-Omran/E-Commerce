package com.example.data.repository.auth

import com.example.data.models.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryImpl(
    private val auth: FirebaseAuth=FirebaseAuth.getInstance()
) : FirebaseAuthRepository {
    override suspend fun loginWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<Resource<String>> = flow {
        try {
            // send to listener tell him that i load data
            emit(Resource.Loading())
            // get data in background and use auth
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.user?.let { user ->
                emit(Resource.Success(user.uid))
            } ?:run {
                emit(Resource.Error(Exception("User not found")))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e))
        }

    }
}