package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.ImageDTO
import com.treasurehunt.data.remote.model.ImageRemoteDataSource

class ImageRepositoryImpl(
    val imageRemoteDataSource: ImageRemoteDataSource
) : ImageRepository {
    override suspend fun insertImage(imageDTO: ImageDTO): String =
        imageRemoteDataSource.insert(imageDTO)
}