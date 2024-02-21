package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.ImageDTO
import javax.inject.Inject

class ImageRemoteDataSource @Inject constructor(private val imageService: ImageService) {

    suspend fun insert(image: ImageDTO) = imageService.insert(image).name

    suspend fun getRemoteImageById(id: String) = imageService.getRemoteImageById(id)

    suspend fun delete(id: String) {
        imageService.delete(id)
    }
}