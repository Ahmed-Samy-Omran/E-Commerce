package com.example.ui.products.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.models.Resource
import com.example.data.models.reviews.ReviewModel
import com.example.data.repository.products.ProductsRepository
import com.example.data.repository.reviews.ReviewsRepo
import com.example.domain.models.toProductUIModel
import com.example.domain.models.toReviewUIModel
import com.example.ui.products.ProductDetailsActivity
import com.example.ui.products.model.ProductColorUIModel
import com.example.ui.products.model.ProductUIModel
import com.example.ui.reviews.model.ReviewUIModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val productsRepository: ProductsRepository,
    private val reviewRepository: ReviewsRepo
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

    private val _reviews = MutableStateFlow<List<ReviewUIModel>>(emptyList())
    val reviews: StateFlow<List<ReviewUIModel>> = _reviews

    private val _addReviewResult = MutableLiveData<Boolean>()
    val addReviewResult: LiveData<Boolean> get() = _addReviewResult

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // LiveData to hold existing review (if any)
    private val _existingUserReview = MutableLiveData<ReviewModel?>()
    val existingUserReview: LiveData<ReviewModel?> get() = _existingUserReview

    // 🔥 ADD THIS — to store selected images temporarily in ViewModel
    val selectedReviewImages = mutableListOf<Uri>()

    init {
        listenToProductDetails()
        fetchReviews()
        checkIfUserReviewed() // Check if current user has already reviewed
    }

    // Function to check if current user already submitted a review
    fun checkIfUserReviewed() = viewModelScope.launch(IO) {
        try {
            val snapshot = reviewRepository.checkIfUserReviewed(productUiModel.id, getCurrentUserId())
            val review = snapshot.documents.firstOrNull()?.toObject(ReviewModel::class.java)
            _existingUserReview.postValue(review)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking existing review: ${e.message}")
        }
    }

    private fun listenToProductDetails() = viewModelScope.launch(IO) {
        productsRepository.listenToProductDetails(productUiModel.id).collectLatest { productModel ->
            val uiModel = productModel.toProductUIModel()
            _productDetailsState.value = uiModel

            // Update the description state
            _description.value = uiModel.description

            // Check if the product is a bag (e.g., sizes is empty)
            val isBag = uiModel.sizes.isEmpty()

            if (isBag) {
                // For bags, use colors directly without size grouping
                // Convert ProductColorModel to ProductColorUIModel and filter by stock > 0
                val bagColors = uiModel.colors.map { ProductColorUIModel(color = it.color) }
                    .filter { it.color != null && (it.stock ?: 0) > 0 } ?: emptyList()
                _sizeMap.value = mapOf("" to bagColors) // Use empty string key as a fallback
            } else {
                // For non-bag products, group colors by size
                val sizeGroupedMap = uiModel.colors
                    .groupBy { it.size ?: "" }
                    .mapValues { it.value.filter { (it.stock ?: 0) > 0 } }
                    .filterKeys { it.isNotBlank() }
                _sizeMap.value = sizeGroupedMap
            }

            // Auto-select the first size if available (for non-bags)
            if (!isBag && uiModel.sizes?.isNotEmpty() == true) {
                uiModel.sizes.firstOrNull()?.size?.let { _selectedSize.value = it }
            }
        }
    }

    fun fetchReviews() = viewModelScope.launch(IO) {
        Log.d(TAG, "Fetching reviews for product ID: ${productUiModel.id}")
        try {
            reviewRepository.getReviews(productUiModel.id).collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val uiReviews = resource.data?.map { it.toReviewUIModel() }
                        Log.d(TAG, "Fetched ${uiReviews?.size ?: 0} reviews")
                        uiReviews?.let { _reviews.emit(it) }
                    }

                    is Resource.Error -> {
                        Log.e(TAG, "Error fetching reviews: ${resource.exception?.message}")
                    }

                    is Resource.Loading -> {
                        Log.d(TAG, "Fetching reviews: Loading")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in fetchReviews: ${e.message}")
        }
    }


//    fun addReview(productId: String, reviewText: String, rating: Int) {
//
//        // Prevent duplicate review submission
//        if (_existingUserReview.value != null) {
//            _error.value = "You have already submitted a review for this product."
//            return
//        }
//
//        val reviewUIModel = ReviewUIModel(
//            id = "", // Firestore will generate it
//            userId = getCurrentUserId(),                  // <-- Add this line
//            userName = getCurrentUserName(),
//            imageUrl = getCurrentUserImageUrl(),
//            rating = rating,
//            reviewText = reviewText,
//            reviewImages = emptyList(),
//            formattedDate = ""
//        )
//
//        viewModelScope.launch {
//            when (val result = reviewRepository.addReview(productId, reviewUIModel)) {
//                is Resource.Success -> {
//                    _addReviewResult.value = true
//                    fetchReviews()
//                    checkIfUserReviewed() // Refresh to prevent duplicate again
//                }
//                is Resource.Error -> _error.value = result.exception?.message
//                else -> {}
//            }
//        }
//    }

    fun addReview(productId: String, reviewText: String, rating: Int) {
        // Prevent duplicate review submission
        if (_existingUserReview.value != null) {
            _error.value = "You have already submitted a review for this product."
            return
        }

        viewModelScope.launch {
            try {
                // 🔥 ADD THIS — Upload selected images and get URLs
                val uploadedImageUrls = reviewRepository.uploadReviewImages(selectedReviewImages)

                // 🔥 UPDATE THIS — Now include uploaded images
                val reviewUIModel = ReviewUIModel(
                    id = "",
                    userId = getCurrentUserId(),
                    userName = getCurrentUserName(),
                    imageUrl = getCurrentUserImageUrl(),
                    rating = rating,
                    reviewText = reviewText,
                    reviewImages = uploadedImageUrls,
                    formattedDate = "" // You can format timestamp later
                )

                // 🔥 Save review to Firestore
                when (val result = reviewRepository.addReview(productId, reviewUIModel)) {
                    is Resource.Success -> {
                        _addReviewResult.value = true
                        fetchReviews()
                        checkIfUserReviewed()
                        clearSelectedReviewImages()
                    }
                    is Resource.Error -> _error.value = result.exception?.message
                    else -> {}
                }

            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun clearSelectedReviewImages() {
        selectedReviewImages.clear()
    }

    fun resetAddReviewResult() {
        _addReviewResult.value = false
    }


    val productId: String
        get() = productUiModel.id

    fun getCurrentUserId(): String {
        return reviewRepository.getCurrentUserId()
    }

    fun getCurrentUserName(): String {
        return reviewRepository.getCurrentUserName()
    }

    fun getCurrentUserImageUrl(): String {
        return reviewRepository.getCurrentUserImageUrl()
    }

    fun setSelectedReviewImages(uris: List<Uri>) {
        selectedReviewImages.clear()
        selectedReviewImages.addAll(uris)
    }


    companion object {
        private const val TAG = "ProductDetailsViewModel"
    }
}

