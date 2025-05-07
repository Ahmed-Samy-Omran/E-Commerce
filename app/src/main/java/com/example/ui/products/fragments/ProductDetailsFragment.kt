package com.example.ui.products.fragments

import ProductImagesAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.example.utils.HorizontalSpaceItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailsFragment :
    BaseFragment<FragmentProductDetailsBinding, ProductDetailsViewModel>() {

    override val viewModel: ProductDetailsViewModel by activityViewModels()

    private lateinit var sizeAdapter: ProductSizeAdapter
    private lateinit var colorAdapter: ProductColorAdapter

    override fun getLayoutResId(): Int = R.layout.fragment_product_details

    override fun init() {
        observeProduct()
        observeSizes()
        observeSelectedSize()
        observeDescription()

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
                val colorModels = viewModel.sizeMap.value[selectedSize]
                    ?.mapNotNull { it.color }
                    ?.distinct()
                    ?.map { ProductColorUIModel(color = it) }
                    ?: emptyList()

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
}
