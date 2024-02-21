package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.ImageDTO
import javax.inject.Inject

class ImageRemoteDataSource @Inject constructor(private val imageService: ImageService) {

    suspend fun insert(imageDTO: ImageDTO) = imageService.insert(imageDTO).name

    suspend fun getRemoteImage(id: String) = imageService.getRemoteImage(id)
}