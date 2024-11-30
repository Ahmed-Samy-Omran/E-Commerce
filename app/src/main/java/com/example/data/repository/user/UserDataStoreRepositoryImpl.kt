package com.example.data.repository.user

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.example.data.datasource.datastore.DataStoreKeys.IS_USER_LOGGED_IN
import com.example.data.datasource.datastore.DataStoreKeys.USER_ID
import com.example.data.datasource.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserDataStoreRepositoryImpl(private var context: Context) : UserPreferenceRepository {



    override suspend fun saveLoginState(isLoggedIn: Boolean) {
                context.dataStore.edit { preferences ->
                    // returns the logged-in state,defaulting to false if not found
                    preferences[IS_USER_LOGGED_IN] = isLoggedIn
                }
    }

        override suspend fun isUserLoggedIn(): Flow<Boolean> {
            // here i map flow of data
            return context.dataStore.data.map { preferences ->
                // returns the logged-in state,defaulting to false if not found
                preferences[IS_USER_LOGGED_IN] ?: false
            }
        }

    override suspend fun saveUserID(userID: String) {
        context.dataStore.edit { preferences ->
            // returns the logged-in state,defaulting to false if not found
            preferences[USER_ID] = userID

        }
    }
}

