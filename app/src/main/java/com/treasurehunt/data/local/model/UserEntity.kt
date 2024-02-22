package com.treasurehunt.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("users")
data class UserEntity(
    val nickname: String,
    @ColumnInfo("profile_image")
    val profileImage: String? = null,
    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0,
    @ColumnInfo("remote_id")
    val remoteId: String? = null
)