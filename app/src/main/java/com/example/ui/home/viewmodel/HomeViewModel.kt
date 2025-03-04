package com.example.ui.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.Resource
import com.example.data.models.products.ProductSaleType
import com.example.data.models.sale_ads.SalesAdModel
import com.example.data.repository.categories.CategoriesRepository
import com.example.data.repository.home.SalesAdsRepository
import com.example.data.repository.products.ProductsRepository
import com.example.ui.home.model.SalesAdUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val salesAdsRepository: SalesAdsRepository,
    private val categoriesRepository: CategoriesRepository,
    private val productsRepository: ProductsRepository
) : ViewModel() {

    val salesAdsStateTemp = salesAdsRepository.getSalesAds().stateIn(
        viewModelScope + IO, started = SharingStarted.Eagerly, initialValue = Resource.Loading()
    )
        // i apply stateIn to my flow to convert it from cold flow to hot flow
    val categoriesState = categoriesRepository.getCategories().stateIn(
        viewModelScope + IO, started = SharingStarted.Eagerly, initialValue = Resource.Loading()
    )

    fun stopTimer() {
        salesAdsStateTemp.value.data?.forEach { it.stopCountdown() }
    }

    fun startTimer() {
        salesAdsStateTemp.value.data?.forEach { it.startCountdown() }
    }
    fun getFlashSaleProducts() = viewModelScope.launch(IO) {
        productsRepository.getSaleProducts(
            "EO8zdYKPeomrYLG7zYY3", ProductSaleType.FLASH_SALE.type, 10
        ).collectLatest { products ->
            Log.d(TAG, "Flash sale products: $products")
        }
    }
    companion object {
        private const val TAG = "HomeViewModel"
    }
}