package com.example.ui.home.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentHomeBinding
import com.example.ui.home.adapter.SalesAdAdapter
import com.example.ui.home.model.SalesAdUIModel
import com.example.utils.DepthPageTransformer


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: HomeFragment")


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

        val adapter = SalesAdAdapter(salesAds)
        binding.saleAdsViewPager.adapter = adapter
        binding.saleAdsViewPager.setPageTransformer(DepthPageTransformer())

    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}