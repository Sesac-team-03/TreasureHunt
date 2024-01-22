package com.treasurehunt.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.treasurehunt.R

class ImageSliderAdapter(val imageItems: List<Any>) :
    RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>() {
    var currentPage: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = imageItems[position]

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

    override fun getItemCount(): Int {
        return imageItems.size
    }

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_image)
    }
}
