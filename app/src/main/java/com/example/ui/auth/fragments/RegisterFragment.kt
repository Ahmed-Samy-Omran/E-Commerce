package com.example.ui.auth.fragments

import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.data.models.Resource
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentRegisterBinding
import com.example.ui.auth.viewmodel.RegisterViewModel

import com.example.ui.getGoogleRequestIntent
import com.example.ui.showSnakeBarError

import com.example.ui.common.fragments.BaseFragment
import com.example.utils.CrashlyticsUtils
import com.example.utils.RegisterException
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding, RegisterViewModel>() {

    // for facebook login
    private val callbackManager: CallbackManager by lazy {
        CallbackManager.Factory.create()
    }
    private val loginManager: LoginManager by lazy {
        LoginManager.getInstance()
    }



    override val viewModel: RegisterViewModel by  viewModels ()


    override fun getLayoutResId(): Int = R.layout.fragment_register


    override fun init() {
        initViewModel()

        initListeners()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            viewModel.registerState.collect {resource->
                when (resource) {
                    is Resource.Loading -> {
                        progressDialog.show()
                    }

                    is Resource.Success -> {
                        progressDialog.dismiss()
                        showLoginSuccessDialog()

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

    private fun initListeners() {
        binding.signInTv.setOnClickListener {
            findNavController().popBackStack() // make me go to previous screen without making a new navigate that put screen above others
        }

        binding.googleSignupBtn.setOnClickListener {
            registerWithGoogleRequest()
        }

        binding.facebookSignUpBtn.setOnClickListener {
            registerWithFacebook()
        }
    }

    private fun registerWithGoogleRequest() {
        // Configure Google Sign-In
        val signInIntent = getGoogleRequestIntent(requireActivity())
        launcher.launch(signInIntent)
    }




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
        viewModel.signUpWithGoogle(idToken)
    }


    private fun logAuthIssueToCrashlytics(msg: String, provider: String) {
        CrashlyticsUtils.sendCustomLogToCrashlytics<RegisterException>(
            msg,
            CrashlyticsUtils.LOGIN_KEY to msg,
            CrashlyticsUtils.LOGIN_PROVIDER to provider

        )
    }


    private fun isLoggedIn(): Boolean {
        val accessToken = AccessToken.getCurrentAccessToken()
        return accessToken != null && !accessToken.isExpired
    }

    private fun registerWithFacebook() {
        if (isLoggedIn()) signOut()
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val token = result.accessToken.token
                Log.d(TAG, "onSuccess: $token")
                firebaseAuthWithFacebook(token)
            }

            override fun onCancel() {
                // Handle login cancel
            }

            override fun onError(error: FacebookException) {
                // Handle login error
                val msg = error.message ?: getString(R.string.generic_err_msg)
                Log.d(TAG, "onError: $msg")
                view?.showSnakeBarError(msg)
                logAuthIssueToCrashlytics(msg, "Facebook")
            }
        })
        loginManager.logInWithReadPermissions(
            this,
            listOf("email", "public_profile")
        )
    }

    private fun signOut() {
        loginManager.logOut()

    }

    private fun firebaseAuthWithFacebook(token: String) {
        viewModel.registerWithFacebook(token)
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



    companion object {
        private const val TAG = "RegisterFragment"
    }

}