package com.example.ui.products.viewmodel


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.products.ProductsRepository
import com.example.domain.models.toProductUIModel
import com.example.ui.products.ProductDetailsActivity
import com.example.ui.products.model.ProductColorUIModel
import com.example.ui.products.model.ProductUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val productsRepository: ProductsRepository
) : ViewModel() {

    private val productUiModel: ProductUIModel by lazy {
        savedStateHandle.get<ProductUIModel>(ProductDetailsActivity.PRODUCT_UI_MODEL_EXTRA)
            ?: throw IllegalArgumentException("ProductUIModel is required")
    }

    private val _productDetailsState = MutableStateFlow(productUiModel)
    val productDetailsState = _productDetailsState.asStateFlow()


    private val _selectedSize = MutableStateFlow<String?>(null)
    val selectedSize: StateFlow<String?> = _selectedSize


    // Store the map of sizes -> List of colors
    private val _sizeMap = MutableStateFlow<Map<String, List<ProductColorUIModel>>>(emptyMap())
    val sizeMap: StateFlow<Map<String, List<ProductColorUIModel>>> = _sizeMap

    private val _description = MutableStateFlow<String?>(null)
    val description: StateFlow<String?> = _description.asStateFlow()


    fun selectSize(size: String) {
        _selectedSize.value = size
    }

    private val _selectedColor = MutableStateFlow("")
    val selectedColor = _selectedColor.asStateFlow()

    fun selectColor(color: String) {
        _selectedColor.value = color
    }

    init {
        listenToProductDetails()
    }

    private fun listenToProductDetails() = viewModelScope.launch(IO) {
        productsRepository.listenToProductDetails(productUiModel.id).collectLatest { productModel ->
            val uiModel = productModel.toProductUIModel()
            _productDetailsState.value = uiModel

            // Update the description state
            _description.value = uiModel.description

            // Group color options by size instead of color
            val sizeGroupedMap = uiModel.colors
                .groupBy { it.size ?: "" }
                .mapValues { it.value.filter { it.stock ?: 0 > 0 } }
                .filterKeys { it.isNotBlank() }

            _sizeMap.value = sizeGroupedMap
        }
    }
}

