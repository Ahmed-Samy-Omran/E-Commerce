package com.example.ui.products.fragments

import ProductImagesAdapter
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.data.models.products.ProductSizeModel
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentProductDetailsBinding
import com.example.ui.common.fragments.BaseFragment
import com.example.ui.common.views.CircleView
import com.example.ui.common.views.sliderIndicatorsView
import com.example.ui.products.adapter.ProductColorAdapter
import com.example.ui.products.adapter.ProductSizeAdapter
import com.example.ui.products.model.ProductColorUIModel
import com.example.ui.products.model.ProductUIModel
import com.example.ui.products.viewmodel.ProductDetailsViewModel
import com.example.ui.reviews.ReviewFilter
import com.example.ui.reviews.adapter.ReviewAdapter
import com.example.ui.reviews.model.ReviewUIModel
import com.example.utils.HorizontalSpaceItemDecoration
import com.example.utils.ViewButtonAnimations
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailsFragment :
    BaseFragment<FragmentProductDetailsBinding, ProductDetailsViewModel>() {

    override val viewModel: ProductDetailsViewModel by activityViewModels()

    private lateinit var sizeAdapter: ProductSizeAdapter
    private lateinit var colorAdapter: ProductColorAdapter

    private val reviewAdapter = ReviewAdapter()
    private var allReviews: MutableList<ReviewUIModel> = mutableListOf()

    override fun getLayoutResId(): Int = R.layout.fragment_product_details

    override fun init() {
        observeProduct()
        observeSizes()
        observeSelectedSize()
        observeDescription()
        setupReviewRecyclerView()
        observeReviews()




    }

//    private fun observeReviews() {
//        lifecycleScope.launch {
//            viewModel.reviews.collectLatest { reviews ->
//                if (reviews.isNotEmpty()) {
//                    Log.d("ProductDetailsFragment", "Received ${reviews.size} reviews")
//                    allReviews = listOf(reviews.first()).toMutableList()
//                    reviewAdapter.submitList(allReviews)
//                } else {
//                    Log.d("ProductDetailsFragment", "No reviews available")
//                }
//            }
//        }
//    }

    private fun observeReviews() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.reviews.collectLatest { reviews ->
                Log.d("ProductDetailsFragment", "Reviews data: $reviews")
                if (reviews.isNotEmpty()) {
                    Log.d("ProductDetailsFragment", "Received ${reviews.size} reviews")
                    allReviews = listOf(reviews.first()).toMutableList()
                    reviewAdapter.submitList(allReviews)
                    binding.reviewsRecyclerView.visibility = View.VISIBLE
                    binding.noReviewsAnimation.visibility = View.GONE
                    binding.noReviewsText.visibility = View.GONE
                    binding.btnWriteReview.visibility = View.GONE
                } else {
                    Log.d("ProductDetailsFragment", "No reviews available")
                    binding.reviewsRecyclerView.visibility = View.GONE
                    binding.noReviewsAnimation.visibility = View.VISIBLE
                    binding.noReviewsText.visibility = View.VISIBLE
                    binding.btnWriteReview.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun observeDescription() {
        lifecycleScope.launch {
            viewModel.description.collectLatest { description ->
                binding.productDescriptionTextView.text = description ?: "No description available"
            }
        }
    }

    private fun observeProduct() {
        lifecycleScope.launch {
            viewModel.productDetailsState.collectLatest { product ->
                product.let { initUI(it) }
            }
        }
    }

    private fun initUI(product: ProductUIModel) {
        binding.product = product
        binding.productName.text = product.name
        binding.productRate.rating = product.rate
        binding.price.text = product.getFormattedPrice()
        binding.titleTv.text = product.name  // Fix: Set title in the AppBar

        initImages(product.images)

        binding.moveToReview.setOnClickListener {
            ViewButtonAnimations.playClickScaleAnimation(it) {
                // Navigate to the review section
                findNavController().navigate(R.id.action_productDetailsFragment_to_reviewFragment)
            }

        }


        binding.btnWriteReview.setOnClickListener {
            ViewButtonAnimations.playClickScaleAnimation(it) {
                findNavController().navigate(R.id.action_productDetailsFragment_to_addReviewFragment)
            }
        }

        // Check if the product is a bag based on categories_ids
        val isBag = product.categoriesIDs.contains("KvqnQX2dNNDkMpnXqY1g")
        binding.selectSizeTv.visibility = if (isBag) View.GONE else View.VISIBLE

        if (isBag && product.colors.isEmpty().not()) {
            // For bags, set colors directly from product.colors
            val colorModels = product.colors.map { ProductColorUIModel(color = it.color) } ?: emptyList()
            setupColorRecyclerView(colorModels)
            colorModels.firstOrNull()?.color?.let { viewModel.selectColor(it) }
        }
    }


    private fun initImages(images: List<String>) {
        val indicators = mutableListOf<CircleView>()
        sliderIndicatorsView(
            requireContext(),
            binding.productImagesViewPager,
            binding.indicatorView,
            indicators,
            images.size
        )
        binding.productImagesViewPager.adapter = ProductImagesAdapter(images)
    }

    private fun observeSizes() {
        lifecycleScope.launch {
            viewModel.sizeMap.collectLatest { sizeMap ->
                val sizes = sizeMap.keys.map { ProductSizeModel(size = it, stock = 1) }
                setupSizeRecyclerView(sizes)

                // Auto-select first size
                sizes.firstOrNull()?.size?.let { firstSize ->
                    viewModel.selectSize(firstSize)
                }
            }
        }
    }



    private fun observeSelectedSize() {
        lifecycleScope.launch {
            viewModel.selectedSize.collectLatest { selectedSize ->
                val isBag = viewModel.productDetailsState.value.categoriesIDs.contains("KvqnQX2dNNDkMpnXqY1g")

                val colorModels = if (isBag) {
                    // For bags, use colors directly from product.colors
                    viewModel.productDetailsState.value.colors.map { ProductColorUIModel(color = it.color) }
                        ?: emptyList()
                } else {
                    // For non-bags, use size-based color selection
                    viewModel.sizeMap.value[selectedSize]
                        ?.mapNotNull { it.color }
                        ?.distinct()
                        ?.map { ProductColorUIModel(color = it) }
                        ?: emptyList()
                }

                setupColorRecyclerView(colorModels)

                // Auto-select first color
                colorModels.firstOrNull()?.color?.let { firstColor ->
                    viewModel.selectColor(firstColor)
                }
            }
        }
    }


    private fun setupColorRecyclerView(colors: List<ProductColorUIModel>) {
        // Initialize the adapter if not already initialized
        if (!::colorAdapter.isInitialized) {
            colorAdapter = ProductColorAdapter { colorModel ->
                colorModel.color?.let { viewModel.selectColor(it) }
            }

            binding.colorsRecyclerView.apply {
                adapter = colorAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

                // Clear existing decorations before adding to avoid stacking
                if (itemDecorationCount == 0) {
                    addItemDecoration(HorizontalSpaceItemDecoration(45))
                }
            }
        }

        // Update the adapter's list using submitList for efficient updates
        colorAdapter.submitList(colors)
    }

    private fun setupSizeRecyclerView(sizes: List<ProductSizeModel>) {
        sizeAdapter = ProductSizeAdapter(sizes) { sizeModel ->
            sizeModel.size?.let { viewModel.selectSize(it) }
        }

        binding.sizesRecyclerView.apply {
            adapter = sizeAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(HorizontalSpaceItemDecoration(16))
        }

        sizes.firstOrNull()?.size?.let { viewModel.selectSize(it) }
    }

    private fun setupReviewRecyclerView() {

        binding.reviewsRecyclerView.apply {
            adapter = reviewAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

    }
}
