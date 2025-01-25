package com.example.ui.auth.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.datasource.datastore.AppPreferencesDataSource
import com.example.data.models.Resource
import com.example.data.models.user.UserDetailsModel
import com.example.data.repository.auth.FirebaseAuthRepository
import com.example.data.repository.auth.FirebaseAuthRepositoryImpl
import com.example.data.repository.common.AppDataStoreRepositoryImpl
import com.example.data.repository.common.AppPreferenceRepository
import com.example.data.repository.user.UserPreferenceRepository
import com.example.data.repository.user.UserPreferenceRepositoryImpl
import com.example.utils.isValidEmail
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val appPreferenceRepository: AppPreferenceRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {

    // any changes happen to this flow will be shared with the UI
    private val _registerState = MutableSharedFlow<Resource<UserDetailsModel>>()
    val registerState: SharedFlow<Resource<UserDetailsModel>> = _registerState.asSharedFlow()

    val name = MutableStateFlow("")
    val password = MutableStateFlow("")
    val email = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")


    private val isRegisterIsValid= combine(
        name,
        email,
        password,
        confirmPassword
    ) { name, email, password, confirmPassword ->
        name.isNotEmpty() && email.isValidEmail() && password.length >=6 && confirmPassword.isNotEmpty() && password == confirmPassword
    }


    fun registerWithEmailAndPassword()= viewModelScope.launch(IO) {
        val name = name.value
        val email = email.value
        val password = password.value

        if (isRegisterIsValid.first()) {
            authRepository.registerWithEmailAndPassword(email, password, name).collect {
                _registerState.emit(it)
            }
        }else{

        }
    }

    fun signUpWithGoogle(idToken: String) =viewModelScope.launch(IO) {
        authRepository.registerWithGoogle(idToken).collect {
            _registerState.emit(it)
        }
    }


}


// create viewmodel factory class
class RegisterViewModelFactory(
    private val contextValue: Context
) : ViewModelProvider.Factory {

    private val appPreferenceRepository =
        AppDataStoreRepositoryImpl(AppPreferencesDataSource(contextValue))
    private val userPreferenceRepository = UserPreferenceRepositoryImpl(contextValue)
    private val authRepository = FirebaseAuthRepositoryImpl()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return RegisterViewModel(
                appPreferenceRepository,
                userPreferenceRepository,
                authRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}