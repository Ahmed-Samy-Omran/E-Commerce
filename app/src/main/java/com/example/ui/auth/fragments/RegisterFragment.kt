package com.example.ui.auth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.data.models.Resource
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentLoginBinding
import com.example.e_commerce.databinding.FragmentRegisterBinding
import com.example.ui.auth.viewmodel.LoginViewModel
import com.example.ui.auth.viewmodel.LoginViewModelFactory
import com.example.ui.auth.viewmodel.RegisterViewModel
import com.example.ui.auth.viewmodel.RegisterViewModelFactory
import com.example.ui.common.views.ProgressDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch


class RegisterFragment : Fragment() {

    private val progressDialog by lazy {
        ProgressDialog.createProgressDialog(requireActivity())
    }

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val registerViewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(contextValue = requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = registerViewModel

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()

        initListeners()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            registerViewModel.registerState.collect {
                when (it) {
                    is Resource.Loading -> {
                        progressDialog.show()
                    }

                    is Resource.Success -> {
                        progressDialog.dismiss()
                        showLoginSuccessDialog()

                    }

                    is Resource.Error -> {
                        progressDialog.dismiss()
                    }
                }
            }
        }
    }

    private fun initListeners() {
        binding.signInTv.setOnClickListener {
            findNavController().popBackStack() // make me go to previous screen without making a new navigate that put screen above others
        }
    }

    private fun showLoginSuccessDialog() {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Login Success")
            .setMessage("We have sent you an email verification link. Please verify your email to login.")
            .setPositiveButton(
                "OK"
            )
            { dialog, which ->
                dialog?.dismiss()
                findNavController().popBackStack()
            }
            .create()
            .show()
    }

}