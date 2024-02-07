package com.treasurehunt.ui

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.treasurehunt.R

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

@BindingAdapter("storageUrl")
fun bindStorageUrl(view: ImageView, url: String?) {
    when (url) {
        null -> view.run {
            setImageResource(R.drawable.ic_no_profile_image)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }

        else -> view.run {
            val storageRef = Firebase.storage.getReferenceFromUrl(url)
            val thumbnailRequestBuilder = Glide.with(context)
                .load(storageRef)
                .sizeMultiplier(0.25f)
            Glide.with(context)
                .load(storageRef)
                .thumbnail(thumbnailRequestBuilder)
                .into(this)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }
}