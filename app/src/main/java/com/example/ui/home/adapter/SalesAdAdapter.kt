package com.example.ui.home.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.databinding.ItemSalesAdBinding
import com.example.ui.home.model.SalesAdUIModel
import com.example.utils.CountdownTimer
import com.example.utils.FlipClockDigit
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SalesAdAdapter(
    private val lifecycleScope: LifecycleCoroutineScope,
    private val salesAds: List<SalesAdUIModel>,
) :
    RecyclerView.Adapter<SalesAdAdapter.SalesAdViewHolder>() {

    val timersList = mutableMapOf<String, CountdownTimer>()

    inner class SalesAdViewHolder(private val binding: ItemSalesAdBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(salesAd: SalesAdUIModel) {
            salesAd.startCountdown()
            binding.lifecycleScope = lifecycleScope
            binding.salesAd = salesAd

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesAdViewHolder {
        val binding = ItemSalesAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SalesAdViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SalesAdViewHolder, position: Int) {
        holder.bind(salesAds[position])
    }

    override fun getItemCount(): Int = salesAds.size

    // Cancel all timers when the adapter is detached from the RecyclerView
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        salesAds.forEach { it.stopCountdown() }
    }
}

@BindingAdapter("countdownTimer", "lifecycleScope")
    fun timerChanges(
view: FlipClockDigit,
timerState: MutableStateFlow<String>?,
    lifecycleScope: LifecycleCoroutineScope?
) {
    val job: Job? = null // Track coroutine job

    lifecycleScope?.launch {
        job?.cancel() // Cancel previous coroutine if exists

        timerState?.collectLatest { value ->
            val digit = value.toIntOrNull() ?: 0 // Default to 0 if invalid
            view.setDigit(digit)
        }
    }
}