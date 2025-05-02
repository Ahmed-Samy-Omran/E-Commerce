package com.example.ui.products.fragments

import ProductImagesAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentProductDetailsBinding
import com.example.ui.common.fragments.BaseFragment
import com.example.ui.common.views.CircleView
import com.example.ui.common.views.sliderIndicatorsView
import com.example.ui.common.views.updateIndicators
import com.example.ui.products.adapter.ProductSizeAdapter
import com.example.ui.products.model.ProductUIModel
import com.example.ui.products.viewmodel.ProductDetailsViewModel
import com.example.utils.DepthPageTransformer
import com.example.utils.HorizontalSpaceItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailsFragment : BaseFragment<FragmentProductDetailsBinding, ProductDetailsViewModel>() {

    override val viewModel: ProductDetailsViewModel by activityViewModels()
    private lateinit var sizeAdapter: ProductSizeAdapter

    override fun getLayoutResId(): Int = R.layout.fragment_product_details

    override fun init() {
        initViewModel()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            viewModel.productDetailsState.collectLatest {
                initView(it)
            }
        }
    }

    private fun initView(it: ProductUIModel) {

        binding.product = it
        it.name.let { binding.titleTv.text = it
        binding.productName.text=it
        }
        it.rate.let { binding.productRate.rating = it }
        it.price.let { binding.price.text = it.toString() }

        it.sizes.let { sizesList ->
            sizeAdapter = ProductSizeAdapter(sizesList) { selectedSize ->
                // Handle selected size here
                // For now, just log or show a toast
                Toast.makeText(requireContext(), "Selected size: ${selectedSize.size}", Toast.LENGTH_SHORT).show()
            }

            binding.sizesRecyclerView.apply {
                adapter = sizeAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                addItemDecoration(HorizontalSpaceItemDecoration(20)) // 16 pixels spacing
            }
        }

        initImagesView(it.images)
    }

    private var indicators = mutableListOf<CircleView>()
    private fun initImagesView(images: List<String>) {

        sliderIndicatorsView(
            requireContext(),
            binding.productImagesViewPager,
            binding.indicatorView,
            indicators,
            images.size
        )
        binding.productImagesViewPager.apply {
            adapter = ProductImagesAdapter(images)
            setPageTransformer(DepthPageTransformer())

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateIndicators(requireContext(), indicators, position)
                }
            })
        }
    }

    companion object
}