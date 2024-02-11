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

    override suspend fun insertImage(imageEntity: ImageEntity): Long = imageDao.insert(imageEntity)

    override suspend fun insertImage(imageDTO: ImageDTO): String =
        imageRemoteDataSource.insert(imageDTO)

<<<<<<< HEAD
    override suspend fun getRemoteImage(id: String): ImageDTO =
=======
    override suspend fun getImage(id: String): ImageDTO =
>>>>>>> 53c7952 (feat: 이미지 조회 getImage 함수 추가 및 imageModel로 변환)
        imageRemoteDataSource.getImage(id)
}