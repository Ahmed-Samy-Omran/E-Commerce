package com.example.ui.home.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.data.models.Resource
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentHomeBinding
import com.example.ui.common.fragments.BaseFragment
import com.example.ui.common.views.CircleView
import com.example.ui.common.views.loadImage
import com.example.ui.common.views.sliderIndicatorsView
import com.example.ui.common.views.updateIndicators
import com.example.ui.home.MainActivity
import com.example.ui.home.adapter.CategoriesAdapter
import com.example.ui.home.adapter.ShimmerAdapter
import com.example.ui.home.adapter.SalesAdAdapter
import com.example.ui.home.model.CategoryUIModel
import com.example.ui.home.model.SalesAdUIModel
import com.example.ui.home.model.SpecialSectionUIModel
import com.example.ui.home.viewmodel.HomeViewModel
import com.example.ui.products.ProductDetailsActivity
import com.example.ui.products.ProductDetailsActivity.Companion.PRODUCT_UI_MODEL_EXTRA
import com.example.ui.products.adapter.ProductAdapter
import com.example.ui.products.adapter.ProductViewType
import com.example.ui.products.model.ProductUIModel
import com.example.ui.search.fragments.SearchFragment
import com.example.utils.DepthPageTransformer
import com.example.utils.GridSpacingItemDecoration
import com.example.utils.HorizontalSpaceItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    override val viewModel: HomeViewModel by viewModels()
    override fun getLayoutResId(): Int = R.layout.fragment_home

    override fun init() {
        initViews()
        iniViewModel()
//        binding.searchEt.setOnClickListener {
//            if (findNavController().currentDestination?.id == R.id.navigation_home)  {
//                findNavController().navigate(R.id.action_navigation_home_to_navigation_search)
//            } else {
//                Log.w(TAG, "Attempted to navigate from HomeFragment but not currently on home destination.")
//            }
//        }

        binding.searchEt.setOnClickListener {
            try {
                 //
                (requireActivity() as? MainActivity)?.navigateToSearchTab()
            }
            catch (e: Exception) {
                Log.e(TAG, "Navigation to search failed: ${e.message}")
                Toast.makeText(requireContext(), "Navigation failed", Toast.LENGTH_SHORT).show()
            }
        }

    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding.searchEt.setOnClickListener {
//         try {
//             findNavController().navigate(R.id.action_navigation_home_to_navigation_search)
//         }
//         catch (e: Exception) {
//             Log.e(TAG, "Navigation to search failed: ${e.message}")
//             Toast.makeText(requireContext(), "Navigation failed", Toast.LENGTH_SHORT).show()
//         }
//        }
//    }

    private fun iniViewModel() {
        lifecycleScope.launch {
            // Observe Sales Ads
            viewModel.salesAdsStateTemp.collect { resources ->
                when (resources) {
                    is Resource.Loading -> {
                        Log.d(TAG, "iniViewModel: Loading")
                    }

                    is Resource.Success -> {
                        binding.saleAdsShimmerView.root.stopShimmer()
                        binding.saleAdsShimmerView.root.visibility = View.GONE
                        initSalesAdsView(resources.data)
                    }

                    is Resource.Error -> {
                        Log.d(TAG, "iniViewModel: Error")
                    }
                }
            }
        }
            // Observe Categories
            lifecycleScope.launch {
                viewModel.categoriesState.collect { resources ->
                    when (resources) {
                        is Resource.Loading -> {
                            Log.d(TAG, "Categories Loading...")
                            showCategoriesShimmer()
                        }
                        is Resource.Success -> {
                            Log.d(TAG, "Categories Loaded Successfully.")
                            initCategoriesView(resources.data)

                        }
                        is Resource.Error -> {
                            binding.categoriesRecyclerView.visibility = View.VISIBLE                        }
                    }
                }
            }


        lifecycleScope.launch {
            viewModel.flashSaleState.collect { productsList ->
                if (productsList.isEmpty()) {
                    binding.flashSaleProductsRv.adapter = ShimmerAdapter(6, R.layout.item_product_shimmer)
                } else {
                    binding.flashSaleProductsRv.adapter = flashSaleAdapter
                    flashSaleAdapter.submitList(productsList)
                }
            }
        }

        // Mega Sale
        lifecycleScope.launch {
            viewModel.megaSaleState.collect { productsList ->
                if (productsList.isEmpty()) {
                    binding.megaSaleProductsRv.adapter = ShimmerAdapter(6, R.layout.item_product_shimmer)
                } else {
                    binding.megaSaleProductsRv.adapter = megaSaleAdapter
                    megaSaleAdapter.submitList(productsList)
                }
            }
        }


        lifecycleScope.launch {
            viewModel.recommendedSectionDataState.collectLatest { recommendedSectionData ->

                Log.d(TAG, "Recommended section data: $recommendedSectionData")


                if (recommendedSectionData == null) {
                    // Show shimmer
                    binding.recommendedShimmerView.root.visibility = View.VISIBLE
                    binding.recommendedShimmerView.root.startShimmer()
                    binding.recommendedProductLayout.visibility = View.GONE

                    // Optional: Add timeout logic
                    launch {
                        delay(10000) // 10 seconds timeout
                        // Re-check the flow's latest state
                        viewModel.recommendedSectionDataState.collectLatest { data ->
                            if (data == null) {
                                Log.d(TAG, "Recommended section data timeout")
                                binding.recommendedShimmerView.root.stopShimmer()
                                binding.recommendedShimmerView.root.visibility = View.GONE
                                // Optionally show an error message
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to load recommended section",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            // Exit after checking once
                            return@collectLatest
                        }
                    }
                } else {
                    // Stop shimmer and show content
                    binding.recommendedShimmerView.root.stopShimmer()
                    binding.recommendedShimmerView.root.visibility = View.GONE
                    binding.recommendedProductLayout.visibility = View.VISIBLE
                    setupRecommendedViewData(recommendedSectionData)
                }
                viewModel.getNextProducts()

                lifecycleScope.launch {
                    viewModel.allProductsState.collectLatest { productsList ->

                        if (productsList.isEmpty()) {
                            // Show shimmer for all products
                            binding.allProductsRv.adapter =
                                ShimmerAdapter(6, R.layout.item_product_shimmer)
                        } else {
                            // Show actual products
                            binding.allProductsRv.adapter = allProductsAdapter
                            allProductsAdapter.submitList(productsList)
                        }

                        binding.invalidateAll()
                    }
                }
            }
        }

    }

    private fun showCategoriesShimmer() {
        binding.categoriesRecyclerView.adapter =
            ShimmerAdapter(6, R.layout.categories_shimmer_view)
    }


    private fun setupRecommendedViewData(sectionData: SpecialSectionUIModel) {
        // Stop and hide shimmer
        binding.recommendedShimmerView.root.stopShimmer()
        binding.recommendedShimmerView.root.visibility = View.GONE

        // Show real layout
        binding.recommendedProductLayout.visibility = View.VISIBLE

        // Populate data
        loadImage(binding.recommendedProductIv, sectionData.image)
        binding.recommendedProductTitleIv.text = sectionData.title

        binding.recommendedProductLayout.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Recommended Product Clicked, goto ${sectionData.type}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }




    private fun initCategoriesView(data: List<CategoryUIModel>?) {
        if (data.isNullOrEmpty()) return

        val categoriesAdapter = CategoriesAdapter(data)
        binding.categoriesRecyclerView.adapter = categoriesAdapter
    }



    private val flashSaleAdapter by lazy {
        ProductAdapter(viewType = ProductViewType.LIST) {
            goToProductDetails(it)
        }
    }
    private val megaSaleAdapter by lazy {
        ProductAdapter(viewType = ProductViewType.LIST) {
            goToProductDetails(it)
        }
    }
    private val allProductsAdapter by lazy { ProductAdapter { goToProductDetails(it) } }

    private fun initViews() {
        // Flash Sale RecyclerView
        binding.flashSaleProductsRv.apply {
            adapter = flashSaleAdapter
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
            )
        }
            // Mega Sale RecyclerView
        binding.megaSaleProductsRv.apply {
            adapter = megaSaleAdapter
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
            )
            addItemDecoration(HorizontalSpaceItemDecoration(16))
        }
            // All Products RecyclerView
        binding.allProductsRv.apply {
            adapter = allProductsAdapter
            layoutManager = GridLayoutManager(
                requireContext(), 2
            )
            addItemDecoration(GridSpacingItemDecoration(2, 16, true))
        }


        // Categories RecyclerView initially with shimmer adapter
        binding.categoriesRecyclerView.apply {
            adapter =   ShimmerAdapter(6, R.layout.categories_shimmer_view)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(HorizontalSpaceItemDecoration(16))
        }


    }

    private fun initSalesAdsView(salesAds: List<SalesAdUIModel>?) {
        if (salesAds.isNullOrEmpty()) {
            return
        }

        sliderIndicatorsView(
            requireContext(),
            binding.saleAdsViewPager,
            binding.indicatorView,
            indicators,
            salesAds.size
        )

        val salesAdapter = SalesAdAdapter(lifecycleScope, salesAds)
        binding.saleAdsViewPager.apply {
            adapter = salesAdapter
            setPageTransformer(DepthPageTransformer())
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateIndicators(requireContext(), indicators,position)
                }
            })
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                tickerFlow(5000).collect {
                    binding.saleAdsViewPager.setCurrentItem(
                        (binding.saleAdsViewPager.currentItem + 1) % salesAds.size, true
                    )
                }
            }
        }

        // add animation from top to bottom
        binding.saleAdsViewPager.animate().translationY(0f).alpha(1f).setDuration(500).start()

    }

    private fun tickerFlow(period: Long) = flow {
        while (true) {
            emit(Unit)
            delay(period)
        }
    }

    private var indicators = mutableListOf<CircleView>()



    private fun goToProductDetails(product: ProductUIModel) {
        requireActivity().startActivity(
            Intent(
            requireActivity(), ProductDetailsActivity::class.java
        ).apply {
            putExtra(PRODUCT_UI_MODEL_EXTRA, product)
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.startTimer()

    }

    override fun onPause() {
        super.onPause()
        viewModel.stopTimer()
        binding.recommendedShimmerView.root.stopShimmer()
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}