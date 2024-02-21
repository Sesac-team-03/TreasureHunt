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

    override suspend fun insert(image: ImageEntity) = imageDao.insert(image)

    override suspend fun insert(image: ImageDTO) = imageRemoteDataSource.insert(image)

    override suspend fun getRemoteImageById(id: String) = imageRemoteDataSource.getRemoteImageById(id)

    override suspend fun delete(id: String) {
        imageRemoteDataSource.delete(id)
    }
}