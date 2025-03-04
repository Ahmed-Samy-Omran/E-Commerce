package com.example.ui.home.fragments

import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.data.models.Resource
import com.example.data.models.sale_ads.SalesAdModel
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentHomeBinding
import com.example.ui.common.fragments.BaseFragment
import com.example.ui.common.views.CircleView
import com.example.ui.home.adapter.CategoriesAdapter
import com.example.ui.home.adapter.SalesAdAdapter
import com.example.ui.home.model.CategoryUIModel
import com.example.ui.home.model.SalesAdUIModel
import com.example.ui.home.viewmodel.HomeViewModel
import com.example.utils.DepthPageTransformer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    override val viewModel: HomeViewModel by viewModels()
    override fun getLayoutResId(): Int = R.layout.fragment_home


    override fun init() {
        initViews()
        initViewModel()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            viewModel.salesAdsStateTemp.collect {resources->
                when(resources){
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

//        binding.searchTv.paintFlags = binding.searchTv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG;

        lifecycleScope.launch {
            viewModel.categoriesState.collect { resources ->
                when (resources) {
                    is Resource.Loading -> {
                        Log.d(TAG, "iniViewModel: categories Loading")
                    }

                    is Resource.Success -> {
//                        binding.categoriesShimmerView.root.stopShimmer()
//                        binding.categoriesShimmerView.root.visibility = View.GONE
                        Log.d(TAG, "iniViewModel: categories Success = ${resources.data}")
                        initCategoriesView(resources.data)
                    }

                    is Resource.Error -> {
                        Log.d(TAG, "iniViewModel: categories Error")
                    }
                }
            }
        }


    }

    private fun initCategoriesView(data: List<CategoryUIModel>?) {
        if (data.isNullOrEmpty()) {
            return
        }
        val categoriesAdapter = CategoriesAdapter(data)
        binding.categoriesRecyclerView.apply {
            adapter = categoriesAdapter
            setHasFixedSize(true)   // because the recyclerview size is fixed and small so we can set it to true to increase performance
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
            )
        }
    }



    private fun initViews() {
        Log.d(TAG, "onViewCreated: HomeFragment")

    }

    private fun initSalesAdsView(salesAds: List<SalesAdUIModel>?) {

        if (salesAds.isNullOrEmpty()) {
            return
        }
        initializeIndicators(salesAds.size)
        val salesAdapter = SalesAdAdapter(lifecycleScope, salesAds)
        binding.saleAdsViewPager.apply {
            adapter = salesAdapter
            setPageTransformer(DepthPageTransformer())
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateIndicators(position)
                }
            })
        }
        // that responsible for the auto slide of the viewpager every 5 seconds
        lifecycleScope.launch(IO) {
            tickerFlow(5000).collect {
                withContext(Main) {
                    binding.saleAdsViewPager.setCurrentItem(
                        (binding.saleAdsViewPager.currentItem + 1) % salesAds.size, true
                    )
                }
            }
        }
    }


    private fun tickerFlow(period: Long) = flow {
        while (true) {
            emit(Unit)
            delay(period)
        }
    }

    private var indicators = mutableListOf<CircleView>()

    private fun initializeIndicators(count: Int) {
        for (i in 0 until count) {
            val circleView = CircleView(requireContext())
            val params = LinearLayout.LayoutParams(
                20, 20
            )
            params.setMargins(8, 0, 8, 0) // Margin between circles
            circleView.setLayoutParams(params)
            circleView.setRadius(10f) // Set radius
            circleView.setColor(     // change here the color you want
                if (i == 0) requireContext().getColor(R.color.primary_color) else requireContext().getColor(
                    R.color.neutral_grey
                )
            ) // First indicator is red
            indicators.add(circleView)
            binding.indicatorView.addView(circleView)
        }
    }

    private fun updateIndicators(position: Int) {
        for (i in 0 until indicators.size) {
            indicators[i].setColor(  // change here the color you want
                if (i == position) requireContext().getColor(R.color.primary_color) else requireContext().getColor(
                    R.color.neutral_grey
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.startTimer()
    }
    override fun onPause() {
        super.onPause()
        viewModel.stopTimer()
    }
    companion object {
        private const val TAG = "HomeFragment"
    }
}