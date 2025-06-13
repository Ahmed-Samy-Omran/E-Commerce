package com.example.utils

import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation

object ViewButtonAnimations {
    fun playClickScaleAnimation(view: View, onAnimationEnd: () -> Unit) {
        val scaleUp = ScaleAnimation(
            1f, 1.1f,
            1f, 1.1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 150
            fillAfter = true
            repeatCount = 1
            repeatMode = Animation.REVERSE
        }

        view.startAnimation(scaleUp)

        scaleUp.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                onAnimationEnd()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }
}