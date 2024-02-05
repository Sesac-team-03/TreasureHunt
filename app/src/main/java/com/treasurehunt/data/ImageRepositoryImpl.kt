package com.treasurehunt.data

import com.treasurehunt.data.local.ImageDao
import com.treasurehunt.data.local.model.ImageEntity
import com.treasurehunt.data.remote.model.ImageDTO
import com.treasurehunt.data.remote.ImageRemoteDataSource
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val imageDao: ImageDao,
    private val imageRemoteDataSource: ImageRemoteDataSource
) : ImageRepository {

    override suspend fun insertImage(imageEntity: ImageEntity): Long = imageDao.insert(imageEntity)

    override suspend fun insertImage(imageDTO: ImageDTO): String =
        imageRemoteDataSource.insert(imageDTO)
}