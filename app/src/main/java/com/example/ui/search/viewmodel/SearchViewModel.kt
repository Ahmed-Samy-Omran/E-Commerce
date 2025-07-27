package com.example.ui.search.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.Resource
import com.example.data.models.search_filter.FilterSortOptions
import com.example.data.repository.products.ProductsRepository
import com.example.ui.products.model.ProductUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productsRepository: ProductsRepository
) : ViewModel() {

    // success state
    private val _searchState = MutableStateFlow<Resource<List<ProductUIModel>>>(Resource.Loading())
    val searchState: StateFlow<Resource<List<ProductUIModel>>> = _searchState

    private var originalList: List<ProductUIModel> = emptyList()

    //  Save the last used filter and sort options
    var lastUsedFilterSortOptions: FilterSortOptions? = null

    // It performs the search operation when the user types a query, and updates
    // the interface according to the result (loading - data - error)

    fun searchProducts(query: String) {
        viewModelScope.launch {
            _searchState.value = Resource.Loading()

            try {
                val results = productsRepository.searchProducts(query)
                // to make sure the original list is updated with the results
                originalList = results
                _searchState.value = Resource.Success(results)

            } catch (e: Exception) {
                e.printStackTrace()
                _searchState.value = Resource.Error(e)

            }


        }
    }

    fun applyFiltersAndSort(options: FilterSortOptions) {
        var filteredList = originalList
        Log.d("FilterDebug", "Original list size = ${originalList.size}")


        // Filter by availability
        if (options.filterAvailable) {
            filteredList = filteredList.filter { it.inStock }
        }

        // Filter by price

        if (options.filterPrice) {
            Log.d("FilterDebug", "Filtering by price: ${options.selectedMinPrice} - ${options.selectedMaxPrice}")
            filteredList = filteredList.filter { product ->
                val actualPrice =if(product.salePercentage != null && product.salePercentage > 0) {
                    product.price - (product.price * product.salePercentage / 100)
                } else {
                    product.price
                }
                actualPrice in options.selectedMinPrice..options.selectedMaxPrice
            }
        } else {
            Log.d("FilterDebug", "No price filter applied")
        }
        // Filter by rating

        filteredList = when {
            options.filterRatingLess3 -> filteredList.filter { it.rate < 3 }
            options.filterRatingAbove3 -> filteredList.filter { it.rate > 3 }
            else -> filteredList

        }
        // Sorting logic
        filteredList = when {
            options.sortLowToHigh -> filteredList.sortedBy { it.price }
            options.sortHighToLow -> filteredList.sortedByDescending { it.price }
            else -> filteredList
        }
        //  Save for next bottom sheet use
        lastUsedFilterSortOptions = options
        _searchState.value = Resource.Success(filteredList)

    }
}
//
//    private fun filterAndSortProducts(
//        productList: List<ProductUIModel>,
//        selectedMinPrice: Int,
//        selectedMaxPrice: Int,
//        sortOption: FilterSortOptions
//    ): List<ProductUIModel> {
//        val filteredList = productList.filter { product ->
//            val price = product.price
//            val salePercentage = product.salePercentage
//
//            val finalPrice = if (salePercentage != null && salePercentage > 0) {
//                price - (price * salePercentage / 100)
//            } else {
//                price
//            }
//
//            finalPrice in selectedMinPrice..selectedMaxPrice
//        }
//    }
//    }
//
//


