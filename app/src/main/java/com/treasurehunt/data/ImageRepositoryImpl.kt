package com.treasurehunt.data

import com.treasurehunt.data.local.ImageDao
import com.treasurehunt.data.local.model.ImageEntity
import com.treasurehunt.data.remote.ImageRemoteDataSource
import com.treasurehunt.data.remote.model.ImageDTO
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val imageDao: ImageDao,
    private val imageRemoteDataSource: ImageRemoteDataSource
) : ImageRepository {

    override suspend fun insert(imageEntity: ImageEntity): Long = imageDao.insert(imageEntity)

    override suspend fun insert(imageDTO: ImageDTO): String = imageRemoteDataSource.insert(imageDTO)

    override suspend fun getRemoteImage(id: String): ImageDTO = imageRemoteDataSource.getRemoteImage(id)
}