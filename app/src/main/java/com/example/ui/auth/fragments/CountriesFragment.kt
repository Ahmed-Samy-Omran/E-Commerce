package com.example.ui.auth.fragments

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentCountriesBinding
import com.example.ui.auth.adapter.CountriesAdapter
import com.example.ui.auth.adapter.CountryClickListener
import com.example.ui.auth.models.CountryUIModel
import com.example.ui.auth.viewmodel.CountriesViewModel
import com.example.ui.common.fragments.BaseBottomSheetFragment
import com.example.ui.common.fragments.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CountriesFragment : BaseBottomSheetFragment<FragmentCountriesBinding, CountriesViewModel>(),
    CountryClickListener {

    override val viewModel: CountriesViewModel by viewModels()

    override fun getLayoutResId(): Int = R.layout.fragment_countries

    override fun init() {
        initViewModel()
    }

    private fun initViewModel() {

        lifecycleScope.launch {
            viewModel.countriesUIModelState.collectLatest {
                if(it.isEmpty()) return@collectLatest
                binding.progressBar.visibility = View.GONE
                binding.countriesLayout.visibility = View.VISIBLE

                val countriesAdapter = CountriesAdapter(it, this@CountriesFragment)
                binding.countriesRv.apply {
                    adapter = countriesAdapter
                    layoutManager = LinearLayoutManager(context)
                }
            }
        }
    }

    override fun onCountryClicked(country: CountryUIModel) {
        viewModel.saveUserCountry(country)
        dismiss()
    }

}