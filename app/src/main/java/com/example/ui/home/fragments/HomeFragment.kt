package com.example.ui.home.fragments

import android.util.Log
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentHomeBinding
import com.example.ui.common.fragments.BaseFragment
import com.example.ui.common.views.CircleView
import com.example.ui.home.adapter.SalesAdAdapter
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
            viewModel.salesAdsStateTemp.collect {
                Log.d(TAG, "initViewModel: $it")
            }
        }
    }

    private fun initViews() {
        Log.d(TAG, "onViewCreated: HomeFragment")
        initSalesAdsView()
    }

    private fun initSalesAdsView() {

        val salesAds = listOf(
            SalesAdUIModel(
                title = "Super Flash Sale",
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/e-commerce-39c78.appspot.com/o/temps%2Fpromo_image.png?alt=media&token=6b4e45d7-b3d8-4823-b72e-53f1e3134e25",
                endAt = System.currentTimeMillis() + 3600000 // 1 hour from now
            ), SalesAdUIModel(
                title = "Limited Offer",
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/e-commerce-39c78.appspot.com/o/temps%2Fsale-banner-template-with-special-sale-vector.jpg?alt=media&token=b53a1702-4f9b-4d4c-811f-4d860c67da71",
                endAt = System.currentTimeMillis() + 7200000 // 2 hours from now
            )
        )

        initializeIndicators(salesAds.size)
        val adapter = SalesAdAdapter(salesAds)
        binding.saleAdsViewPager.adapter = adapter
        binding.saleAdsViewPager.setPageTransformer(DepthPageTransformer())

        binding.saleAdsViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicators(position)
            }
        })
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
                16, 16
            )
            params.setMargins(8, 0, 8, 0) // Margin between circles
            circleView.setLayoutParams(params)
            circleView.setRadius(8f) // Set radius
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

    companion object {
        private const val TAG = "HomeFragment"
    }
}