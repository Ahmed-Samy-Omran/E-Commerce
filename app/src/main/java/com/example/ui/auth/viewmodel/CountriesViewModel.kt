package com.example.ui.auth.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.auth.CountryRepository
import com.example.data.repository.user.UserPreferenceRepository
import com.example.domain.models.toUIModel
import com.example.ui.auth.models.CountryUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountriesViewModel @Inject constructor(
    private val countriesRepository: CountryRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
) : ViewModel() {

    private val countriesState = countriesRepository.getCountries().stateIn(
        scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val countriesUIModelState = countriesState.mapLatest { countries ->
        countries.map { country ->
            Log.d("CountriesViewModel", "countriesUIModelState: $country")
            country.toUIModel()
        }
    }

    fun saveUserCountry(country: CountryUIModel) {
        viewModelScope.launch {
            userPreferenceRepository.saveUserCountry(country)
        }
    }
}