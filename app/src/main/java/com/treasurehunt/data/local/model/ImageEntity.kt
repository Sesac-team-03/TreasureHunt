package com.treasurehunt.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.treasurehunt.data.remote.model.ImageDTO

@Entity("images")
data class ImageEntity(
    val url: String,
    @ColumnInfo("local_path")
    val localPath: String? = null,
    @ColumnInfo("remote_id")
    val remoteId: String? = null,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
)

fun ImageEntity.toImageDTO(): ImageDTO {
    val (url, _, _, localId) = this
    return ImageDTO(url, localId)
}