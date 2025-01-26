package com.example.ui.auth.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.datasource.datastore.AppPreferencesDataSource
import com.example.data.repository.auth.FirebaseAuthRepository
import com.example.data.repository.auth.FirebaseAuthRepositoryImpl
import com.example.data.repository.common.AppDataStoreRepositoryImpl
import com.example.data.repository.user.UserPreferenceRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow

class ForgetPasswordViewModel(val authRepository: FirebaseAuthRepository): ViewModel()  {
    val email = MutableStateFlow("")

    fun sendUpdatePasswordEmail() {
        // send email to the user to update the password
    }

}

// create viewmodel factory class
class ForgetPasswordViewModelFactory(
    private val authRepository : FirebaseAuthRepository=FirebaseAuthRepositoryImpl()
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForgetPasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return ForgetPasswordViewModel(
                authRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}