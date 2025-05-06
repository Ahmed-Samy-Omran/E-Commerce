package com.example.ui.products.fragments

import ProductImagesAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.data.models.products.ProductSizeModel
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentProductDetailsBinding
import com.example.ui.common.fragments.BaseFragment
import com.example.ui.common.views.CircleView
import com.example.ui.common.views.sliderIndicatorsView
import com.example.ui.common.views.updateIndicators
import com.example.ui.products.adapter.ProductColorAdapter
import com.example.ui.products.adapter.ProductSizeAdapter
import com.example.ui.products.model.ProductColorUIModel
import com.example.ui.products.model.ProductUIModel
import com.example.ui.products.viewmodel.ProductDetailsViewModel
import com.example.utils.DepthPageTransformer
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
        observeColorMap()
        observeSelectedColor()
    }

    private fun observeProduct() {
        lifecycleScope.launch {
            viewModel.productDetailsState.collectLatest { product ->
                product?.let { initUI(it) }
            }
        }
    }

    private fun initUI(product: ProductUIModel) {
        binding.product = product
        binding.productName.text = product.name
        binding.productRate.rating = product.rate
        binding.price.text = product.getFormattedPrice()
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

    private fun observeColorMap() {
        lifecycleScope.launch {
            viewModel.colorMap.collectLatest { colorMap ->
                val colorModels = colorMap.keys.map { ProductColorUIModel(color = it) }
                setupColorRecyclerView(colorModels)

                // Auto-select first color
                colorModels.firstOrNull()?.color?.let { firstColor ->
                    viewModel.selectColor(firstColor)
                }
            }
        }
    }

    private fun observeSelectedColor() {
        lifecycleScope.launch {
            viewModel.selectedColor.collectLatest { selectedColor ->
                val sizeModels = viewModel.colorMap.value[selectedColor]
                    ?.mapNotNull { it.size }
                    ?.distinct()
                    ?.map { ProductSizeModel(size = it, stock = 1) }
                    ?: emptyList()

                setupSizeRecyclerView(sizeModels)
            }
        }
    }

    private fun setupColorRecyclerView(colors: List<ProductColorUIModel>) {
        colorAdapter = ProductColorAdapter(colors) { colorModel ->
            colorModel.color?.let { viewModel.selectColor(it) }
        }

        binding.colorsRecyclerView.apply {
            adapter = colorAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(HorizontalSpaceItemDecoration(16))
        }
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