package com.treasurehunt.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.treasurehunt.data.remote.model.LogDTO
import com.treasurehunt.ui.model.LogModel

@Entity(tableName = "logs")
data class LogEntity(
    val remotePlaceId: String,
    val text: String,
    val theme: String,
    @ColumnInfo("created_date")
    val createdDate: Long,
    val remoteImageIds: List<String>,
    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0,
    @ColumnInfo("remote_id")
    val remoteId: String? = null
)

fun LogEntity.toLogDTO() =
    LogDTO(remotePlaceId, text, theme, createdDate, remoteImageIds.associateWith { true }, localId)

fun LogEntity.toLogModel(imagesUrls: List<String>) =
    LogModel(remotePlaceId, text, theme, createdDate, remoteImageIds, imagesUrls,localId,remoteId)