package com.example.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.e_commerce.databinding.DialogFullscreenImageBinding

class FullscreenImageDialog(context: Context, private val imageUrl: String) : Dialog(context) {

    private lateinit var binding: DialogFullscreenImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request to remove the title bar from the dialog
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        // Initialize binding
        binding = DialogFullscreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Make the dialog full-screen and transparent
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Enable dismiss on clicking outside the image
        setCanceledOnTouchOutside(true)

        // Load the image using Glide
        Glide.with(context)
            .load(imageUrl)
            .into(binding.fullscreenImageView)

        // Close the dialog when clicking the image
        binding.fullscreenImageView.setOnClickListener { dismiss() }
    }
}