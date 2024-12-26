package com.example.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.models.Resource
import com.example.data.repository.auth.FirebaseAuthRepository
import com.example.data.repository.user.UserPreferenceRepository
import com.example.utils.isValidEmail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class LoginViewModel(
    private val userPrefs: UserPreferenceRepository,
    private val authRepository: FirebaseAuthRepository,
) : ViewModel() {

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")

    // any changes happen to this flow will be shared with the UI
    private val _loginState = MutableSharedFlow<Resource<String>>()
    val loginState: SharedFlow<Resource<String>> = _loginState.asSharedFlow()

    // any changes happen to this flow will validate automatically
    val isLoginIsValid: Flow<Boolean> = combine(email, password) { email, password ->
        email.isValidEmail() && password.length >= 6
    }

    fun login() = viewModelScope.launch {
        val email = email.value
        val password = password.value
        if (isLoginIsValid.first()) {
            authRepository.loginWithEmailAndPassword(email, password).onEach { resource ->
                when (resource) {
//                        is Resource.Loading -> loginState.emit(Resource.Loading())
                    is Resource.Success -> {

                        _loginState.emit(Resource.Success(resource.data ?: "Empty User ID"))
                    }

                    else -> _loginState.emit(resource)
                }
            }.launchIn(viewModelScope)
        } else {
            _loginState.emit(Resource.Error(Exception("Invalid Email or Password")))
        }
    }

    fun loginWithGoogle(idToken: String) = viewModelScope.launch {
        authRepository.loginWithGoogle(idToken).onEach { resource ->
            when (resource) {
//                is Resource.Loading -> loginState.emit(Resource.Loading())
                is Resource.Success -> {
                    _loginState.emit(Resource.Success(resource.data ?: "Empty User ID"))
                }

                else -> _loginState.emit(resource)
            }
        }.launchIn(viewModelScope)
    }
}


class LoginViewModelFactory(
    private val userPrefs: UserPreferenceRepository,
    private val authRepository: FirebaseAuthRepository,

    ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userPrefs, authRepository)
                    as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



