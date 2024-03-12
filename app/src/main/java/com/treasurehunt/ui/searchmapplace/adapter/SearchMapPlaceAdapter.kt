package com.treasurehunt.ui.searchmapplace.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.treasurehunt.databinding.ItemSearchMapPlaceBinding
import com.treasurehunt.ui.model.MapPlaceModel
import com.treasurehunt.ui.searchmapplace.MapPlaceClickListener

class SearchMapPlaceAdapter(private val clickListener: MapPlaceClickListener) :
    ListAdapter<MapPlaceModel, SearchMapPlaceAdapter.ViewHolder>(diffutil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position], clickListener)
    }

    class ViewHolder(private val binding: ItemSearchMapPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(mapPlace: MapPlaceModel, clickListener: MapPlaceClickListener) {
            with(binding) {
                tvTitle.text = HtmlCompat.fromHtml(mapPlace.title, HtmlCompat.FROM_HTML_MODE_LEGACY)
                tvRoadAddress.text = mapPlace.roadAddress
                tvCategory.text = mapPlace.category?.substringAfter('>') // trim

                // distance

                root.setOnClickListener {
                    clickListener.onClick(mapPlace)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ItemSearchMapPlaceBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    companion object {
        private val diffutil = object : DiffUtil.ItemCallback<MapPlaceModel>() {
            override fun areItemsTheSame(oldItem: MapPlaceModel, newItem: MapPlaceModel): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(
                oldItem: MapPlaceModel,
                newItem: MapPlaceModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}