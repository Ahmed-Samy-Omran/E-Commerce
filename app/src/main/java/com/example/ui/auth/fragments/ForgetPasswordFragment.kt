package com.example.ui.auth.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.data.models.Resource
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentForgetPasswordBinding
import com.example.e_commerce.databinding.FragmentLoginBinding
import com.example.ui.auth.fragments.LoginFragment.Companion
import com.example.ui.auth.showSnakeBarError
import com.example.ui.auth.viewmodel.ForgetPasswordViewModel
import com.example.ui.auth.viewmodel.LoginViewModel
import com.example.ui.common.views.ProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgetPasswordFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentForgetPasswordBinding? = null
    private val binding get() = _binding!!

    private val progressDialog by lazy {
        ProgressDialog.createProgressDialog(requireActivity())
    }

    private val forgetPasswordViewModel: ForgetPasswordViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = forgetPasswordViewModel

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            forgetPasswordViewModel.forgotPasswordState.collect {resource->
                when (resource) {
                    is Resource.Loading -> {
                        progressDialog.show()
                    }
                    is Resource.Success -> {
                        progressDialog.dismiss()
                        showSendEmailSuccessDialog()

                    }
                    is Resource.Error -> {
                        progressDialog.dismiss()
                        val msg = resource.exception?.message ?: getString(R.string.generic_err_msg)
                        Log.d(TAG, "initViewModel: $msg")
                        view?.showSnakeBarError(msg)
                    }

                }
            }
        }
    }

    private fun showSendEmailSuccessDialog() {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Reset Password")
            .setMessage("Check your email to reset your password")
            .setPositiveButton(
                "OK"
            )
            { dialog, which ->
                dialog?.dismiss()
             this@ForgetPasswordFragment.dismiss()
            }
            .create()
            .show()
    }


    companion object {
        private const val TAG = "ForgetPasswordFragment"
     }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}