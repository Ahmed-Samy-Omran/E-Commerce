package com.example

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.initialize
import io.reactivex.schedulers.Schedulers

//manages the lifecycle of the application
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        listenToNetworkConnectivity()
//        FirebaseApp.initializeApp(this)

    }
    @SuppressLint("CheckResult")
    fun listenToNetworkConnectivity() {
        ReactiveNetwork.observeInternetConnectivity().subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io()).subscribe { isConnected: Boolean ->
                Log.d(TAG, "Connected to internet: $isConnected")
                FirebaseCrashlytics.getInstance().setCustomKey("connected_to_internet", isConnected)
            }
    }

    companion object {
        private const val TAG = "MyApplication"
    }
}