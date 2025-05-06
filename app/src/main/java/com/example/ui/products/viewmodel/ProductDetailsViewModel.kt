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

    private val _colorMap = MutableStateFlow<Map<String, List<ProductColorUIModel>>>(emptyMap())
    val colorMap = _colorMap.asStateFlow()

    private val _selectedSize = MutableStateFlow<String?>(null)
    val selectedSize: StateFlow<String?> = _selectedSize


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

            val colorGroupedMap = uiModel.colors
                .groupBy { it.color ?: "" }
                .mapValues { it.value.filter { it.stock ?: 0 > 0 } }
                .filterValues { it.isNotEmpty() }

            _colorMap.value = colorGroupedMap
        }
    }
}

//@HiltViewModel
//class ProductDetailsViewModel @Inject constructor() : ViewModel() {
//
//    private val _productDetailsState = MutableStateFlow<ProductUIModel?>(null)
//    val productDetailsState: StateFlow<ProductUIModel?> = _productDetailsState
//
//    private val _selectedSize = MutableStateFlow<String?>(null)
//    val selectedSize: StateFlow<String?> = _selectedSize
//
//    private val _selectedColor = MutableStateFlow<String?>(null)
//    val selectedColor: StateFlow<String?> = _selectedColor
//
//    val sizeMap = MutableStateFlow<Map<String, List<ProductColorUIModel>>>(emptyMap())
//    val colorMap = MutableStateFlow<Map<String, List<ProductColorUIModel>>>(emptyMap())
//
//    fun setProductDetails(product: ProductUIModel) {
//        _productDetailsState.value = product
//        buildSizeColorMaps(product.colors)
//    }
//
//    fun selectSize(size: String) {
//        _selectedSize.value = size
//    }
//
//    fun selectColor(color: String) {
//        _selectedColor.value = color
//    }
//
//    private fun buildSizeColorMaps(colors: List<ProductColorUIModel>) {
//        val sizeToColors = mutableMapOf<String, MutableList<ProductColorUIModel>>()
//        val colorToSizes = mutableMapOf<String, MutableList<ProductColorUIModel>>()
//
//        for (colorModel in colors) {
//            val size = colorModel.size ?: continue
//            val color = colorModel.color ?: continue
//
//            sizeToColors.getOrPut(size) { mutableListOf() }.add(colorModel)
//            colorToSizes.getOrPut(color) { mutableListOf() }.add(colorModel)
//        }
//
//        sizeMap.value = sizeToColors
//        colorMap.value = colorToSizes
//    }
//}