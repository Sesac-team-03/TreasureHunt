package com.treasurehunt.ui.detail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.treasurehunt.R
import com.treasurehunt.databinding.ItemImageBinding
import com.treasurehunt.databinding.ItemThemedTextBinding
import com.treasurehunt.ui.detail.ImageItem

class ImageSliderAdapter :
    ListAdapter<ImageItem, ImageSliderAdapter.ImageViewHolder>(ImageDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) is ImageItem.Url) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = if (viewType == 0) ItemImageBinding.inflate(
            layoutInflater,
            parent,
            false
        ) else ItemThemedTextBinding.inflate(layoutInflater, parent, false)
        return ImageViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = getItem(position)

        if (item is ImageItem.Text) {
            if (holder.textView == null) return
            holder.textView.text = item.value
            holder.textView.background = item.theme.backgroundResId?.let {
                AppCompatResources.getDrawable(holder.itemView.context, it)
            }
            holder.textView.setTextColor(holder.itemView.context.getColor(item.theme.textColorResId))
            return
        }

        if (item is ImageItem.Url) {
            if (holder.imageView == null) return
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(item.url)
            Glide.with(holder.imageView)
                .load(storageReference)
                .into(holder.imageView)
        }
    }

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView? = view.findViewById(R.id.item_image)

        val textView: TextView? = view.findViewById(R.id.item_text)
    }

    class ImageDiffCallback : DiffUtil.ItemCallback<ImageItem>() {
        override fun areItemsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
            return if (oldItem is ImageItem.Url && newItem is ImageItem.Url) {
                oldItem.url == newItem.url
            } else if (oldItem is ImageItem.Text && newItem is ImageItem.Text) {
                oldItem.value == newItem.value
            } else {
                false
            }
        }

        override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
            return oldItem == newItem
        }
    }
}