package com.treasurehunt.ui.searchmapplace.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.naver.maps.geometry.LatLng
import com.treasurehunt.R
import com.treasurehunt.databinding.ItemSearchMapPlaceBinding
import com.treasurehunt.ui.model.MapPlaceModel
import com.treasurehunt.ui.searchmapplace.MapPlaceClickListener
import com.treasurehunt.util.convertNaverLocalSearchMapXYToLatLng

private const val CATEGORY_SEPARATOR = '>'

class SearchMapPlaceAdapter(
    private val userPosition: LatLng,
    private val clickListener: MapPlaceClickListener
) :
    ListAdapter<MapPlaceModel, SearchMapPlaceAdapter.ViewHolder>(diffutil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position], userPosition, clickListener)
    }

    class ViewHolder(private val binding: ItemSearchMapPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            mapPlace: MapPlaceModel,
            userPosition: LatLng,
            clickListener: MapPlaceClickListener
        ) {
            with(binding) {
                tvTitle.text = HtmlCompat.fromHtml(mapPlace.title, HtmlCompat.FROM_HTML_MODE_LEGACY)
                tvRoadAddress.text = mapPlace.roadAddress
                tvCategory.text = mapPlace.category?.substringAfter(CATEGORY_SEPARATOR)
                tvDistance.text =
                    getDistance(mapPlace.mapx, mapPlace.mapy, userPosition)
                        ?: itemView.context.getString(R.string.search_map_place_unknown)

                root.setOnClickListener {
                    clickListener.onClick(mapPlace)
                }
            }
        }

        private fun getDistance(x: String?, y: String?, other: LatLng) =
            convertNaverLocalSearchMapXYToLatLng(x, y)
                ?.distanceTo(other)
                ?.toString()

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