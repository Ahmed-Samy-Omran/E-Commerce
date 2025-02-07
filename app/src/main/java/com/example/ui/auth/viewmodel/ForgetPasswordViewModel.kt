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
import com.example.data.repository.user.UserPreferenceRepositoryImpl
import com.example.utils.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgetPasswordViewModel @Inject constructor(
     val authRepository: FirebaseAuthRepository
): ViewModel()  {
    val email = MutableStateFlow("")

    // any changes happen to this flow will be shared with the UI
    private val _forgotPasswordState = MutableSharedFlow<Resource<String>>()
    val forgotPasswordState : SharedFlow<Resource<String>> = _forgotPasswordState.asSharedFlow()

    fun sendUpdatePasswordEmail()=viewModelScope.launch(IO) {
        if (email.value.isValidEmail()) {
            authRepository.sendUpdatePasswordEmail(email.value).collect {
                _forgotPasswordState.emit(it)
            }
        }else{
            _forgotPasswordState.emit(Resource.Error(Exception("Invalid email")))
        }
    }

}

