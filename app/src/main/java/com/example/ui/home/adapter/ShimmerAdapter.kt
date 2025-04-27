package com.example.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import androidx.annotation.LayoutRes

class ShimmerAdapter(
    private val itemCount: Int,
    @LayoutRes private val shimmerLayoutId: Int
) : RecyclerView.Adapter<ShimmerAdapter.ShimmerViewHolder>() {

    inner class ShimmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun animate(position: Int) {
            itemView.alpha = 0f
            itemView.animate()
                .alpha(1f)
                .setDuration(500)
                .setStartDelay((position * 100).toLong())
                .start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShimmerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(shimmerLayoutId, parent, false)
        return ShimmerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShimmerViewHolder, position: Int) {
        holder.animate(position)
    }

    override fun getItemCount(): Int = itemCount
}