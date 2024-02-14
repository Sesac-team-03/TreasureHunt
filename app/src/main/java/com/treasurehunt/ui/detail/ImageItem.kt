package com.treasurehunt.ui.detail

sealed class ImageItem {
    data class Url(val url: String) : ImageItem()
}
