package com.treasurehunt.ui

import android.graphics.Color
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.treasurehunt.R
import com.treasurehunt.ui.model.ImageModel
import java.lang.Exception

private val storage = Firebase.storage

@BindingAdapter("dataset")
fun bindDataset(view: RecyclerView, itemList: List<Any>?) {
    view.adapter?.let {
        (view.adapter as ListAdapter<Any, *>).submitList(itemList)
    }
}

@BindingAdapter("imageSource")
fun bindImageSource(
    view: ImageView,
    imageSource: ImageModel
) {
    val src = imageSource.contentUri.ifEmpty { storage.getReferenceFromUrl(imageSource.storageUrl) }
    Glide.with(view.context)
        .load(src)
        .into(view)
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
                .error(R.drawable.ic_no_image)
                .thumbnail(thumbnailRequestBuilder)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(this)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
    } catch (e: Exception) {
        view.run {
            setImageResource(R.drawable.ic_no_image)
            scaleType = android.widget.ImageView.ScaleType.CENTER_INSIDE
        }
    }
}