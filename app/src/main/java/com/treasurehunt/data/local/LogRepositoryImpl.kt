package com.treasurehunt.data.local

import com.treasurehunt.data.model.LogEntity
import kotlinx.coroutines.flow.Flow

class LogRepositoryImpl(private val logDao: LogDao) : LogRepository {
    override suspend fun insert(log: LogEntity) {
        logDao.insert(log)
    }

    override fun getLogById(id: String): Flow<LogEntity> = logDao.getLogById(id)

    override fun getAllLogs(): Flow<List<LogEntity>> = logDao.getAllLogs()

    override suspend fun delete(log: LogEntity) {
        logDao.delete(log)
    }
}