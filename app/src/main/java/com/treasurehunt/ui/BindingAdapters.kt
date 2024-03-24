package com.treasurehunt.ui

import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.treasurehunt.R
import com.treasurehunt.ui.model.ImageModel
import com.treasurehunt.ui.model.LogModel
import com.treasurehunt.ui.model.TextTheme

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
    if (url == null) return

    try {
        view.run {
            val storageRef = storage.getReferenceFromUrl(url)
            val thumbnailRequestBuilder = Glide.with(context)
                .load(storageRef)
                .placeholder(R.color.gray_200)
                .sizeMultiplier(0.25f)
            Glide.with(context)
                .load(storageRef)
                .error(R.drawable.ic_no_image)
                .thumbnail(thumbnailRequestBuilder)
                .into(this)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
    } catch (e: Exception) {
        view.run {
            setImageResource(R.drawable.ic_no_image)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
    }
}

@BindingAdapter("themedText")
fun bindThemedText(view: TextView, log: LogModel?) {
    if (log == null) return

    view.run {
        val theme = TextTheme.entries[log.theme]
        background = theme.backgroundResId?.let {
            AppCompatResources.getDrawable(view.context, it)
        }
        setTextColor(view.context.getColor(theme.textColorResId))
        text = log.text
    }
}