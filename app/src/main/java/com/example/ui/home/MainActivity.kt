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
import androidx.lifecycle.lifecycleScope
import com.example.e_commerce.R
import com.example.e_commerce.databinding.ActivityMainBinding
import com.example.ui.account.fragments.AccountFragment
import com.example.ui.common.viewmodel.UserViewModel
import com.example.ui.auth.AuthActivity
import com.example.ui.cart.fragments.CartFragment
import com.example.ui.home.adapter.HomeViewPagerAdapter
import com.example.ui.explore.fragments.ExploreFragment
import com.example.ui.home.fragments.HomeFragment
import com.example.ui.offers.fragments.OffersFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels()

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        initSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val isLoggedIn = runBlocking { userViewModel.isUserLoggedIn().first() }
        if (!isLoggedIn) {
            goToAuthActivity()
            return
        }

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        initViewModel()
        intViews()


    }

    private fun intViews() {
        initViewPager()
        initBottomNavigationView()
    }

    private fun initBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> binding.homeViewPager.currentItem = 0
                R.id.exploreFragment -> binding.homeViewPager.currentItem = 1
                R.id.cartFragment -> binding.homeViewPager.currentItem = 2
                R.id.offerFragment -> binding.homeViewPager.currentItem = 3
                R.id.accountFragment -> binding.homeViewPager.currentItem = 4
            }
            true
        }
    }
    private fun initViewPager() {
        val fragments = listOf(
            //make sure the order of fragments is the same as the order of the bottom navigation view items
            HomeFragment(),
            ExploreFragment(),
            CartFragment(),
            OffersFragment(),
            AccountFragment()

        )
        binding.homeViewPager.offscreenPageLimit = fragments.size // that make all screen created and loaded at once and not necessary to open it to load
        binding.homeViewPager.adapter = HomeViewPagerAdapter(this, fragments)
      // that make when i swipe the viewpager the bottom navigation view change the selected item
        binding.homeViewPager.registerOnPageChangeCallback(
            object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.bottomNavigationView.menu.getItem(position).isChecked = true
                }
            }
        )
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