package com.example.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.databinding.ItemSalesAdBinding
import com.example.ui.home.model.SalesAdUIModel
import com.example.utils.CountdownTimer
import kotlinx.coroutines.cancel


class SalesAdAdapter(private val salesAds: List<SalesAdUIModel>) :
    RecyclerView.Adapter<SalesAdAdapter.SalesAdViewHolder>() {

    val timersList = mutableMapOf<String, CountdownTimer>()

    inner class SalesAdViewHolder(private val binding: ItemSalesAdBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(salesAd: SalesAdUIModel) {
            binding.salesAd = salesAd
            salesAd.endAt?.let {
                timersList[salesAd.id ?: ""]?.cancel()
                timersList.remove(salesAd.id ?: "")
                val timer = CountdownTimer(it) { hours, minutes, seconds ->
                    binding.hoursTextView.text = hours.toString()
                    binding.minutesTextView.text = minutes.toString()
                    binding.secondsTextView.text = seconds.toString()
                }
                timer.start()
                timersList.put(salesAd.id ?: "", timer)
            }
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
        timersList.forEach { (_, timer) ->
            timer.cancel()
        }
    }
}