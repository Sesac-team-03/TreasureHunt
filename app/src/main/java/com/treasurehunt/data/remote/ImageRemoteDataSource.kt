package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.ImageDTO

class ImageRemoteDataSource(private val imageService: ImageService) {

    suspend fun insert(imageDTO: ImageDTO) = imageService.insert(imageDTO).name
}