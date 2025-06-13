package com.example.ui.reviews.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentReviewBinding
import com.example.ui.common.fragments.BaseFragment
import com.example.ui.products.viewmodel.ProductDetailsViewModel
import com.example.ui.reviews.ReviewFilter
import com.example.ui.reviews.adapter.ReviewAdapter
import com.example.ui.reviews.model.ReviewUIModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReviewFragment : BaseFragment<FragmentReviewBinding, ProductDetailsViewModel>() { // Ensure this is 'ReviewFragment'

    override val viewModel: ProductDetailsViewModel by activityViewModels()
    override fun getLayoutResId(): Int = R.layout.fragment_review

    private lateinit var reviewAdapter: ReviewAdapter
    private var allReviews: List<ReviewUIModel> = emptyList()

    // Now, populate the init() method as it's called after binding is ready
    override fun init() {
        Log.d("ReviewFragment", "init() called") // Will be called after binding is ready

        setupRecyclerView()
        setupRatingFilters()

        binding.reviewTitleTv.text = "All Reviews"

        binding.chipAll.isChecked = true
        // Post the animation to ensure layout pass (and chip dimensions) are ready
        binding.chipAll.post {
            animateChip(binding.chipAll, 1.1f)
        }

        binding.writeReviewBtn.setOnClickListener {
            findNavController().navigate(R.id.action_reviewFragment_to_addReviewFragment)
        }

        observeReviews()
        viewModel.fetchReviews()
        updateReviewList(null) // Ensure initial list update
    }

    // You can remove onViewCreated or leave it for anything that doesn't depend on binding immediately
    // If you keep onViewCreated, ensure it doesn't duplicate init() logic
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Anything here must NOT depend on 'binding' being initialized *yet* unless it's handled carefully.
        // It's generally safer to put binding-dependent logic in init().
    }

    private fun setupRecyclerView() {
        reviewAdapter = ReviewAdapter()
        binding.reviewsRecyclerView.apply {
            adapter = reviewAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupRatingFilters() {
        with(binding.ratingChipGroup) {
            isSingleSelection = true
            binding.chipAll.isChecked = true

            setOnCheckedStateChangeListener { group, checkedIds ->
                for (i in 0 until childCount) {
                    animateChip(getChildAt(i), 1.0f)
                }

                val selectedChip = checkedIds.firstOrNull()?.let { group.findViewById<View>(it) }
                selectedChip?.let { animateChip(it, 1.2f) }

                val selectedRating = getSelectedRating()

                binding.reviewTitleTv.text = when (selectedRating) {
                    null -> "All Reviews"
                    else -> "$selectedRating Stars"
                }

                updateReviewList(selectedRating)
                binding.reviewsRecyclerView.smoothScrollToPosition(0)
            }
        }
    }

    private fun animateChip(view: View, scale: Float) {
        view.animate().scaleX(scale).scaleY(scale).setDuration(200).start()
    }

    private fun observeReviews() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.reviews.collectLatest { reviews ->
                    Log.d("ReviewFragment", "Received reviews: ${reviews.size}")
                    allReviews = reviews

                    val selectedRating = getSelectedRating()
                    updateReviewList(selectedRating)
                }
            }
        }
    }

    private fun updateReviewList(rating: Int?) {
        val filteredReviews = ReviewFilter.filterReviews(allReviews, rating)
        reviewAdapter.submitList(filteredReviews)

        val isEmpty = filteredReviews.isEmpty()
        binding.reviewsRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.noReviewsAnimation.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.noReviewsText.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun getSelectedRating(): Int? {
        return when (binding.ratingChipGroup.checkedChipId) {
            binding.chip1Star.id -> 1
            binding.chip2Stars.id -> 2
            binding.chip3Stars.id -> 3
            binding.chip4Stars.id -> 4
            binding.chip5Stars.id -> 5
            else -> null // chipAll
        }
    }
}