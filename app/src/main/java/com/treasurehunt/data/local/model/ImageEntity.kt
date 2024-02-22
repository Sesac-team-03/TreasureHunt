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
    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0,
    @ColumnInfo("remote_id")
    val remoteId: String? = null
)

fun ImageEntity.toImageDTO() = ImageDTO(url, localId)