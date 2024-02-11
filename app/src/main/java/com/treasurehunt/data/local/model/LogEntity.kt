package com.treasurehunt.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.treasurehunt.data.ImageRepository
import com.treasurehunt.data.remote.model.LogDTO
import com.treasurehunt.ui.model.LogModel

@Entity(tableName = "logs")
data class LogEntity(
    val place: String,
    val images: List<String>,
    val text: String,
    val theme: String,
    @ColumnInfo("created_date")
    val createdDate: Long,
    @ColumnInfo("remote_id")
    val remoteId: String? = null,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
)

fun LogEntity.toLogDTO(): LogDTO {
    val (place, images, text, theme, createdDate, _, localId) = this
    return LogDTO(place, images.associateWith { true }, text, theme, createdDate, localId)
}

suspend fun LogEntity.toLogModel(
    imageRepo: ImageRepository
): LogModel {
    val (place, imageIds, text, theme, createdDate, _, _) = this
    val imagesUrls = imageIds.map { id -> imageRepo.getImage(id).url }
    return LogModel(place, imageIds, imagesUrls, text, theme, createdDate)
}
