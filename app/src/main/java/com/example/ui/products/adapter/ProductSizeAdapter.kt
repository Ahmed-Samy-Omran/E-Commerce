package com.example.ui.products.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.data.models.products.ProductSizeModel
import com.example.e_commerce.R
import com.example.e_commerce.databinding.ItemProductSizeBinding

class ProductSizeAdapter(
    private val sizes: List<ProductSizeModel>,
    private val onSizeSelected: (ProductSizeModel) -> Unit
) : RecyclerView.Adapter<ProductSizeAdapter.SizeViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProductSizeBinding.inflate(inflater, parent, false)
        return SizeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        holder.bind(sizes[position], position == selectedPosition) // ✅ Pass whole size model
    }

    override fun getItemCount(): Int = sizes.size

    inner class SizeViewHolder(private val binding: ItemProductSizeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.tvSize.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION && sizes[position].stock != 0) { // ✅ Prevent clicking on out-of-stock
                    val selectedSize = sizes[position]
                    val oldSelected = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(oldSelected)
                    notifyItemChanged(selectedPosition)
                    onSizeSelected(selectedSize)
                }
            }
        }

        fun bind(sizeModel: ProductSizeModel, isSelected: Boolean) {
            val context = binding.tvSize.context
            val size = sizeModel.size ?: ""
            val stock = sizeModel.stock ?: 0

            binding.tvSize.text = size

            if (stock == 0) {
                // ✅ Handle out-of-stock UI
                binding.tvSize.setBackgroundResource(R.drawable.bg_circle_unselected_size)
                binding.tvSize.setTextColor(ContextCompat.getColor(context, R.color.neutral_grey))
                binding.tvSize.paintFlags = binding.tvSize.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvSize.isEnabled = false
            } else {
                // ✅ Normal available UI
                binding.tvSize.setBackgroundResource(
                    if (isSelected) R.drawable.bg_circle_selected_size
                    else R.drawable.bg_circle_unselected_size
                )
                binding.tvSize.setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (isSelected) R.color.primary_color else R.color.primary_dark_color
                    )
                )
                binding.tvSize.paintFlags = binding.tvSize.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.tvSize.isEnabled = true
            }
        }
    }
}