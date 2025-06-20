    package com.example.ui.reviews.fragment

    import android.content.Intent
    import android.net.Uri
    import android.os.Bundle
    import android.util.Log
    import androidx.fragment.app.Fragment
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ImageButton
    import android.widget.ImageView
    import android.widget.Toast
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.appcompat.app.AppCompatDelegate
    import androidx.databinding.DataBindingUtil.setContentView
    import androidx.fragment.app.activityViewModels
    import androidx.fragment.app.viewModels
    import androidx.lifecycle.ViewModelProvider
    import androidx.lifecycle.lifecycleScope
    import androidx.navigation.fragment.findNavController
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.example.data.models.Resource
    import com.example.data.models.reviews.ReviewModel
    import com.example.e_commerce.R
    import com.example.e_commerce.databinding.FragmentAddReviewBinding
    import com.example.ui.common.views.ProgressDialog
    import com.example.ui.products.model.ProductUIModel
    import com.example.ui.products.viewmodel.ProductDetailsViewModel
    import com.example.ui.reviews.adapter.ReviewImageAdapter
    import com.example.ui.reviews.model.ReviewUIModel
    import com.example.ui.showSnakeBarError
    import com.example.utils.PlaneFlyAnimator
    import com.example.utils.formatTimestamp
    import com.google.firebase.Timestamp
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.storage.FirebaseStorage
    import dagger.hilt.android.AndroidEntryPoint
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.collectLatest
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.tasks.await
    import java.util.UUID

    @AndroidEntryPoint
    class AddReviewFragment : Fragment() {

        private var _binding: FragmentAddReviewBinding? = null
        private val binding get() = _binding!!

        // Shared ViewModel between fragments
        private val viewModel: ProductDetailsViewModel by activityViewModels()

        private var currentRating = 0f
        private lateinit var productId: String

        // To hold the selected images from gallery
        private val selectedImages = mutableListOf<Uri>()

        // Adapter for the image list with add button
        private lateinit var reviewImageAdapter: ReviewImageAdapter

        private val pickImagesLauncher =
            registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
                val spaceLeft = 4 - selectedImages.size

                if (spaceLeft <= 0) {
                    Toast.makeText(requireContext(), "Maximum 4 images allowed", Toast.LENGTH_SHORT)
                        .show()
                    return@registerForActivityResult
                }

                val imagesToAdd = uris.take(spaceLeft)
                selectedImages.addAll(imagesToAdd)
                reviewImageAdapter.notifyDataSetChanged()

                if (selectedImages.size >= 4) {
                    Toast.makeText(requireContext(), "Maximum 4 images allowed", Toast.LENGTH_SHORT)
                        .show()
                }
            }

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

            viewModel.checkIfUserReviewed()
            setupStarClicks()
            setupSubmitButton()
            observeReviewResult()
            observeIfUserAlreadyReviewed()

    // Setup RecyclerView for image previews

            reviewImageAdapter = ReviewImageAdapter(selectedImages) {
                pickImageFromGallery()
            }
            binding.reviewImageRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.reviewImageRecycler.adapter = reviewImageAdapter
        }

        private fun pickImageFromGallery() {
            if (selectedImages.size >= 4) {
                Toast.makeText(requireContext(), "Maximum 4 images allowed", Toast.LENGTH_SHORT).show()
                return
            }
            pickImagesLauncher.launch("image/*")
        }

        private fun observeIfUserAlreadyReviewed() {
            viewModel.existingUserReview.observe(viewLifecycleOwner) { review ->
                if (review != null) {
                    // Disable review form
                    binding.reviewEt.isEnabled = false
                    binding.submitReviewButton.isEnabled = false
                    binding.ratingText.text = "You've already reviewed this product"

                    val stars = listOf(
                        binding.star1, binding.star2, binding.star3, binding.star4, binding.star5
                    )
                    stars.forEach { it.isEnabled = false }

                    Toast.makeText(
                        requireContext(),
                        "You’ve already reviewed this product.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
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

                // 🔸 Disable the button and show the loading overlay
                binding.submitReviewButton.isEnabled = false
                binding.loadingOverlay.visibility = View.VISIBLE

                lifecycleScope.launch {
                    viewModel.setSelectedReviewImages(selectedImages)
                    viewModel.addReview(productId, reviewText, currentRating.toInt())
                }
            }
        }


        private suspend fun uploadReviewImagesToFirebase(imageUris: List<Uri>): List<String> {
            val downloadUrls = mutableListOf<String>()
            val storage = FirebaseStorage.getInstance()

            for (uri in imageUris) {
                try {
                    val fileName = UUID.randomUUID().toString()
                    val ref = storage.reference.child("review/$fileName.jpg")

                    ref.putFile(uri).await()
                    val url = ref.downloadUrl.await().toString()
                    downloadUrls.add(url)
                } catch (e: Exception) {
                    Log.e("UploadImage", "Failed to upload image: ${e.message}")
                }
            }

            return downloadUrls
        }



        private fun observeReviewResult() {
            viewModel.addReviewResult.observe(viewLifecycleOwner) { success ->
                if (success) {
                    binding.reviewEt.text.clear()
                    setStarRating(0f)
                    currentRating = 0f
                    binding.submitReviewButton.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    clearSelectedReviewImages()

                    PlaneFlyAnimator.animate(binding.submitReviewButton) {
                        findNavController().navigate(R.id.action_addReviewFragment_to_reviewFragment)
                        viewModel.resetAddReviewResult()
                    }

                    Toast.makeText(requireContext(), "Review added!", Toast.LENGTH_SHORT).show()
                }
            }

            viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
                if (!errorMsg.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Error: $errorMsg", Toast.LENGTH_SHORT).show()
                    binding.submitReviewButton.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        private fun clearSelectedReviewImages() {
            selectedImages.clear()
            reviewImageAdapter.notifyDataSetChanged()
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }

