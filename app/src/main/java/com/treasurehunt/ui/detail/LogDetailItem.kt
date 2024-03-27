package com.treasurehunt.ui.detail

import com.google.firebase.storage.StorageReference
import com.treasurehunt.ui.model.TextTheme

sealed class LogDetailItem {

    data class ImageItem(val storageReference: StorageReference) : LogDetailItem()

    data class TextItem(val value: String, val theme: TextTheme) : LogDetailItem()

    enum class ItemType {
        ImageItem, TextItem
    }
}
