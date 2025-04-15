package com.example.ui.products.fragments

import ProductImagesAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentProductDetailsBinding
import com.example.ui.common.fragments.BaseFragment
import com.example.ui.common.views.CircleView
import com.example.ui.common.views.sliderIndicatorsView
import com.example.ui.common.views.updateIndicators
import com.example.ui.products.model.ProductUIModel
import com.example.ui.products.viewmodel.ProductDetailsViewModel
import com.example.utils.DepthPageTransformer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailsFragment : BaseFragment<FragmentProductDetailsBinding, ProductDetailsViewModel>() {

    override val viewModel: ProductDetailsViewModel by activityViewModels()

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
        it.name.let { binding.titleTv.text = it }
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