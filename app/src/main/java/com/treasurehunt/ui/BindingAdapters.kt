package com.treasurehunt.ui

import android.graphics.Color
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.treasurehunt.R

private val storage = Firebase.storage

@BindingAdapter("dataset")
fun bindDataset(view: RecyclerView, itemList: List<Any>?) {
    view.adapter?.let {
        (view.adapter as ListAdapter<Any, *>).submitList(itemList)
    }
}

@BindingAdapter("imageUrl")
fun bindImageUrl(view: ImageView, url: String?) {
    when (url) {
        null -> view.run {
            setImageResource(R.drawable.ic_no_profile_image)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }

        else -> view.run {
            Glide.with(context).load(url)
                .into(this)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }
}

@BindingAdapter("imageStorageUrl")
fun bindImageStorageUrl(view: ImageView, url: String?) {
    try {
        view.run {
            val storageRef = storage.getReferenceFromUrl(url!!)
            val thumbnailRequestBuilder = Glide.with(view.context)
                .load(storageRef)
                .placeholder(R.color.gray_200)
                .sizeMultiplier(0.25f)
            Glide.with(view.context)
                .load(storageRef)
                .thumbnail(thumbnailRequestBuilder)
                .into(this)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
    } catch (e: Exception) {
        view.run {
            setBackgroundColor(Color.GRAY)
            scaleType = android.widget.ImageView.ScaleType.CENTER_INSIDE
        }
    }
}