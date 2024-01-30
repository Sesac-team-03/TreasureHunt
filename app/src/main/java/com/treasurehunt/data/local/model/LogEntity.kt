package com.treasurehunt.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.treasurehunt.data.remote.model.LogDTO

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
    val (place, images, text, theme, createdDate) = this
    return LogDTO(place, images.associateWith { true }, text, theme, createdDate)
}
