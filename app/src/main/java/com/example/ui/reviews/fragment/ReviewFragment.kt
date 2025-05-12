package com.example.ui.reviews.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentProductDetailsBinding
import com.example.e_commerce.databinding.FragmentReviewBinding
import com.example.ui.common.fragments.BaseFragment
import com.example.ui.products.viewmodel.ProductDetailsViewModel
import com.example.ui.reviews.adapter.ReviewAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReviewFragment : BaseFragment<FragmentReviewBinding, ProductDetailsViewModel>() {

    override val viewModel: ProductDetailsViewModel by activityViewModels()
    override fun getLayoutResId(): Int = R.layout.fragment_review

    private lateinit var reviewAdapter: ReviewAdapter

    override fun init() {
        Log.d("ReviewFragment", "init() called")
        setupRecyclerView()
        observeReviews()
        viewModel.fetchReviews()
    }

    private fun setupRecyclerView() {
        reviewAdapter = ReviewAdapter()
        binding.reviewsRecyclerView.apply {
            adapter = reviewAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

//    private fun observeReviews() {
//        lifecycleScope.launch {
//            viewModel.reviews.collectLatest { reviews ->
//                reviewAdapter.submitList(reviews)
//            }
//        }
//    }

    private fun observeReviews() {
        lifecycleScope.launch {
            viewModel.reviews.collectLatest { reviews ->
                if (reviews.isNotEmpty()) {
                    Log.d("ReviewFragment", "Received ${reviews.size} reviews")
                    reviews.forEach { review ->
                        Log.d("ReviewFragment", "Review: ${review.userName}, Rating: ${review.rating}")
                    }
                    reviewAdapter.submitList(reviews)
                } else {
                    Log.d("ReviewFragment", "No reviews received or list is empty")
                }
            }
        }
    }
}