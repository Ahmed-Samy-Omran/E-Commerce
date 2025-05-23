package com.example.ui.home.model

import android.provider.ContactsContract.Data
import android.util.Log
import android.widget.TextSwitcher
import com.example.utils.CountdownTimer
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Date

data class SalesAdUIModel(
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,

    var imageUrl: String? = null, val type: String? = null,

    var productId: String? = null,

    var categoryId: String? = null,

    var externalLink: String? = null,

    var endAt: Date? = null
) {

    private var timer: CountdownTimer? = null
    val hours = MutableStateFlow("")
    val minutes = MutableStateFlow("")
    val seconds = MutableStateFlow("")

    fun startCountdown() {
        endAt?.let {
            timer?.stop()
            timer = CountdownTimer(it) { hours, minutes, seconds ->
                Log.d("CountdownTimer", "hours: $hours, minutes: $minutes, seconds: $seconds")
                this.hours.value = hours.toString()
                this.seconds.value = seconds.toString()
                this.minutes.value = minutes.toString()
//                this.hours.value = hours.toString().padStart(2, '0')
//                this.minutes.value = minutes.toString().padStart(2, '0')
//                this.seconds.value = seconds.toString().padStart(2, '0')

            }
            timer?.start()
        }
    }

    fun stopCountdown() {
        timer?.stop()
    }

}
enum class SalesAdType {
    PRODUCT,
    CATEGORY,
    EXTERNAL_LINK,
    EMPTY
}