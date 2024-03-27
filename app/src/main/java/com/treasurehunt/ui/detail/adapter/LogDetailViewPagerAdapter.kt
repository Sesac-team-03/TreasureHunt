package com.treasurehunt.ui.detail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.treasurehunt.databinding.ItemLogDetailImageBinding
import com.treasurehunt.databinding.ItemLogDetailTextBinding
import com.treasurehunt.ui.detail.LogDetailItem
import com.treasurehunt.ui.model.TextTheme

class LogDetailViewPagerAdapter :
    ListAdapter<LogDetailItem, LogDetailViewPagerAdapter.ViewHolder>(ImageDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is LogDetailItem.ImageItem -> {
                LogDetailItem.ItemType.ImageItem.ordinal
            }

            is LogDetailItem.TextItem -> {
                LogDetailItem.ItemType.TextItem.ordinal
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (LogDetailItem.ItemType.entries[viewType]) {
            LogDetailItem.ItemType.ImageItem -> {
                ImageItemViewHolder.from(parent)
            }

            LogDetailItem.ItemType.TextItem -> {
                TextItemViewHolder.from(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is ImageItemViewHolder -> {
                holder.bind(currentList[position] as LogDetailItem.ImageItem)
            }

            is TextItemViewHolder -> {
                holder.bind(currentList[position] as LogDetailItem.TextItem)
            }
        }
    }

    sealed class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ImageItemViewHolder(val binding: ItemLogDetailImageBinding) : ViewHolder(binding.root) {

        fun bind(item: LogDetailItem.ImageItem) {
            binding.ivImage.run {
                Glide.with(this).load(item.storageReference).into(this)
            }
        }

        companion object {
            fun from(parent: ViewGroup) = ImageItemViewHolder(
                ItemLogDetailImageBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    class TextItemViewHolder(val binding: ItemLogDetailTextBinding) : ViewHolder(binding.root) {

        fun bind(item: LogDetailItem.TextItem) {
            with(binding.tvText) {
                text = item.value
                setTextTheme(item.theme)
            }
        }

        private fun TextView.setTextTheme(theme: TextTheme) {
            background = theme.backgroundResId?.let {
                AppCompatResources.getDrawable(itemView.context, it)
            }
            setTextColor(itemView.context.getColor(theme.textColorResId))
        }

        companion object {
            fun from(parent: ViewGroup) = TextItemViewHolder(
                ItemLogDetailTextBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    class ImageDiffCallback : DiffUtil.ItemCallback<LogDetailItem>() {

        override fun areItemsTheSame(oldItem: LogDetailItem, newItem: LogDetailItem): Boolean {
            return if (oldItem is LogDetailItem.ImageItem && newItem is LogDetailItem.ImageItem) {
                oldItem.storageReference == newItem.storageReference
            } else if (oldItem is LogDetailItem.TextItem && newItem is LogDetailItem.TextItem) {
                oldItem.value == newItem.value
            } else {
                false
            }
        }

        override fun areContentsTheSame(oldItem: LogDetailItem, newItem: LogDetailItem): Boolean {
            return oldItem == newItem
        }
    }
}