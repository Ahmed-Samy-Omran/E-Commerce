package com.example.ui.login.viewmodel

import androidx.lifecycle.ViewModel
import com.example.data.repository.user.UserPreferenceRepository


class LoginViewModel(
    val userPrefs: UserPreferenceRepository
) : ViewModel() {


}

