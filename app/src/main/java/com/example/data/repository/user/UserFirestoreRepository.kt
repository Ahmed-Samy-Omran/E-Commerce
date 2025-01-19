package com.example.data.repository.user

import com.example.data.models.Resource
import com.example.data.models.user.UserDetailsModel
import kotlinx.coroutines.flow.Flow

interface UserFirestoreRepository {
    suspend fun getUserDetails(userId: String): Flow<Resource<UserDetailsModel>>
}