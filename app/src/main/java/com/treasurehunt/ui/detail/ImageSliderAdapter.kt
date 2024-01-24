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

class ImageSliderAdapter(
    private val imageUrls: List<Any>
) : ListAdapter<Any, ImageSliderAdapter.ImageViewHolder>(ImageDiffCallback()) {

    internal var currentPage: Int = 0

    init {
        submitList(imageUrls)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = getItem(position)

        if (item is String) {
            Glide.with(holder.itemView.context)
                .load(item)
                .into(holder.imageView)
        } else if (item is Int) {
            Glide.with(holder.itemView.context)
                .load(item)
                .into(holder.imageView)
        }

        currentPage = position
    }

    fun getImageItems(): List<Any> {
        return currentList
    }

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_image)
    }

    class ImageDiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }
    }
}