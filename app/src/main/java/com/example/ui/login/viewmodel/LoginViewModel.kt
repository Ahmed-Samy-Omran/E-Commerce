package com.example.ui.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.repository.user.UserPreferenceRepository


class LoginViewModel(
    val userPrefs: UserPreferenceRepository
) : ViewModel() {
    class LoginViewModelFactory(
        private val userPrefs: UserPreferenceRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(userPrefs)
                        as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}

