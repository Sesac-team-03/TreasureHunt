package com.treasurehunt.data

import com.treasurehunt.data.local.model.ImageEntity
import com.treasurehunt.data.remote.model.ImageDTO

interface ImageRepository {

    suspend fun insert(imageEntity: ImageEntity): Long

    suspend fun insert(imageDTO: ImageDTO): String

    suspend fun getRemoteImage(id: String): ImageDTO
}