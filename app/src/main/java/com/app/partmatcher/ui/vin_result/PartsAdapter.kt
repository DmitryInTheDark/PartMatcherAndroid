package com.app.partmatcher.ui.vin_result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.partmatcher.data.model.PartDto
import com.app.partmatcher.databinding.ItemPartBinding
import com.bumptech.glide.Glide

class PartsAdapter(
    private val onPartClick: (PartDto) -> Unit
) : RecyclerView.Adapter<PartsAdapter.PartViewHolder>() {

    private var parts: List<PartDto> = emptyList()

    fun submitList(newParts: List<PartDto>) {
        parts = newParts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartViewHolder {
        val binding = ItemPartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PartViewHolder, position: Int) {
        holder.bind(parts[position])
    }

    override fun getItemCount(): Int = parts.size

    inner class PartViewHolder(private val binding: ItemPartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(part: PartDto) {
            binding.tvPartName.text = part.name
            binding.tvArticle.text = part.article
            binding.chipBrand.text = part.manufacturer
            binding.tvPrice.text = if (part.price != null) "${part.price} ₽" else "N/A"

            Glide.with(binding.ivPartThumbnail)
                .load(part.imageUrl)
                .placeholder(com.app.partmatcher.R.drawable.ic_part_placeholder)
                .into(binding.ivPartThumbnail)

            binding.root.setOnClickListener { onPartClick(part) }
        }
    }
}
