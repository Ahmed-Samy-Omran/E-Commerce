package com.example.ui

import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.e_commerce.R
import com.google.android.material.snackbar.Snackbar

// extension function to show a snake bar with an error message work with any view
fun View.showSnakeBarError(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)
        .setAction(this.context.resources.getString(R.string.ok)) { }.setActionTextColor(
            ContextCompat.getColor(this.context, R.color.white)
        ).show()
}

fun View.showRetrySnakeBarError(message: String, retry: () -> Unit) {
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)
        .setAction(this.context.resources.getString(R.string.retry)) { retry.invoke() }
        .setActionTextColor(
            ContextCompat.getColor(this.context, R.color.white)
        ).show()
}


@BindingAdapter("android:visibilities")
fun setVisibility(view: View, isEmpty: Boolean) {
    view.visibility = if (isEmpty) View.GONE else View.VISIBLE
}