    package com.example.utils

    import android.content.Context
    import android.util.AttributeSet
    import android.view.LayoutInflater
    import android.view.animation.Animation
    import android.view.animation.AnimationUtils
    import android.widget.LinearLayout
    import android.widget.TextView
    import com.example.e_commerce.R

    class FlipClockDigit @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : LinearLayout(context, attrs, defStyleAttr) {

        private var topDigit: TextView
        private var bottomDigit: TextView

        init {
            LayoutInflater.from(context).inflate(R.layout.flip_digit_layout, this, true)
            topDigit = findViewById(R.id.top_digit)
            bottomDigit = findViewById(R.id.bottom_digit)

            //        // Initialize with default values (e.g., "00")
            //        topDigit.text = "0"
            //        bottomDigit.text = "0"

            // Retrieve custom attributes if needed
            attrs?.let {
                val typedArray = context.obtainStyledAttributes(it, R.styleable.FlipClockDigit)
                typedArray.recycle()
            }
        }

        fun setDigit(digit: Int) {
            val currentDigit = bottomDigit.text.toString().toIntOrNull() ?: -1

            if (currentDigit != digit) {
                // Set the bottom digit first (before animation starts)
                bottomDigit.text = digit.toString()

                // Load animations
                val flipOut = AnimationUtils.loadAnimation(context, R.anim.flip_out)
                val flipIn = AnimationUtils.loadAnimation(context, R.anim.flip_in)

                flipOut.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        // Update the top digit when the flip-out ends
                        topDigit.text = digit.toString()
                        topDigit.startAnimation(flipIn)
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                })

                // Start flip-out animation immediately
                topDigit.startAnimation(flipOut)
            }
        }
    }