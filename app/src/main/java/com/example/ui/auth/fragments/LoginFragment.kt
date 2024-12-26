package com.example.ui.auth.fragments

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
import com.example.data.datasource.datastore.UserPreferencesDataSource
import com.example.data.models.Resource
import com.example.data.repository.auth.FirebaseAuthRepositoryImpl
import com.example.data.repository.user.UserDataStoreRepositoryImpl
import com.example.e_commerce.BuildConfig
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentLoginBinding
import com.example.ui.auth.viewmodel.LoginViewModel
import com.example.ui.auth.viewmodel.LoginViewModelFactory
import com.example.ui.common.views.ProgressDialog
import com.example.ui.showSnakeBarError
import com.example.utils.CrashlyticsUtils
import com.example.utils.LoginException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
//import com.google.firebase.auth.GoogleAuthProvider

import kotlinx.coroutines.launch


@Suppress("DEPRECATION")
class LoginFragment : Fragment() {

    private val progressDialog by lazy {
        ProgressDialog.createProgressDialog(requireActivity())
    }
//    // Create launcher for sign-in intent
//    private val signInLauncher = registerForActivityResult(
//        FirebaseAuthUIActivityResultContract()
//    ) { res ->
//        onSignInResult(res)
//    }

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(
            userPrefs = UserDataStoreRepositoryImpl(
                UserPreferencesDataSource(requireActivity())
            ),
            authRepository = FirebaseAuthRepositoryImpl()
        )
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
//private lateinit var firebaseAuth: FirebaseAuth

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
                    }
                    is Resource.Error -> {
                        progressDialog.dismiss()
                        val msg= resource.exception?.message?:getString(R.string.generic_err_msg)
                        Log.e(TAG, "Resource.Error: ${resource.exception?.message}")
                        view?.showSnakeBarError(resource.exception?.message?:getString(R.string.generic_err_msg))
                        logAuthIssueToCrashlytics(msg,"Login Error")
                    }
                }
            }
        }
    }

    private fun initListeners() {
        // Ensure the button ID matches the one in the layout file
        binding.googleSignInBtn.setOnClickListener {
            loginWithGoogleRequest()
//            if (isPlayServicesGood()) {
//
//            } else {
//                internetAlert()
//            }

        }
    }


//    private fun isNetworkAvailable(): Boolean {
//        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val network = connectivityManager.activeNetwork ?: return false
//            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
//            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//        } else {
//            @Suppress("DEPRECATION")
//            val networkInfo = connectivityManager.activeNetworkInfo
//            @Suppress("DEPRECATION")
//            return networkInfo?.isConnected == true
//        }
//    }
//
//    private fun showNoInternetDialog() {
//        AlertDialog.Builder(requireContext())
//            .setTitle("No Internet Connection")
//            .setMessage("Please check your internet connection and try again.")
//            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
//            .show()
//    }


    // solutions 1 for login with google

//    private fun startSignIn() {
//        // Configure Google Sign In
//        val providers = arrayListOf(
//            AuthUI.IdpConfig.GoogleBuilder().build()
//        )
//
//        // Create and launch sign-in intent
//        val signInIntent = AuthUI.getInstance()
//            .createSignInIntentBuilder()
//            .setAvailableProviders(providers)
//            .setIsSmartLockEnabled(false) // Disable Smart Lock for testing
//            .build()
//
//        signInLauncher.launch(signInIntent)
//    }
//
//    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
//        val response = result.idpResponse
//        if (result.resultCode == Activity.RESULT_OK) {
//            // Successfully signed in
//            val user = FirebaseAuth.getInstance().currentUser
//            Log.d(TAG, "Sign in successful: ${user?.displayName}")
//            Toast.makeText(
//                requireContext(),
//                "Welcome ${user?.displayName}",
//                Toast.LENGTH_SHORT
//            ).show()
//            // Navigate to your next screen or update UI
//        } else {
//            // Sign in failed
//            if (response == null) {
//                // User cancelled sign-in flow
//                Log.d(TAG, "Sign in cancelled")
//                Toast.makeText(
//                    requireContext(),
//                    "Sign in cancelled",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else {
//                // Handle error
//                Log.e(TAG, "Sign in error: ${response.error?.errorCode}")
//                Toast.makeText(
//                    requireContext(),
//                    "Sign in failed: ${response.error?.localizedMessage}",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//    }
//
//    // Optional: Sign out method
//    private fun signOut() {
//        AuthUI.getInstance()
//            .signOut(requireContext())
//            .addOnCompleteListener {
//                // Sign out completed
//                Toast.makeText(
//                    requireContext(),
//                    "Signed out successfully",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//    }


    // solutions 2 for login with google

//    private fun loginWithGoogleRequest() {
//        val credentialManager = CredentialManager.create(requireContext())
//        val googleIdOption = GetGoogleIdOption.Builder()
//            .setFilterByAuthorizedAccounts(false)
//            .setServerClientId(getString(R.string.server_client_id))
//            .build()
//
//        val request = GetCredentialRequest.Builder()
//            .addCredentialOption(googleIdOption)
//            .build()
//
//        lifecycleScope.launch {
//            try {
//                // Perform the credential request
//                val result = credentialManager.getCredential(
//                    request = request,
//                    context = requireContext()
//                )
//                handleCredential(result.credential)
//            } catch (e: GetCredentialException) {
//                Log.e(TAG, "Credential request failed: ${e.message}")
//            }
//        }
//    }
//
//    private fun handleCredential(credential: androidx.credentials.Credential) {
//        if (credential is com.google.android.libraries.identity.googleid.GoogleIdTokenCredential) {
//            val idToken = credential.idToken
//            firebaseAuthWithGoogle(idToken)
//        } else {
//            Log.e(TAG, "Unexpected credential type")
//            Toast.makeText(requireContext(), "Unexpected credential type", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun firebaseAuthWithGoogle(idToken: String) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        firebaseAuth.signInWithCredential(credential)
//            .addOnCompleteListener(requireActivity()) { task ->
//                if (task.isSuccessful) {
//                    val user = firebaseAuth.currentUser
//                    Toast.makeText(requireContext(), "Welcome ${user?.displayName}", Toast.LENGTH_SHORT).show()
//                } else {
//                    Log.e(TAG, "Authentication failed: ${task.exception}")
//                    Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }

    // solutions 3 for login with google

//    private fun configureFirebaseServices(){
//        if(BuildConfig.DEBUG){
//            Firebase.auth.useEmulator("10.0.2.2", 9000)
//            Firebase.firestore.useEmulator("10.0.2.2", 9000)
//        }
//    }


    // solution 4 for login with google

//    private fun loginWithGoogleRequest() {
//        val googleIdOption = GetGoogleIdOption.Builder()
//            .setFilterByAuthorizedAccounts(false)
//            .setServerClientId(getString(R.string.server_client_id)) // Make sure this is set correctly
//            .build()
//
//        val request = Builder()
//            .addCredentialOption(googleIdOption)
//            .build()
//
//        val credentialManager = CredentialManager.create(requireContext())
//
//        lifecycleScope.launch {
//            try {
//                val result = credentialManager.getCredential(requireContext(), request)
//                handleSignIn(result)
//            } catch (e: GetCredentialException) {
//                Log.e(TAG, "Credential request failed: ${e.message}")
//                Toast.makeText(requireContext(), "Failed to sign in: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//
//
//    private fun handleSignIn(result: GetCredentialResponse) {
//        val credential = result.credential
//        if (credential is GoogleIdTokenCredential) {
//            val idToken = credential.idToken
//            if (!idToken.isNullOrEmpty()) {
//                firebaseAuthWithGoogle(idToken)
//            } else {
//                Log.e(TAG, "Google ID Token is null or empty")
//            }
//        } else {
//            Log.e(TAG, "Unexpected credential type: ${credential::class.java.simpleName}")
//        }
//    }
//
//    private fun firebaseAuthWithGoogle(idToken: String) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        firebaseAuth.signInWithCredential(credential)
//            .addOnCompleteListener(requireActivity()) { task ->
//                if (task.isSuccessful) {
//                    val user = firebaseAuth.currentUser
//                    Toast.makeText(requireContext(), "Welcome ${user?.displayName}", Toast.LENGTH_SHORT).show()
//                } else {
//                    Log.e(TAG, "Authentication failed: ${task.exception}")
//                    Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }

    // solution 5 for login with google
//    private fun loginWithGoogleRequest() {
//        val googleIdOption = GetGoogleIdOption.Builder()
//            .setFilterByAuthorizedAccounts(false)
//            .setServerClientId("322843996565-r72jde9qij54q1fbj2frso8n99m3qmr7.apps.googleusercontent.com")
//            .build()
//
//        val request:GetCredentialRequest=Builder()
//            .addCredentialOption(googleIdOption)
//            .build()
//
//        val credentialManager = CredentialManager.create(requireContext())
//
//        lifecycleScope.launch {
//            try {
//                val result = credentialManager.getCredential(context=requireContext()
//                    ,request=request
//                )
//                handleSignIn(result)
//            } catch (e: GetCredentialException) {
//                Log.e(TAG, "Credential request failed: ${e.message}")
//                Toast.makeText(requireContext(), "Failed to sign in: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun handleSignIn(result: GetCredentialResponse) {
//        val credential = result.credential
//        if (credential is GoogleIdTokenCredential) {
//            val idToken = credential.idToken
//            if (idToken.isNotEmpty()) {
//                firebaseAuthWithGoogle(idToken)
//            } else {
//                Log.e(TAG, "Google ID Token is null or empty")
//            }
//        } else {
//            Log.e(TAG, "Unexpected credential type: ${credential::class.java.simpleName}")
//        }
//    }

//    private fun firebaseAuthWithGoogles(idToken: String) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        firebaseAuth.signInWithCredential(credential)
//            .addOnCompleteListener(requireActivity()) { task ->
//                if (task.isSuccessful) {
//                    val user = firebaseAuth.currentUser
//                    Toast.makeText(requireContext(), "Welcome ${user?.displayName}", Toast.LENGTH_SHORT).show()
//                } else {
//                    Log.e(TAG, "Authentication failed: ${task.exception}")
//                    Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }

//    private fun firebaseAuthWithGoogle(idToken: String) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        lifecycleScope.launch {
//            firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val user = firebaseAuth.currentUser
//                    updateUI(user)
//                } else {
//                    Log.w(TAG, "signInWithCredential:failure", task.exception)
//                    Toast.makeText(requireContext(), "Authentication Failed.", Toast.LENGTH_SHORT).show()
//                    updateUI(null)
//                }
//            }
//        }
//    }




//    private fun checkGooglePlayServices(context: Context): Boolean {
//        val googleApiAvailability = GoogleApiAvailability.getInstance()
//        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
//        return if (resultCode == ConnectionResult.SUCCESS) {
//            true
//        } else {
//            if (googleApiAvailability.isUserResolvableError(resultCode)) {
//                googleApiAvailability.getErrorDialog(context as Activity, resultCode, 2404)?.show()
//            } else {
//                Toast.makeText(context, "This device is not supported", Toast.LENGTH_LONG).show()
//            }
//            false
//        }
//    }
//


    // solution 6 for login with google and success one

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
//        startActivityForResult(signInIntent, RC_SIGN_IN)
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
           handleSignInResult(task)
        }else{
           view?.showSnakeBarError(getString(R.string.google_sign_in_field_msg))
        }
    }

private fun handleSignInResult(task: com.google.android.gms.tasks.Task<GoogleSignInAccount>) {
    try {
        val account = task.getResult(ApiException::class.java)
        firebaseAuthWithGoogle(account.idToken!!)

    } catch (e: Exception) {
        view?.showSnakeBarError(e.message?:getString(R.string.generic_err_msg))

        val msg= e.message?:getString(R.string.generic_err_msg)
        logAuthIssueToCrashlytics(msg,"Google")
    }
}

    private fun logAuthIssueToCrashlytics(msg: String,provider:String ) {
        CrashlyticsUtils.sendCustomLogToCrashlytics<LoginException>(
            msg,
            CrashlyticsUtils.LOGIN_KEY to msg,
            CrashlyticsUtils.LOGIN_PROVIDER to provider

        )
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
      loginViewModel.loginWithGoogle(idToken)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


// @Deprecated("Deprecated in Java")
// override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        Log.d(TAG, "onActivityResult: requestCode=$requestCode, resultCode=$resultCode")
//
//        if (requestCode == RC_SIGN_IN) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                val account = task.getResult(ApiException::class.java)
//                if (account != null) {
//                    // Authenticate with Firebase
//                    firebaseAuthWithGoogle(account.idToken!!)
//                }
//            } catch (e: ApiException) {
//                Log.e(TAG, "Google Sign-In failed: ${e.statusCode}")
//                Toast.makeText(requireContext(), "Sign-In failed", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }


//    private fun updateUI(user: String?) {
//        if (user != null) {
//            showToast("🎉 Welcome, ${user}!")
//        } else {
//            showToast("Sign-in is needed!")
//        }
//    }

//    private fun showToast(message: String) {
//        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
//    }


//    private fun isPlayServicesGood(): Boolean {
//        val availability = com.google.android.gms.common.GoogleApiAvailability.getInstance()
//        val resultCode = availability.isGooglePlayServicesAvailable(requireContext())
//        return if (resultCode == com.google.android.gms.common.ConnectionResult.SUCCESS) {
//            true
//        } else {
//            if (availability.isUserResolvableError(resultCode)) {
//                availability.getErrorDialog(
//                    requireActivity(),
//                    resultCode,
//                    2404
//                )?.show()
//            } else {
//                showToast("This device isn't supported!")
//            }
//            false
//        }
//    }

//    private fun internetAlert() {
//        AlertDialog.Builder(requireContext())
//            .setTitle("🚫 No Internet!")
//            .setMessage("Oops! Check your connection and try again.")
//            .setPositiveButton("Got it!") { dialog, _ -> dialog.dismiss() }
//            .show()
//    }




    companion object {
        private const val TAG = "LoginFragment"
//        private const val RC_SIGN_IN = 9001
    }
}