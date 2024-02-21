package com.treasurehunt.ui.savelog

import com.treasurehunt.ui.model.ImageModel

fun interface ImageClickListener {
    fun onClick(imageModel: ImageModel)
}