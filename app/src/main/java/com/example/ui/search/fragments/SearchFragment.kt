package com.example.ui.search.fragments


import android.annotation.SuppressLint
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.data.models.Resource
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentSearchBinding
import com.example.ui.common.fragments.BaseFragment
import com.example.ui.products.ProductDetailsActivity
import com.example.ui.products.ProductDetailsActivity.Companion.PRODUCT_UI_MODEL_EXTRA
import com.example.ui.products.adapter.ProductAdapter
import com.example.ui.products.adapter.ProductViewType
import com.example.ui.products.model.ProductUIModel
import com.example.ui.reviews.adapter.ReviewImageAdapter
import com.example.ui.search.viewmodel.SearchViewModel
import com.example.ui.showSnakeBarError
import com.example.utils.GridSpacingItemDecoration
import com.example.utils.HorizontalSpaceItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>() {

    private val searchAdapter by lazy {
        ProductAdapter(
            viewType = ProductViewType.LIST
        ) {
            goToProductDetails(it)
        }

    }


    override val viewModel: SearchViewModel by viewModels()
    override fun getLayoutResId(): Int = R.layout.fragment_search
    override fun init() {
        setupRecyclerView()
        observeSearchState()
        setupSearchListener()
        openFilterBottomSheet()



//        setupBackToHomeButton()
    }

    private fun openFilterBottomSheet() {
       binding.filterBtn.setOnClickListener{
           val filterBottomSheet = FilterSortBottomSheet{options->
               viewModel.applyFiltersAndSort(options)
           }
           // If you're inside an Activity, use supportFragmentManager that used for make bottomSheet dialog appear `
           filterBottomSheet.show(parentFragmentManager, "filter")
       }
    }


//    private fun setupBackToHomeButton() {
//        binding.bacKHomeBtn.setOnClickListener {
//         findNavController().navigate(R.id.navigation_home)
//        }
//    }


    @SuppressLint("SetTextI18n")
    private fun observeSearchState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchState.collect { state ->
                    when (state) {
                        is Resource.Loading -> {
                            binding.progressBar.isVisible = true
                            binding.searchRecyclerView.isVisible = false

                        }

                        is Resource.Success -> {
                            binding.progressBar.isVisible = false
                            val list = state.data ?: emptyList()
                            binding.numOfResult.text = "${list.size} Results"
                            if (list.isEmpty()) {
                                binding.searchRecyclerView.isVisible = false
                                binding.emptyLayout.isVisible = true
                            } else {
                                binding.searchRecyclerView.isVisible = true
                                binding.emptyLayout.isVisible = false
                                searchAdapter.submitList(list)
                            }


                        }

                        is Resource.Error -> {
                            binding.progressBar.isVisible = false
                            requireView().showSnakeBarError(
                                state.exception?.message ?: "Something went wrong"
                            )
                        }
                    }

                }
            }

        }
    }

    private fun setupRecyclerView() {

        binding.searchRecyclerView.apply {
            adapter = searchAdapter
            layoutManager = GridLayoutManager(
                requireContext(), 2
            )
            addItemDecoration(GridSpacingItemDecoration(2, 16, true))
        }

    }

    private fun goToProductDetails(product: ProductUIModel) {
        context?.let {
            val intent = Intent(it, ProductDetailsActivity::class.java)
            intent.putExtra(PRODUCT_UI_MODEL_EXTRA, product)
            it.startActivity(intent)
        }
    }

    @OptIn(FlowPreview::class)
    private fun setupSearchListener() {
        //  نستخدم callbackFlow لإنشاء flow يتعامل مع تغييرات النص في EditText
        // every time the text changes, we will send the new text to the flow
        val searchFlow = callbackFlow {
            val watcher = object : TextWatcher {
                // use textWatcher to listen to text changes in the edit text and onTextChanged to know every time the user types something
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    trySend(s.toString()) // نبعت النص الجديد
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {}
            }

            binding.searchEditText.addTextChangedListener(watcher)

            awaitClose {
                // when the flow is closed, we remove the text watcher to avoid memory leaks
                binding.searchEditText.removeTextChangedListener(watcher)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        searchFlow
                            .debounce(300) // ✅ استنى    500 ملي ثانية بعد آخر كتابة
                            .distinctUntilChanged() // ✅ ما تبعتش نفس الكلمة مرتين
                            .collectLatest { query -> // when the user types something, we will collect the latest value and cancel the previous one
                                if (query.isNotBlank()) { // ✅ لو الكلمة مش فاضيةsend the query to the viewModel
                                    viewModel.searchProducts(query)
                                }
//                        else {
//                            searchAdapter.submitList(emptyList())
//                            binding.searchRecyclerView.isVisible = false
//                            binding.emptyLayout.isVisible = true
//                        }
                            }
            }

        }
    }

}

