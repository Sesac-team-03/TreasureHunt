package com.treasurehunt.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("users")
data class UserEntity(
    val nickname: String,
    @ColumnInfo("profile_image")
    val profileImage: String? = null,
    @PrimaryKey(autoGenerate = true)
    val uid: Long = 0,
)