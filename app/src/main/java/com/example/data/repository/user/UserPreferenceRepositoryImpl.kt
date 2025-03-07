package com.example.data.repository.user

import android.app.Application
import android.content.Context
import com.example.data.datasource.datastore.userDetailsDataStore
import com.example.data.models.user.CountryData
import com.example.data.models.user.UserDetailsPreferences
import com.example.ui.auth.models.CountryUIModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferenceRepositoryImpl @Inject constructor( val context: Application) : UserPreferenceRepository {
    override fun getUserDetails(): Flow<UserDetailsPreferences> {
        return context.userDetailsDataStore.data
    }
    override suspend fun updateUserId(userId: String) {
        context.userDetailsDataStore.updateData { preferences ->
            preferences.toBuilder().setId(userId).build()
        }
    }
    override suspend fun getUserId(): Flow<String> {
        return context.userDetailsDataStore.data.map { it.id }
    }
    override suspend fun clearUserPreferences() {
        context.userDetailsDataStore.updateData { preferences ->
            preferences.toBuilder().clear().build()
        }
    }
    override suspend fun updateUserDetails(userDetailsPreferences: UserDetailsPreferences) {
        context.userDetailsDataStore.updateData { userDetailsPreferences }
    }

//    override suspend fun saveUserCountry(countryId: CountryUIModel) {
//        val countryData = CountryData.newBuilder().setId(countryId.id).setCode(countryId.code)
//            .setName(countryId.name).setCurrency(countryId.currency)
//            .setCurrencySymbol(countryId.currencySymbol).build()
//        context.userDetailsDataStore.updateData { preferences ->
//            preferences.toBuilder().setCountry(countryData).build()
//        }
//    }

    // that fun is new to save user country not exist in videos i make it to Ensure countryId.currencySymbol is not null
    override suspend fun saveUserCountry(countryId: CountryUIModel) {
        val countryDataBuilder = CountryData.newBuilder()
            .setId(countryId.id)
            .setCode(countryId.code)
            .setName(countryId.name)
            .setCurrency(countryId.currency ?: "EGP") // Default to "EGP" if null

        // Only set currency symbol if it's not null
        countryId.currencySymbol?.let { symbol ->
            countryDataBuilder.setCurrencySymbol(symbol)
        }

        val countryData = countryDataBuilder.build()

        context.userDetailsDataStore.updateData { preferences ->
            preferences.toBuilder().setCountry(countryData).build()
        }
    }

    override suspend fun getUserCountry(): Flow<CountryData> {
        return context.userDetailsDataStore.data.map { it.country }
    }
}