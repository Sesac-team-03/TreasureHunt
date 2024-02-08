package com.treasurehunt.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.treasurehunt.R
import com.treasurehunt.databinding.ItemImageBinding

class ImageSliderAdapter(
    private var imageItems: List<ImageItem>
) : ListAdapter<ImageItem, ImageSliderAdapter.ImageViewHolder>(ImageDiffCallback()) {

    init {
        submitList(imageItems)
    }

    fun updateData(newImageItems: List<ImageItem>) {
        imageItems = newImageItems
        submitList(newImageItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemImageBinding.inflate(layoutInflater, parent, false)
        return ImageViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = getItem(position)
        when (item) {
            is ImageItem.Url -> {
                Glide.with(holder.itemView.context).load(item.url).into(holder.imageView)
            }

            is ImageItem.ResourceId -> {
                Glide.with(holder.itemView.context).load(item.resourceId).into(holder.imageView)
            }
        }
    }

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_image)
    }

    class ImageDiffCallback : DiffUtil.ItemCallback<ImageItem>() {
        override fun areItemsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
            return when {
                oldItem is ImageItem.Url && newItem is ImageItem.Url -> oldItem.url == newItem.url
                oldItem is ImageItem.ResourceId && newItem is ImageItem.ResourceId -> oldItem.resourceId == newItem.resourceId
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
            return oldItem == newItem
        }
    }
}