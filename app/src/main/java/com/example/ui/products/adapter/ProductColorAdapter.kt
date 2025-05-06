package com.example.ui.products.adapter

import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.R
import com.example.e_commerce.databinding.ProductItemColorBinding
import com.example.ui.products.model.ProductColorUIModel

class ProductColorAdapter(
    private val colors: List<ProductColorUIModel>,
    private val onColorSelected: (ProductColorUIModel) -> Unit
) : RecyclerView.Adapter<ProductColorAdapter.ColorViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val binding =
            ProductItemColorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ColorViewHolder(binding)
    }

    override fun getItemCount(): Int = colors.size

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colors[position], position == selectedPosition)
    }

    inner class ColorViewHolder(private val binding: ProductItemColorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedColor = colors[position]
                    val oldPos = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(oldPos)
                    notifyItemChanged(selectedPosition)
                    onColorSelected(selectedColor)
                }
            }
        }
//
//        fun bind(colorModel: ProductColorUIModel, isSelected: Boolean) {
//            val colorHex = colorModel.color ?: "#FFFFFF"
//            //It casts the background to GradientDrawable so we can change its color dynamically.
//            val colorDrawable = binding.colorCircle.background as GradientDrawable
//            //Converts the hex color string into an actual color and sets it as the fill color of the outer circle.
//            colorDrawable.setColor(android.graphics.Color.parseColor(colorHex))
//
//            // Show the small circle only if selected
//            binding.selectedIndicator.visibility = if (isSelected) View.VISIBLE else View.GONE
//        }

        fun bind(colorModel: ProductColorUIModel, isSelected: Boolean) {
            // Get color string or default to white
            val colorHex = colorModel.color ?: "#FFFFFF"

            val colorDrawable = binding.colorCircle.background as GradientDrawable
            colorDrawable.setColor(android.graphics.Color.parseColor(colorHex))

            // Show the small circle only if selected
            binding.selectedIndicator.visibility = if (isSelected) View.VISIBLE else View.GONE
        }
    }
}