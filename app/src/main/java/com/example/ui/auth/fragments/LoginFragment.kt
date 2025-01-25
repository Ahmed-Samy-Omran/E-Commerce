package com.example.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.MainActivity
import com.example.data.models.Resource
import com.example.data.repository.auth.FirebaseAuthRepositoryImpl
import com.example.e_commerce.BuildConfig
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentLoginBinding
import com.example.ui.auth.viewmodel.LoginViewModel
import com.example.ui.auth.viewmodel.LoginViewModelFactory
import com.example.ui.common.views.ProgressDialog
import com.example.ui.showSnakeBarError
import com.example.utils.CrashlyticsUtils
import com.example.utils.LoginException
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


@Suppress("DEPRECATION")
class LoginFragment : Fragment() {


    // for facebook login
    private lateinit var callbackManager: CallbackManager
    private lateinit var loginManager: LoginManager


    private val progressDialog by lazy {
        ProgressDialog.createProgressDialog(requireActivity())
    }


    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(contextValue = requireContext())
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = loginViewModel

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callbackManager = CallbackManager.Factory.create()
        loginManager = LoginManager.getInstance()
        initListeners()
        initViewModel()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            loginViewModel.loginState.collect { resource ->
                when (resource) {
                    is Resource.Loading -> progressDialog.show()
                    is Resource.Success -> {
                        progressDialog.dismiss()
                        goToHome()

                    }

                    is Resource.Error -> {
                        progressDialog.dismiss()
                        val msg = resource.exception?.message ?: getString(R.string.generic_err_msg)
                        view?.showSnakeBarError(msg)
                    }
                }
            }
        }
    }


    private fun goToHome() {
        requireActivity().startActivity(Intent(activity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        requireActivity().finish()
    }

    private fun initListeners() {
        // Ensure the button ID matches the one in the layout file
        binding.googleSignInBtn.setOnClickListener {
            loginWithGoogleRequest()
        }

        binding.facebookSignInBtn.setOnClickListener {
            loginWithFacebook()
        }
        binding.registerTv.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }



    }


    private fun loginWithGoogleRequest() {
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
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        } else {
            view?.showSnakeBarError(getString(R.string.google_sign_in_field_msg))
        }
    }

    private fun handleSignInResult(task: com.google.android.gms.tasks.Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)

        } catch (e: Exception) {
            view?.showSnakeBarError(e.message ?: getString(R.string.generic_err_msg))

            val msg = e.message ?: getString(R.string.generic_err_msg)
            logAuthIssueToCrashlytics(msg, "Google")
        }
    }

    private fun logAuthIssueToCrashlytics(msg: String, provider: String) {
        CrashlyticsUtils.sendCustomLogToCrashlytics<LoginException>(
            msg,
            CrashlyticsUtils.LOGIN_KEY to msg,
            CrashlyticsUtils.LOGIN_PROVIDER to provider

        )
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        loginViewModel.loginWithGoogle(idToken)
    }


    // for facebook

    private fun signOut() {
        loginManager.logOut()
        Log.d(TAG, "signOut: ")
    }

    private fun isLoggedIn(): Boolean {
        val accessToken = AccessToken.getCurrentAccessToken()
        return accessToken != null && !accessToken.isExpired
    }

    private fun loginWithFacebook() {
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

    private fun firebaseAuthWithFacebook(token: String) {
        loginViewModel.loginWithFacebook(token)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        private const val TAG = "LoginFragment"
//        private const val RC_SIGN_IN = 9001
    }
}