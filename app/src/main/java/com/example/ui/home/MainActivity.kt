    package com.example.ui.home

    import android.app.ActivityOptions
    import android.content.Intent
    import android.os.Build
    import android.os.Bundle
    import android.util.Log
    import androidx.activity.enableEdgeToEdge
    import androidx.activity.viewModels
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
    import androidx.core.view.ViewCompat
    import androidx.core.view.WindowInsetsCompat
    import androidx.fragment.app.Fragment
    import androidx.lifecycle.lifecycleScope
    import androidx.navigation.NavController
    import androidx.navigation.fragment.NavHostFragment
    import com.example.e_commerce.R
    import com.example.e_commerce.databinding.ActivityMainBinding
    import com.example.ui.account.fragments.AccountFragment
    import com.example.ui.common.viewmodel.UserViewModel
    import com.example.ui.auth.AuthActivity
    import com.example.ui.cart.fragments.CartFragment
    import com.example.ui.explore.fragments.ExploreFragment
    import com.example.ui.home.fragments.HomeFragment
    import com.example.ui.offers.fragments.OffersFragment
    import dagger.hilt.android.AndroidEntryPoint
    import kotlinx.coroutines.flow.first
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.runBlocking
    import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem

    @AndroidEntryPoint
    class MainActivity : AppCompatActivity() {


        private var _binding: ActivityMainBinding? = null
        private val binding get() = _binding!!
        private val userViewModel: UserViewModel by viewModels()



        override fun onCreate(savedInstanceState: Bundle?) {
            // Changed: Call initSplashScreen before super.onCreate to ensure splash screen is set up correctly
            initSplashScreen()
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()

            _binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Changed: Moved login check to lifecycleScope to avoid blocking UI thread
            lifecycleScope.launch {
                val isLoggedIn = userViewModel.isUserLoggedIn().first()
                if (!isLoggedIn) {
                    goToAuthActivity()
                    return@launch
                }

                // Load initial fragment
                if (savedInstanceState == null) {
                    replaceFragment(HomeFragment())
                }

                setupBottomNavigation()
                initViewModel()
            }
        }
        private fun setupBottomNavigation() {

            // Setup bottom navigation
            val menuItems = arrayOf(
                CbnMenuItem(
                    R.drawable.ic_home,
                    R.drawable.avd_home, // Using same drawable to avoid AVD requirement
                    R.id.navigation_home
                ),
                CbnMenuItem(
                    R.drawable.ic_search,
                    R.drawable.avd_search,
                    R.id.navigation_explore
                ),
                CbnMenuItem(
                    R.drawable.ic_cart,
                    R.drawable.avd_cart,
                    R.id.navigation_cart
                ),
                CbnMenuItem(
                    R.drawable.ic_offers,
                    R.drawable.avd_offer,
                    R.id.navigation_offer
                ),
                CbnMenuItem(
                    R.drawable.ic_account,
                    R.drawable.avd_account,
                    R.id.navigation_account
                )
            )

            binding.navView.setMenuItems(menuItems, 0)

            // Handle navigation clicks
            binding.navView.setOnMenuItemClickListener { _, index ->
                when (index) {
                    0 -> replaceFragment(HomeFragment())
                    1 -> replaceFragment(ExploreFragment())
                    2 -> replaceFragment(CartFragment())
                    3 -> replaceFragment(OffersFragment())
                    4 -> replaceFragment(AccountFragment())
                }
            }
        }


        private fun replaceFragment(fragment: Fragment) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }


        private fun initViewModel() {
            lifecycleScope.launch {
                val userDetails = runBlocking { userViewModel.getUserDetails().first() }
                Log.d(TAG, "initViewModel: user details ${userDetails.email}")

                userViewModel.userDetailsState.collect {
                    Log.d(TAG, "initViewModel: user details updated ${it?.email}")
                }

            }
        }

        private fun initSplashScreen() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Changed: Only show splash screen for launcher intents to prevent reappearance after login
                if (intent.action == Intent.ACTION_MAIN && intent.hasCategory(Intent.CATEGORY_LAUNCHER)) {
                    installSplashScreen()
                    // Optional: Control splash screen duration if needed
                    // splashScreen.setKeepOnScreenCondition { false }
                }
            } else {
                setTheme(R.style.Theme_ECommerce)
            }
        }

        override fun onResume() {
            super.onResume()
            Log.d(TAG, "onResume: ")
        }

        // ✅ Redirects unauthenticated users

        private fun goToAuthActivity() {
            val intent = Intent(this, AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val options = ActivityOptions.makeCustomAnimation(
                this, android.R.anim.fade_in, android.R.anim.fade_out
            )
            startActivity(intent, options.toBundle())
            finish()
        }

        override fun onDestroy() {
            super.onDestroy()
            _binding = null
        }

        companion object {
            private const val TAG = "MainActivity"
        }

    }