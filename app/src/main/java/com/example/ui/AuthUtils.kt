@file:Suppress("DEPRECATION")

package com.example.ui

import android.app.Activity
import android.content.Intent
import com.example.e_commerce.BuildConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

fun getGoogleRequestIntent(context: Activity): Intent {
    val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(BuildConfig.clientServerId) // Replace with your client ID
        .requestEmail()
        .requestProfile()
        .requestServerAuthCode(BuildConfig.clientServerId)
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)

    // Show the account picker menu
    googleSignInClient.signOut() // Ensures the picker menu is shown, not direct login

    return googleSignInClient.signInIntent
}