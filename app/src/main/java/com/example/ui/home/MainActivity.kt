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
        initSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load initial fragment
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        val isLoggedIn = runBlocking { userViewModel.isUserLoggedIn().first() }
        if (!isLoggedIn) {
            goToAuthActivity()
            return
        }


        setupBottomNavigation()
        initViewModel()


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

    // the backstack part

//    private var currentFragment: Fragment? = null

//    private fun replaceFragment(newFragment: Fragment) {
//        val transaction = supportFragmentManager.beginTransaction()
//
//        // Hide the current fragment if exists
//        currentFragment?.let { transaction.hide(it) }
//
//        // Check if the fragment already exists in FragmentManager
//        val tag = newFragment::class.java.simpleName
//        var fragment = supportFragmentManager.findFragmentByTag(tag)
//
//        if (fragment == null) {
//            fragment = newFragment
//            transaction.add(R.id.fragment_container, fragment, tag)
//        } else {
//            transaction.show(fragment)
//        }
//
//        currentFragment = fragment
//        transaction.commit()
//    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
//    private fun replaceFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, fragment)
//            //.addToBackStack(null) // Uncomment if you want back navigation
//            .commit()
//    }
//
//    private fun intViews() {
//        initViewPager()            // Initialize ViewPager first
//        setupBottomNavigation()
//    }

//    private fun initBottomNavigationView() {
//        binding.bottomNavigationView.setOnItemSelectedListener {
//            when (it.itemId) {
//                R.id.homeFragment -> binding.homeViewPager.currentItem = 0
//                R.id.exploreFragment -> binding.homeViewPager.currentItem = 1
//                R.id.cartFragment -> binding.homeViewPager.currentItem = 2
//                R.id.offerFragment -> binding.homeViewPager.currentItem = 3
//                R.id.accountFragment -> binding.homeViewPager.currentItem = 4
//            }
//            true
//        }
//    }


//    private fun setupBottomNavigation() {
//        val menuItems = createMenuItems()
//
//        binding.bottomNavigationView.apply {
//            setMenuItems(menuItems, currentNavPosition)
//            setOnMenuItemClickListener { _, index ->
//                handleNavigationItemClick(index)
//            }
//        }
//    }
//
//    private fun handleNavigationItemClick(index: Int) {
//        currentNavPosition = index
//        binding.homeViewPager.currentItem = index
//    }

//    private fun createMenuItems(): Array<CbnMenuItem> {
//        return arrayOf(
//            // Using same drawable for both parameters to avoid AVD requirement
//            CbnMenuItem(R.drawable.ic_home, R.drawable.ic_home, R.id.homeFragment),
//            CbnMenuItem(R.drawable.ic_search, R.drawable.ic_search, R.id.exploreFragment),
//            CbnMenuItem(R.drawable.ic_cart, R.drawable.ic_cart, R.id.cartFragment),
//            CbnMenuItem(R.drawable.ic_offers, R.drawable.ic_offers, R.id.offerFragment),
//            CbnMenuItem(R.drawable.ic_account, R.drawable.ic_account, R.id.accountFragment)
//        )
//    }
//        private fun initViewPager() {
//        val fragments = listOf(
//            //make sure the order of fragments is the same as the order of the bottom navigation view items
//            HomeFragment(),
//            ExploreFragment(),
//            CartFragment(),
//            OffersFragment(),
//            AccountFragment()
//
//        )
//            binding.homeViewPager.apply {
//                offscreenPageLimit = fragments.size - 1
//                adapter = HomeViewPagerAdapter(this@MainActivity, fragments)
//                currentItem = currentNavPosition
//
//
//                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//                    override fun onPageSelected(position: Int) {
//                        super.onPageSelected(position)
//                        currentNavPosition = position
//                        binding.bottomNavigationView.onMenuItemClick(position)
//                    }
//                })
//            }
//        }
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putInt("NAV_POSITION", currentNavPosition)
//    }
//    // Handle configuration changes
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//    }


    private fun initViewModel() {
        lifecycleScope.launch {
            val userDetails = runBlocking { userViewModel.getUserDetails().first() }
            Log.d(TAG, "initViewModel: user details ${userDetails.email}")

            userViewModel.userDetailsState.collect {
                Log.d(TAG, "initViewModel: user details updated ${it?.email}")
            }

        }
    }


    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }



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

    private fun initSplashScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            installSplashScreen()
        } else {
            setTheme(R.style.Theme_ECommerce)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}