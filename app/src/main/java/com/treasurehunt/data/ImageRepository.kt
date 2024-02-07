package com.treasurehunt.data

import com.treasurehunt.data.local.model.ImageEntity
import com.treasurehunt.data.remote.model.ImageDTO

interface ImageRepository {

    suspend fun insertImage(imageEntity: ImageEntity): Long

    suspend fun insertImage(imageDTO: ImageDTO): String

    suspend fun getImage(id: String): ImageDTO

}