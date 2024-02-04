package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.ImageDTO

interface ImageRepository {

    suspend fun insertImage(imageDTO: ImageDTO): String
}