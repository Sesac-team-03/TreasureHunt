package com.treasurehunt.data.remote.model

import com.treasurehunt.data.remote.ImageService

class ImageRemoteDataSource(private val imageService: ImageService) {

    suspend fun insert(imageDTO: ImageDTO) = imageService.insert(imageDTO).name
}