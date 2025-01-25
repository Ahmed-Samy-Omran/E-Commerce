package com.example.ui.auth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.data.models.Resource
import com.example.e_commerce.BuildConfig
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentLoginBinding
import com.example.e_commerce.databinding.FragmentRegisterBinding
import com.example.ui.auth.viewmodel.LoginViewModel
import com.example.ui.auth.viewmodel.LoginViewModelFactory
import com.example.ui.auth.viewmodel.RegisterViewModel
import com.example.ui.auth.viewmodel.RegisterViewModelFactory
import com.example.ui.common.views.ProgressDialog
import com.example.ui.showSnakeBarError
import com.example.utils.CrashlyticsUtils
import com.example.utils.LoginException
import com.example.utils.RegisterException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")

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

        binding.googleSignupBtn.setOnClickListener {
            registerWithGoogleRequest()
        }

        binding.facebookSignUpBtn.setOnClickListener {
//            loginWithFacebook()
        }
    }

    private fun registerWithGoogleRequest() {
        // Configure Google Sign-In
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.clientServerId) // Replace with your client ID
            .requestEmail()
            .requestProfile()
            .requestServerAuthCode(BuildConfig.clientServerId)
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions)

        // Show the account picker menu
        googleSignInClient.signOut() // Ensures the picker menu is shown, not direct login
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)    }


    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignUpResult(task)
        } else {
            view?.showSnakeBarError(getString(R.string.google_sign_in_field_msg))
        }
    }

    private fun handleSignUpResult(task: com.google.android.gms.tasks.Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)

        } catch (e: Exception) {
            view?.showSnakeBarError(e.message ?: getString(R.string.generic_err_msg))

            val msg = e.message ?: getString(R.string.generic_err_msg)
            logAuthIssueToCrashlytics(msg, "Google")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        registerViewModel.signUpWithGoogle(idToken)
    }


    private fun logAuthIssueToCrashlytics(msg: String, provider: String) {
        CrashlyticsUtils.sendCustomLogToCrashlytics<RegisterException>(
            msg,
            CrashlyticsUtils.LOGIN_KEY to msg,
            CrashlyticsUtils.LOGIN_PROVIDER to provider

        )
    }


    private fun showLoginSuccessDialog() {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Register Success")
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