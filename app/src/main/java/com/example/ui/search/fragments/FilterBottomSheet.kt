package com.example.ui.search.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import com.example.data.models.search_filter.FilterSortOptions
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FilterSortBottomSheetBinding
import com.example.ui.common.fragments.BaseBottomSheetFragment
import com.example.ui.search.viewmodel.SearchViewModel

class FilterSortBottomSheet(
    private val onApplyFilters: (FilterSortOptions) -> Unit
) : BaseBottomSheetFragment<FilterSortBottomSheetBinding, SearchViewModel>() {

    override val viewModel: SearchViewModel by activityViewModels()
    override fun getLayoutResId(): Int = R.layout.filter_sort_bottom_sheet

    // ADDED: Variables to track current slider values
    private var selectedMinPrice = 0
    private var selectedMaxPrice = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restoreState()
        setupActions()
        setupSliderInitialState()
        setupSliderListener()
    }


    private fun setupSliderInitialState() {
        // ADDED: Get initial values from slider and update text views
        val min = binding.rangeSlider.values.getOrNull(0)?.toInt() ?: 0
        val max = binding.rangeSlider.values.getOrNull(1)?.toInt() ?: 0
        binding.minPriceTv.text = formatPrice(min)
        binding.maxPriceTv.text = formatPrice(max)

        // ADDED: Store initial values in variables
        selectedMaxPrice = max
        selectedMinPrice = min
    }

    private fun setupSliderListener() {
        // ADDED: Listen to slider changes and update text views and variables
        binding.rangeSlider.addOnChangeListener { slider, _, _ ->
            if (slider.values.size >= 2) {
                val minPrice = slider.values[0].toInt()
                val maxPrice = slider.values[1].toInt()
                binding.minPriceTv.text = formatPrice(minPrice)
                binding.maxPriceTv.text = formatPrice(maxPrice)

                // ADDED: Update the stored values when slider changes
                selectedMinPrice = minPrice
                selectedMaxPrice = maxPrice
            }
        }
    }

    override fun init() {}

    // 1. Restore saved state (single responsibility)
    private fun restoreState() {
        viewModel.lastUsedFilterSortOptions?.let { options ->
            with(binding) {

                checkboxAvailable.isChecked = options.filterAvailable
                radioBtnLowToHigh.isChecked = options.sortLowToHigh
                radioBtnHighToLow.isChecked = options.sortHighToLow
                radioLessThan3Stars.isChecked = options.filterRatingLess3
                radio3StarsAndAbove.isChecked = options.filterRatingAbove3

                // ADDED: Restore slider values from saved options
                rangeSlider.values = listOf(
                    options.selectedMinPrice.toFloat(),
                    options.selectedMaxPrice.toFloat()
                )

                // ADDED: Update stored variables with restored values
                selectedMinPrice = options.selectedMinPrice
                selectedMaxPrice = options.selectedMaxPrice

                // ADDED: Update text views with restored values
                minPriceTv.text = formatPrice(options.selectedMinPrice)
                maxPriceTv.text = formatPrice(options.selectedMaxPrice)

            }

        }
    }

    // 2. Handle all user actions
    private fun setupActions() {
        binding.applyFiltersBtn.setOnClickListener { applyAndDismiss() }
        binding.clearFiltersBtn.setOnClickListener { clearAndDismiss() }
    }

    private fun applyAndDismiss() {
        onApplyFilters(createCurrentOptions())
        dismiss()
    }

    private fun clearAndDismiss() {
        with(binding) {
            checkboxAvailable.isChecked = false
            radio3StarsAndAbove.isChecked = false
            radioLessThan3Stars.isChecked = false
            radioBtnHighToLow.isChecked = false
            radioBtnLowToHigh.isChecked = false

            // Reset slider to default values
            rangeSlider.values = listOf(50f, 1500f)
            minPriceTv.text = formatPrice(50)
            maxPriceTv.text = formatPrice(1500)
        }
        viewModel.lastUsedFilterSortOptions = null
        onApplyFilters(FilterSortOptions()) // Empty filters
        dismiss()
    }

    private fun createCurrentOptions() = FilterSortOptions(
        filterAvailable = binding.checkboxAvailable.isChecked,
        filterPrice = true, // ADDED: Always enable price filtering when applying filters
        selectedMinPrice = selectedMinPrice,
        selectedMaxPrice = selectedMaxPrice,

        filterRatingLess3 = binding.radioLessThan3Stars.isChecked,
        filterRatingAbove3 = binding.radio3StarsAndAbove.isChecked,
        sortLowToHigh = binding.radioBtnLowToHigh.isChecked,
        sortHighToLow = binding.radioBtnHighToLow.isChecked
    ).also { viewModel.lastUsedFilterSortOptions = it }



    private fun formatPrice(price: Int): String = "$price EGP"
}