package com.example

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
import com.example.data.datasource.datastore.UserPreferencesDataSource
import com.example.data.repository.user.UserDataStoreRepositoryImpl
import com.example.e_commerce.R
import com.example.ui.common.viewmodel.UserViewModel
import com.example.ui.common.viewmodel.UserViewModelFactory
import com.example.ui.login.AuthActivity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    val userViewModel: UserViewModel by viewModels() {
        UserViewModelFactory(UserDataStoreRepositoryImpl(UserPreferencesDataSource(this)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        lifecycleScope.launch(Main) { // here we work on main thread and  stop every thing to see if user logged in or not
            val isLoggedIn = userViewModel.isUserLoggedIn().first()
//            Log.d(TAG,"")
            if (isLoggedIn) {
                setContentView(R.layout.activity_main)
            } else {
                userViewModel.setIsLoggedIn(true)
                goToAuthActivity()
            }
        }
    }

    private fun goToAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java).apply {
            // i use that to manage backstack,  FLAG_ACTIVITY_NEW_TASK i said to him to create new task
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val options = ActivityOptions.makeCustomAnimation(this,
           android.R.anim.fade_in,
           android.R.anim.fade_out
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
}