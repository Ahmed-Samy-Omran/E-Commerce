package com.example.ui.home.viewmodel

import android.util.Log
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.data.models.Resource
import com.example.data.models.products.ProductModel
import com.example.data.models.products.ProductSaleType
import com.example.data.models.sale_ads.SalesAdModel
import com.example.data.models.user.CountryData
import com.example.data.repository.categories.CategoriesRepository
import com.example.data.repository.home.SalesAdsRepository
import com.example.data.repository.products.ProductsRepository
import com.example.data.repository.special_sections.SpecialSectionsRepository
import com.example.data.repository.user.UserPreferenceRepository
import com.example.domain.models.toProductUIModel
import com.example.domain.models.toSpecialSectionUIModel
import com.example.ui.home.model.SalesAdUIModel
import com.example.ui.products.model.ProductUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val salesAdsRepository: SalesAdsRepository,
    private val categoriesRepository: CategoriesRepository,
    private val productsRepository: ProductsRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
    private val specialSectionsRepository: SpecialSectionsRepository
) : ViewModel() {

    val salesAdsStateTemp = salesAdsRepository.getSalesAds().stateIn(
        viewModelScope + IO, started = SharingStarted.Eagerly, initialValue = Resource.Loading()
    )

    // i apply stateIn to my flow to convert it from cold flow to hot flow
    val categoriesState = categoriesRepository.getCategories().stateIn(
        viewModelScope + IO, started = SharingStarted.Eagerly, initialValue = Resource.Loading()
    )

    val countryState = userPreferenceRepository.getUserCountry().stateIn(
        viewModelScope + IO,
        started = SharingStarted.Eagerly,
        initialValue = CountryData.getDefaultInstance()
    )






    @OptIn(ExperimentalCoroutinesApi::class)
    val flashSaleState = countryState.flatMapLatest { country ->
        Log.d(TAG, "Country ID for flash sale: ${country.id}")
        productsRepository.getSaleProducts(country.id, ProductSaleType.FLASH_SALE.type, 10)
    }.catch { emit(emptyList()) } // Prevent crashes if an error occurs
        .stateIn(viewModelScope + IO, started = SharingStarted.Eagerly, initialValue = emptyList())


    @OptIn(ExperimentalCoroutinesApi::class)
    val megaSaleState = countryState.flatMapLatest { country ->
        Log.d(TAG, "Country ID for mega sale: ${country.id}")
        productsRepository.getSaleProducts(country.id, ProductSaleType.MEGA_SALE.type, 10)
    }.catch { emit(emptyList()) } // Prevent crashes if an error occurs
        .stateIn(viewModelScope + IO, started = SharingStarted.Eagerly, initialValue = emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val recommendedSectionDataState = specialSectionsRepository.recommendProductsSection().stateIn(
        viewModelScope + IO, started = SharingStarted.Eagerly, initialValue = null
    ).mapLatest { it?.toSpecialSectionUIModel()}

    val isRecommendedSection = recommendedSectionDataState.map { it == null}.asLiveData()


    val isEmptyFlashSale: StateFlow<Boolean> = flashSaleState.map {
        val isEmpty = it.isEmpty()
        Log.d(TAG, "isEmptyFlashSale: $isEmpty")
        isEmpty
    }.stateIn(
        viewModelScope, started = SharingStarted.Eagerly, initialValue = true
    )


    val isEmptyMegaSale: StateFlow<Boolean> = megaSaleState.map {
        val isEmpty = it.isEmpty()
        Log.d(TAG, "isEmptyMegaSale: $isEmpty")
        isEmpty
    }.stateIn(
        viewModelScope, started = SharingStarted.Eagerly, initialValue = true
    )


    fun stopTimer() {
        salesAdsStateTemp.value.data?.forEach { it.stopCountdown() }
    }

    fun startTimer() {
        salesAdsStateTemp.value.data?.forEach { it.startCountdown() }
    }

//    fun getFlashSaleProducts() = viewModelScope.launch(IO) {
//        val country = userPreferenceRepository.getUserCountry().first()
//
//        productsRepository.getSaleProducts(
//            country.id, ProductSaleType.FLASH_SALE.type, 10
//        ).collectLatest { products ->
//            Log.d(TAG, "Flash sale products: $products")
//        }
//    }


//    fun getFlashSaleProducts() = viewModelScope.launch(IO) {
//        productsRepository.getSaleProducts(
////            country.id
//            "EO8zdYKPeomrYLG7zYY3", ProductSaleType.FLASH_SALE.type, 10
//        ).collectLatest { products ->
//            Log.d(TAG, "Flash sale products: $products")
//        }
//    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}

