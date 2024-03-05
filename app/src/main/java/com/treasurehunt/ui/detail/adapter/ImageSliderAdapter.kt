package com.treasurehunt.ui.detail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.treasurehunt.R
import com.treasurehunt.databinding.ItemImageBinding
import com.treasurehunt.ui.detail.ImageItem

class ImageSliderAdapter :
    ListAdapter<ImageItem.Url, ImageSliderAdapter.ImageViewHolder>(ImageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemImageBinding.inflate(layoutInflater, parent, false)
        return ImageViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = getItem(position)
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(item.url)
        Glide.with(holder.imageView)
            .load(storageReference)
            .into(holder.imageView)
    }

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_image)
    }

    class ImageDiffCallback : DiffUtil.ItemCallback<ImageItem.Url>() {
        override fun areItemsTheSame(oldItem: ImageItem.Url, newItem: ImageItem.Url): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: ImageItem.Url, newItem: ImageItem.Url): Boolean {
            return oldItem == newItem
        }
    }
}