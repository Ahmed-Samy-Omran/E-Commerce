package com.example.ui.common.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.data.datasource.datastore.AppPreferencesDataSource
import com.example.data.models.Resource
import com.example.domain.models.toUserDetailsModel
import com.example.domain.models.toUserDetailsPreferences
import com.example.data.repository.auth.FirebaseAuthRepository
import com.example.data.repository.auth.FirebaseAuthRepositoryImpl
import com.example.data.repository.common.AppDataStoreRepositoryImpl
import com.example.data.repository.common.AppPreferenceRepository
import com.example.data.repository.user.UserFirestoreRepository
import com.example.data.repository.user.UserFirestoreRepositoryImpl
import com.example.data.repository.user.UserPreferenceRepository
import com.example.data.repository.user.UserPreferenceRepositoryImpl
import com.example.utils.CrashlyticsUtils
import com.example.utils.CrashlyticsUtils.LISTEN_TO_USER_DETAILS
import com.example.utils.UserDetailsException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val appPreferencesRepository: AppPreferenceRepository,
    private val userPreferencesRepository: UserPreferenceRepository,
    private val userFirestoreRepository: UserFirestoreRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository
) : ViewModel() {


    private val logoutState=MutableSharedFlow<Resource<Unit>>()
    // load user data in state flow inside view model  scope
    val userDetailsState = getUserDetails().stateIn(
        viewModelScope, started = SharingStarted.Eagerly, initialValue = null
    )
    init {
        listenToUserDetails()
    }
    // load user data flow
    // we can use this to get user data in the view in main thread so we do not want to wait the data from state
    // note that this flow block the main thread while you get the data every time you call it
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUserDetails() =
        userPreferencesRepository.getUserDetails().mapLatest { it.toUserDetailsModel() }


    private fun listenToUserDetails() = viewModelScope.launch {
        val userId = userPreferencesRepository.getUserId().first()
        if (userId.isEmpty()) return@launch
        userFirestoreRepository.getUserDetails(userId).catch { e ->
            val msg = e.message ?: "Error listening to user details"
            CrashlyticsUtils.sendCustomLogToCrashlytics<UserDetailsException>(
                msg, LISTEN_TO_USER_DETAILS to msg
            )
            if (e is UserDetailsException) logOut()
        }.collectLatest { resource ->
            Log.d(TAG, "listenToUserDetails: ${resource.data}")
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let {
//                        userPreferencesRepository.updateUserDetails(it.toUserDetailsPreferences())
                    }
                }
                else -> {
                    // Do nothing
                    Log.d(TAG, "Error listen to user details: ${resource.exception?.message}")
                }
            }
        }
    }
    suspend fun isUserLoggedIn() = appPreferencesRepository.isLoggedIn()

    suspend fun logOut() = viewModelScope.launch {
        logoutState.emit(Resource.Loading())
        firebaseAuthRepository.logout()
        userPreferencesRepository.clearUserPreferences()
        appPreferencesRepository.saveLoginState(false)
        logoutState.emit(Resource.Success(Unit))
    }
    companion object {
        private const val TAG = "UserViewModel"
    }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: UserViewModel cleared")
    }
}

