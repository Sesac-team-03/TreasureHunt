package com.treasurehunt.data

import com.treasurehunt.data.local.model.ImageEntity
import com.treasurehunt.data.remote.model.ImageDTO

interface ImageRepository {

    suspend fun insert(image: ImageEntity): Long

    suspend fun insert(image: ImageDTO): String

    suspend fun getRemoteImageById(id: String): ImageDTO

    suspend fun delete(id: String)
}