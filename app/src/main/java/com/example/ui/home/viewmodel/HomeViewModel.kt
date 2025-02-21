package com.example.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.Resource
import com.example.data.models.sale_ads.SalesAdModel
import com.example.data.repository.home.SalesAdsRepository
import com.example.ui.home.model.SalesAdUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val salesAdsRepository: SalesAdsRepository
) : ViewModel() {
    val salesAdsStateTemp =
        salesAdsRepository.getSalesAds().stateIn(
            viewModelScope + IO,
            started = SharingStarted.Eagerly,
            initialValue = Resource.Loading()
        )

    init {
//        getSalesAds()
    }
}