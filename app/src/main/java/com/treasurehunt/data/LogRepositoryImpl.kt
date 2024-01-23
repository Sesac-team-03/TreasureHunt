package com.treasurehunt.data

import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.data.local.LogDao
import com.treasurehunt.data.remote.model.LogDTO
import com.treasurehunt.data.remote.model.LogDTODataSource
import kotlinx.coroutines.flow.Flow

class LogRepositoryImpl(
    private val logDao: LogDao,
    private val logDTODataSource: LogDTODataSource
) : LogRepository {

    override suspend fun insert(log: LogEntity) = logDao.insert(log)
    override suspend fun getRemoteLog(id: String): LogDTO = logDTODataSource.getLog(id)
    override suspend fun getRemoteLogs(): List<LogDTO> = logDTODataSource.getLogs()

    override suspend fun addRemoteLog(logModel: LogDTO) {
        logDTODataSource.addLog(logModel)
    }

    override fun getLogById(id: String): Flow<LogEntity> = logDao.getLogById(id)

    override fun getAllLogs(): Flow<List<LogEntity>> = logDao.getAllLogs()

    override fun update(log: LogEntity) = logDao.update(log)

    override suspend fun delete(vararg logs: LogEntity) = logDao.delete(*logs)
    override suspend fun deleteAll() {
        logDao.deleteAllLogs()
    }

}