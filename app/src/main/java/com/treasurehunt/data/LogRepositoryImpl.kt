package com.treasurehunt.data

import com.treasurehunt.data.local.LogDao
import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.data.remote.LogRemoteDataSource
import com.treasurehunt.data.remote.model.LogDTO
import javax.inject.Inject

class LogRepositoryImpl @Inject constructor(
    private val logDao: LogDao,
    private val logRemoteDataSource: LogRemoteDataSource
) : LogRepository {

    override suspend fun insert(log: LogEntity) = logDao.insert(log)

    override suspend fun insert(log: LogDTO) = logRemoteDataSource.insert(log).name

    override fun getLocalLogById(id: String) = logDao.getLocalLogById(id)

    override fun getAllLocalLogs() = logDao.getAllLocalLogs()

    override suspend fun getRemoteLogById(id: String) = logRemoteDataSource.getRemoteLogById(id)

    override suspend fun getAllRemoteLogs() = logRemoteDataSource.getAllRemoteLogs()

    override fun update(log: LogEntity) = logDao.update(log)

    override suspend fun delete(vararg logs: LogEntity) = logDao.delete(*logs)

    override suspend fun deleteAllLocalLogs() {
        logDao.deleteAllLocalLogs()
    }

    override suspend fun delete(id: String) {
        logRemoteDataSource.delete(id)
    }
}