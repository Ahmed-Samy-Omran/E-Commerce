    package com.example.ui.reviews.fragment

    import android.os.Bundle
    import android.util.Log
    import androidx.fragment.app.Fragment
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ImageButton
    import android.widget.ImageView
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatDelegate
    import androidx.databinding.DataBindingUtil.setContentView
    import androidx.fragment.app.activityViewModels
    import androidx.fragment.app.viewModels
    import androidx.lifecycle.ViewModelProvider
    import androidx.lifecycle.lifecycleScope
    import androidx.navigation.fragment.findNavController
    import com.example.data.models.Resource
    import com.example.data.models.reviews.ReviewModel
    import com.example.e_commerce.R
    import com.example.e_commerce.databinding.FragmentAddReviewBinding
    import com.example.ui.common.views.ProgressDialog
    import com.example.ui.products.model.ProductUIModel
    import com.example.ui.products.viewmodel.ProductDetailsViewModel
    import com.example.ui.reviews.model.ReviewUIModel
    import com.example.utils.formatTimestamp
    import com.google.firebase.Timestamp
    import com.google.firebase.auth.FirebaseAuth
    import dagger.hilt.android.AndroidEntryPoint
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.collectLatest
    import kotlinx.coroutines.launch

    @AndroidEntryPoint
    class AddReviewFragment : Fragment() {

        private var _binding: FragmentAddReviewBinding? = null
        private val binding get() = _binding!!

        // Shared ViewModel between fragments
        private val viewModel: ProductDetailsViewModel by activityViewModels()

        private var currentRating = 0f
        private lateinit var productId: String

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = FragmentAddReviewBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            // Get productId from ViewModel or arguments
            productId = viewModel.productId
            setupStarClicks()
            setupSubmitButton()
            observeReviewResult()
        }

        // Setup star rating selection
        private fun setupStarClicks() {
            val stars = listOf(
                binding.star1,
                binding.star2,
                binding.star3,
                binding.star4,
                binding.star5
            )

            stars.forEachIndexed { index, star ->
                star.setOnClickListener {
                    currentRating = (index + 1).toFloat()
                    setStarRating(currentRating)
                }
            }
        }

        private fun setStarRating(rating: Float) {
            val stars = listOf(
                binding.star1,
                binding.star2,
                binding.star3,
                binding.star4,
                binding.star5
            )

            for (i in stars.indices) {
                stars[i].setImageResource(
                    if (rating >= i + 1) R.drawable.star_full else R.drawable.star_empty
                )
            }

            // Optional: Show text like "4/5"
            binding.ratingText.text = "${rating.toInt()}/5"
        }

        // Handle button click to submit review
        private fun setupSubmitButton() {
            binding.submitReviewButton.setOnClickListener {
                val reviewText = binding.reviewEt.text.toString().trim()

                if (currentRating == 0f) {
                    Toast.makeText(requireContext(), "Please select a rating", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (reviewText.isEmpty()) {
                    Toast.makeText(requireContext(), "Please write a review", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // --- Start: Debugging User Info ---
                val currentUser = FirebaseAuth.getInstance().currentUser
                Log.d("AddReviewFragment", "Current User ID: ${currentUser?.uid}")
                Log.d("AddReviewFragment", "Current User Display Name: ${currentUser?.displayName}")
                Log.d("AddReviewFragment", "Current User Photo URL: ${currentUser?.photoUrl}")
                // --- End: Debugging User Info ---

                // Disable submit button here if needed to prevent double clicks
                binding.submitReviewButton.isEnabled = false

                // Use the new method in ViewModel that handles ReviewUIModel creation
                viewModel.addReview(productId, reviewText, currentRating.toInt())
            }
        }

        // Observe result of adding review
        private fun observeReviewResult() {
            viewModel.addReviewResult.observe(viewLifecycleOwner) { success ->
                if (success) {
                    Toast.makeText(requireContext(), "Review added!", Toast.LENGTH_SHORT).show()
                    // Clear review text & reset rating if staying on the same fragment
                    binding.reviewEt.text.clear()
                    setStarRating(0f)
                    currentRating = 0f

                    // Enable submit button again
                    binding.submitReviewButton.isEnabled = true

                    // Or navigate back if needed
                    parentFragmentManager.popBackStack()
                }
            }

            viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
                if (!errorMsg.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Error: $errorMsg", Toast.LENGTH_SHORT).show()
                    // Re-enable submit button on error
                    binding.submitReviewButton.isEnabled = true
                }
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }

