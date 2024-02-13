package com.treasurehunt.ui.feed.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.treasurehunt.databinding.ItemFeedBinding
import com.treasurehunt.ui.feed.LogClickListener
import com.treasurehunt.ui.model.LogModel

class FeedAdapter(
    private val logClickListener: LogClickListener
) :
    ListAdapter<LogModel, FeedAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position], logClickListener)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    class ViewHolder(private val binding: ItemFeedBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(log: LogModel, logClickListener: LogClickListener) {
            val images = log.imageUrls
            if (images.size > 1) binding.ivAlbum.visibility = View.VISIBLE
            binding.log = log
            binding.logClickListener = logClickListener
        }

    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<LogModel>() {
            override fun areItemsTheSame(oldItem: LogModel, newItem: LogModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: LogModel, newItem: LogModel): Boolean {
                return oldItem.createdDate == newItem.createdDate
            }
        }
    }
}