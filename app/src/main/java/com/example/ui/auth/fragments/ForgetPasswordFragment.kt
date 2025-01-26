package com.example.ui.auth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentForgetPasswordBinding
import com.example.e_commerce.databinding.FragmentLoginBinding
import com.example.ui.auth.viewmodel.ForgetPasswordViewModel
import com.example.ui.auth.viewmodel.ForgetPasswordViewModelFactory
import com.example.ui.auth.viewmodel.LoginViewModel
import com.example.ui.auth.viewmodel.LoginViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ForgetPasswordFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentForgetPasswordBinding? = null
    private val binding get() = _binding!!

    private val forgetPasswordViewModel: ForgetPasswordViewModel by viewModels {
        ForgetPasswordViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = forgetPasswordViewModel

        return binding.root


    }

    companion object {
        private const val TAG = "ForgetPasswordFragment"
     }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}