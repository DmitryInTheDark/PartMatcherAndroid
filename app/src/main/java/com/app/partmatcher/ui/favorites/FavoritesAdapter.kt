package com.app.partmatcher.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.partmatcher.R
import com.app.partmatcher.data.model.PartDto
import com.app.partmatcher.databinding.ItemPartBinding
import com.bumptech.glide.Glide

class FavoritesAdapter(
    private val onPartClick: (PartDto) -> Unit,
    private val onRemoveClick: (PartDto) -> Unit
) : ListAdapter<PartDto, FavoritesAdapter.FavoriteViewHolder>(PartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemPartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FavoriteViewHolder(private val binding: ItemPartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(part: PartDto) {
            binding.tvPartName.text = part.name
            binding.tvArticle.text = part.article
            binding.chipBrand.text = part.manufacturer
            binding.tvPrice.text = if (part.price != null) "${part.price} ₽" else "N/A"
            
            binding.btnFavorite.setImageResource(R.drawable.ic_favorite_filled)
            binding.btnFavorite.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.favorite_red))

            Glide.with(binding.ivPartThumbnail)
                .load(part.imageUrl ?: "")
                .placeholder(R.drawable.ic_part_placeholder)
                .error(R.drawable.ic_part_placeholder)
                .into(binding.ivPartThumbnail)

            binding.root.setOnClickListener { onPartClick(part) }
            binding.btnFavorite.setOnClickListener { onRemoveClick(part) }
        }
    }

    class PartDiffCallback : DiffUtil.ItemCallback<PartDto>() {
        override fun areItemsTheSame(oldItem: PartDto, newItem: PartDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PartDto, newItem: PartDto): Boolean {
            return oldItem == newItem
        }
    }
}
