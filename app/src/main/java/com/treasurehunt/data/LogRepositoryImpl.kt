package com.treasurehunt.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.treasurehunt.data.local.LogDao
import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.data.remote.LogRemoteDataSource
import com.treasurehunt.data.remote.model.LogDTO
import kotlinx.coroutines.flow.Flow
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
    override fun getPagingLogs(pageSize: Int,initialLoadSize:Int): Flow<PagingData<LogEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize,
                initialLoadSize = 3,
                enablePlaceholders = false,
            )
        ) { logDao.getPagingLogs() }
            .flow
    }

    override fun update(log: LogEntity) = logDao.update(log)

    override suspend fun update(id: String, log: LogDTO) {
        logRemoteDataSource.update(id, log)
    }

    override suspend fun delete(vararg logs: LogEntity) = logDao.delete(*logs)

    override suspend fun deleteAllLocalLogs() {
        logDao.deleteAllLocalLogs()
    }

    override suspend fun delete(id: String) {
        logRemoteDataSource.delete(id)
    }
}