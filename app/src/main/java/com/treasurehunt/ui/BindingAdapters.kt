package com.treasurehunt.ui

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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