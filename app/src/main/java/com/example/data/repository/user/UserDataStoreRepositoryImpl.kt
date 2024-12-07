package com.example.data.repository.user

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.example.data.datasource.datastore.DataStoreKeys.IS_USER_LOGGED_IN
import com.example.data.datasource.datastore.DataStoreKeys.USER_ID
import com.example.data.datasource.datastore.UserPreferencesDataSource
import com.example.data.datasource.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserDataStoreRepositoryImpl(
    private val userPreferencesDataSource: UserPreferencesDataSource
) : UserPreferenceRepository {

    override suspend fun saveLoginState(isLoggedIn: Boolean) {
        userPreferencesDataSource.saveLoginState(isLoggedIn)
    }

    override suspend fun isUserLoggedIn(): Flow<Boolean> {
       return  userPreferencesDataSource.isUserLoggedIn
    }

    override suspend fun saveUserID(userID: String) {
        userPreferencesDataSource.saveUserId(userID)
    }

    override fun getUserID(): Flow<String?> {
        return userPreferencesDataSource.userId
    }



}

