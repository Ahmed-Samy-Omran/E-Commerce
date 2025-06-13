package com.example.utils


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View

import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator


object PlaneFlyAnimator {

    fun animate(view: View, onAnimationEnd: () -> Unit) {
        view.visibility = View.VISIBLE
        view.alpha = 1f
        view.translationX = 0f
        view.translationY = 0f

        val arcDuration = 1000L
        val exitDuration = 600L

        val arcRadiusX = 300f
        val arcDepthY = 300f

        // Arc motion down
        val translationXArc = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0f, arcRadiusX)
        val translationYArc = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, arcDepthY)

        val arcAnimator = AnimatorSet().apply {
            playTogether(translationXArc, translationYArc)
            interpolator = DecelerateInterpolator()
            duration = arcDuration
        }

        // Fly off to right edge and fade out
        val screenWidth = view.resources.displayMetrics.widthPixels.toFloat()
        val translationXOff =
            ObjectAnimator.ofFloat(view, View.TRANSLATION_X, arcRadiusX, screenWidth)
        val translationYOff =
            ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, arcDepthY, arcDepthY - 100f)
        val fadeOut = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f)

        val exitAnimator = AnimatorSet().apply {
            playTogether(translationXOff, translationYOff, fadeOut)
            interpolator = AccelerateInterpolator()
            duration = exitDuration
        }

        val fullSet = AnimatorSet()
        fullSet.playSequentially(arcAnimator, exitAnimator)

        fullSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd()
            }
        })

        fullSet.start()
    }
}
