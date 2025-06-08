package com.example.ui.reviews.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentProductDetailsBinding
import com.example.e_commerce.databinding.FragmentReviewBinding
import com.example.ui.common.fragments.BaseFragment
import com.example.ui.products.model.ProductUIModel
import com.example.ui.products.viewmodel.ProductDetailsViewModel
import com.example.ui.reviews.ReviewFilter
import com.example.ui.reviews.adapter.ReviewAdapter
import com.example.ui.reviews.model.ReviewUIModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

//
//@AndroidEntryPoint
//class ReviewFragment : Fragment() {
//
//    private var _binding: FragmentReviewBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var viewModel: ProductDetailsViewModel
//    private lateinit var reviewAdapter: ReviewAdapter
//    private var productUIModel: ProductUIModel? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Get the ProductUIModel from arguments
//        arguments?.let {
//            productUIModel = it.getParcelable("product_ui_model")
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentReviewBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Initialize ViewModel with the ProductUIModel
//        if (productUIModel != null) {
//            val factory = ProductDetailsViewModelFactory(productUIModel!!, requireActivity().application)
//            viewModel = ViewModelProvider(this, factory)[ProductDetailsViewModel::class.java]
//
//            setupRecyclerView()
//            setupAddReviewButton()
//            observeReviews()
//        } else {
//            // Handle the case where ProductUIModel is null
//            Toast.makeText(requireContext(), "Error: Product information is missing", Toast.LENGTH_SHORT).show()
//            parentFragmentManager.popBackStack()
//        }
//    }
//
//    private fun setupRecyclerView() {
//        reviewAdapter = ReviewAdapter()
//        binding.reviewsRecyclerView.apply {
//            adapter = reviewAdapter
//            layoutManager = LinearLayoutManager(requireContext())
//        }
//    }
//
//    private fun setupAddReviewButton() {
//        binding.writeReviewBtn.setOnClickListener {
//            // Navigate to AddReviewFragment with the ProductUIModel
//            if (productUIModel != null) {
//                val addReviewFragment = AddReviewFragment.newInstance(productUIModel!!)
//                parentFragmentManager.beginTransaction()
//                    .replace(android.R.id.content, addReviewFragment)
//                    .addToBackStack(null)
//                    .commit()
//            } else {
//                Toast.makeText(requireContext(), "Error: Product information is missing", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun observeReviews() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.reviews.collectLatest { reviews ->
//                Log.d(TAG, "Received ${reviews.size} reviews")
//
//                if (reviews.isEmpty()) {
//                    binding.noReviewsText.visibility = View.VISIBLE
//                    binding.reviewsRecyclerView.visibility = View.GONE
//                } else {
//                    binding.noReviewsText.visibility = View.GONE
//                    binding.reviewsRecyclerView.visibility = View.VISIBLE
//                    reviewAdapter.submitList(reviews)
//                }
//            }
//        }
//
//        // Fetch reviews when the fragment is created
//        viewModel.fetchReviews()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    companion object {
//        private const val TAG = "ReviewFragment"
//
//        fun newInstance(productUIModel: ProductUIModel): ReviewFragment {
//            val fragment = ReviewFragment()
//            val args = Bundle()
//            args.putParcelable("product_ui_model", productUIModel)
//            fragment.arguments = args
//            return fragment
//        }
//    }
//}
//




@AndroidEntryPoint
class ReviewFragment : BaseFragment<FragmentReviewBinding, ProductDetailsViewModel>() {

    override val viewModel: ProductDetailsViewModel by activityViewModels()
    override fun getLayoutResId(): Int = R.layout.fragment_review

    private lateinit var reviewAdapter: ReviewAdapter
    private var allReviews: List<ReviewUIModel> = emptyList() // This is initially empty


    override fun init() {
        Log.d("ReviewFragment", "init() called")
        setupRecyclerView()
        setupRatingFilters()

        // Set default title when entering the fragment
        binding.reviewTitleTv.text = "All Reviews"

        // Set "All Reviews" chip as checked and trigger animation after layout
        binding.chipAll.isChecked = true
        binding.chipAll.post {
            animateChip(binding.chipAll, 1.1f) // Apply your animation method
        }

        binding.writeReviewBtn.setOnClickListener {
            findNavController().navigate(R.id.action_reviewFragment_to_addReviewFragment)
        }



        observeReviews()
        viewModel.fetchReviews()
    }


    private fun setupRatingFilters() {
        with(binding.ratingChipGroup) {
            isSingleSelection = true
            binding.chipAll.isChecked = true

            setOnCheckedStateChangeListener { group, checkedIds ->
                // Animate all chips back to normal size
                for (i in 0 until childCount) {
                    animateChip(getChildAt(i), 1.0f)
                }

                // Get the selected chip and animate it
                val selectedChip = checkedIds.firstOrNull()?.let { group.findViewById<View>(it) }
                selectedChip?.let { animateChip(it, 1.2f) } // Scale up the selected chip

                val selectedRating = when (checkedIds.firstOrNull()) {
                    binding.chipAll.id -> null
                    binding.chip1Star.id -> 1
                    binding.chip2Stars.id -> 2
                    binding.chip3Stars.id -> 3
                    binding.chip4Stars.id -> 4
                    binding.chip5Stars.id -> 5
                    else -> null
                }

                // Update the app bar title based on the selected rating
                binding.reviewTitleTv.text = when (selectedRating) {
                    null -> "All Reviews"
                    else -> "$selectedRating Stars"
                }

                // Update the filtered review list
                updateReviewList(selectedRating)

                // Smooth scroll to top when filtering
                binding.reviewsRecyclerView.smoothScrollToPosition(0)
            }
        }
    }

    private fun animateChip(view: View, scale: Float) {
        view.animate()
            .scaleX(scale)
            .scaleY(scale)
            .setDuration(200) // Adjust duration for speed
            .start()
    }
    private fun updateReviewList(rating: Int?) {
        val filteredReviews = ReviewFilter.filterReviews(allReviews, rating)
        reviewAdapter.submitList(filteredReviews)
    }


    private fun setupRecyclerView() {
        reviewAdapter = ReviewAdapter()
        binding.reviewsRecyclerView.apply {
            adapter = reviewAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }




private fun observeReviews() {
    lifecycleScope.launch {
        viewModel.reviews.collectLatest { reviews ->
            if (reviews.isNotEmpty()) {
                Log.d("ReviewFragment", "Received ${reviews.size} reviews")
                allReviews = reviews

                // Initially display all reviews
                if (binding.ratingChipGroup.checkedChipId == binding.chipAll.id) {
                    updateReviewList(null) // Show all reviews initially
                }
            } else {
                Log.d("ReviewFragment", "No reviews received or list is empty")
                reviewAdapter.submitList(emptyList()) // Clear the adapter when no reviews
            }
        }
    }
}

}