package com.example.utils

import android.animation.AnimatorSet
import android.animation.IntEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle

import android.view.MotionEvent
import android.view.View

import android.widget.ImageView
import androidx.core.animation.doOnEnd
import com.bumptech.glide.Glide
import com.example.e_commerce.R


class FullscreenImageDialog(
    context: Context,
    private val imageUrl: String,
    private val startBounds: IntArray // [x, y, width, height]
) : Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen) {

    private lateinit var fullscreenImageView: ImageView
    private var endBounds: IntArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setDimAmount(0.7f)
            decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    )
        }

        setContentView(R.layout.dialog_fullscreen_image)
        fullscreenImageView = findViewById(R.id.fullscreenImageView)

        Glide.with(context)
            .load(imageUrl)
            .into(fullscreenImageView)

        fullscreenImageView.post {
            // Get screen dimensions
            val display = window?.windowManager?.defaultDisplay
            val size = Point()
            display?.getSize(size)
            val screenWidth = size.x
            val screenHeight = size.y

            // Calculate target dimensions (90% of screen)
            val targetWidth = (screenWidth * 0.9).toInt()
            val targetHeight = (screenHeight * 0.9).toInt()
            val targetX = (screenWidth - targetWidth) / 2
            val targetY = (screenHeight - targetHeight) / 2

            // Save end bounds for reverse animation
            endBounds = intArrayOf(targetX, targetY, targetWidth, targetHeight)

            // Set initial position (small image)
            fullscreenImageView.x = startBounds[0].toFloat()
            fullscreenImageView.y = startBounds[1].toFloat()
            fullscreenImageView.layoutParams.width = startBounds[2]
            fullscreenImageView.layoutParams.height = startBounds[3]
            fullscreenImageView.requestLayout()

            // Animate to fullscreen
            animateImage(fullscreenImageView, targetX, targetY, targetWidth, targetHeight)
        }

        fullscreenImageView.setOnClickListener {
            animateBackToOriginalPosition()
        }
    }

    private fun animateImage(
        view: ImageView,
        targetX: Int,
        targetY: Int,
        targetWidth: Int,
        targetHeight: Int
    ) {
        val animatorSet = android.animation.AnimatorSet()

        // Create animators for position
        val xAnimator = ObjectAnimator.ofFloat(view, View.X, targetX.toFloat())
        val yAnimator = ObjectAnimator.ofFloat(view, View.Y, targetY.toFloat())

        // Create animators for size using ValueAnimator
        val widthAnimator = ValueAnimator.ofInt(view.width, targetWidth).apply {
            addUpdateListener { animator ->
                view.layoutParams.width = animator.animatedValue as Int
                view.requestLayout()
            }
        }

        val heightAnimator = ValueAnimator.ofInt(view.height, targetHeight).apply {
            addUpdateListener { animator ->
                view.layoutParams.height = animator.animatedValue as Int
                view.requestLayout()
            }
        }

        animatorSet.playTogether(xAnimator, yAnimator, widthAnimator, heightAnimator)
        animatorSet.duration = 300
        animatorSet.interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        animatorSet.start()
    }

    private fun animateBackToOriginalPosition() {
        endBounds?.let { currentBounds ->
            val animatorSet = android.animation.AnimatorSet()

            // Create animators for position
            val xAnimator = ObjectAnimator.ofFloat(
                fullscreenImageView,
                View.X,
                startBounds[0].toFloat()
            )
            val yAnimator = ObjectAnimator.ofFloat(
                fullscreenImageView,
                View.Y,
                startBounds[1].toFloat()
            )

            // Create animators for size
            val widthAnimator = ValueAnimator.ofInt(
                fullscreenImageView.width,
                startBounds[2]
            ).apply {
                addUpdateListener { animator ->
                    fullscreenImageView.layoutParams.width = animator.animatedValue as Int
                    fullscreenImageView.requestLayout()
                }
            }

            val heightAnimator = ValueAnimator.ofInt(
                fullscreenImageView.height,
                startBounds[3]
            ).apply {
                addUpdateListener { animator ->
                    fullscreenImageView.layoutParams.height = animator.animatedValue as Int
                    fullscreenImageView.requestLayout()
                }
            }

            animatorSet.playTogether(xAnimator, yAnimator, widthAnimator, heightAnimator)
            animatorSet.duration = 300
            animatorSet.interpolator = android.view.animation.AccelerateDecelerateInterpolator()
            animatorSet.doOnEnd { dismiss() }
            animatorSet.start()
        } ?: dismiss()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            animateBackToOriginalPosition()
            return true
        }
        return super.onTouchEvent(event)
    }
}
