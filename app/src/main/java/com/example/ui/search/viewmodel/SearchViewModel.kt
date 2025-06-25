package com.example.ui.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.Resource
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

    // It performs the search operation when the user types a query, and updates
    // the interface according to the result (loading - data - error)

    fun searchProducts(query:String){
      viewModelScope.launch {
          _searchState.value= Resource.Loading()

          try {
              val results = productsRepository.searchProducts(query)
              _searchState.value=Resource.Success(results)

          }catch (e:Exception){
              e.printStackTrace()
              _searchState.value= Resource.Error(e)

          }


      }
    }

}