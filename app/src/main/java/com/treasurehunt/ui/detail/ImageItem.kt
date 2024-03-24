package com.treasurehunt.ui.detail

import com.treasurehunt.ui.model.TextTheme

sealed class ImageItem {
    data class Url(val url: String) : ImageItem()

    data class Text(val value: String, val theme: TextTheme) : ImageItem()
}
