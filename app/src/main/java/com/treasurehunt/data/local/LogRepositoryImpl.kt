package com.treasurehunt.data.local

import com.treasurehunt.data.local.model.LogEntity
import kotlinx.coroutines.flow.Flow

class LogRepositoryImpl(private val logDao: LogDao) : LogRepository {

    override suspend fun insert(log: LogEntity) = logDao.insert(log)

    override fun getLogById(id: String): Flow<LogEntity> = logDao.getLogById(id)

    override fun getAllLogs(): Flow<List<LogEntity>> = logDao.getAllLogs()

    override fun update(log: LogEntity) = logDao.update(log)

    override suspend fun delete(vararg logs: LogEntity) = logDao.delete(*logs)
    override suspend fun deleteAll() {
        logDao.deleteAllLogs()
    }


}