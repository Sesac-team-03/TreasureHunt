package com.treasurehunt.data

import com.treasurehunt.data.local.LogDao
import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.data.remote.model.LogDTO
import com.treasurehunt.data.remote.model.LogRemoteDataSource
import com.treasurehunt.ui.model.LogModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class LogRepositoryImpl(
    private val logDao: LogDao,
    private val logRemoteDataSource: LogRemoteDataSource
) : LogRepository {

    override suspend fun insert(log: LogEntity) = logDao.insert(log)

    override suspend fun insert(logDTD: LogDTO) = logRemoteDataSource.insert(logDTD).name

    override suspend fun getRemoteLog(id: String): LogDTO = logRemoteDataSource.getLog(id)

    override suspend fun getLogModelById(id: String): LogModel {
        val logEntity = logDao.getLogById(id).firstOrNull() ?: throw Exception("Log not found")
        
        return LogModel(
            text = logEntity.text,
            images = logEntity.images,
            theme = logEntity.theme,
            createdDate = logEntity.createdDate,
            place = logEntity.place
        )
    }

    override suspend fun getRemoteAllLogs(): List<LogDTO> = logRemoteDataSource.getAllLogs()

    override fun getLogById(id: String): Flow<LogEntity> = logDao.getLogById(id)

    override fun getAllLogs(): Flow<List<LogEntity>> = logDao.getAllLogs()

    override fun update(log: LogEntity) = logDao.update(log)

    override suspend fun delete(vararg logs: LogEntity) = logDao.delete(*logs)

    override suspend fun deleteAll() {
        logDao.deleteAllLogs()
    }
}