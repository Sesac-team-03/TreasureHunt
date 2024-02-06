package com.treasurehunt.data.remote.model

import com.treasurehunt.ui.model.UserModel
import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val email: String?,
    val nickname: String? = null,
    val profileImage: String? = null,
    val public: Boolean = false,
    val friends: Map<String, Boolean> = emptyMap(),
    val logs: Map<String, Boolean> = emptyMap(),
    val places: Map<String, Boolean> = emptyMap(),
    val plans: Map<String, Boolean> = emptyMap(),
    val localId: Long = 0,
    val remoteId: String? = null
)

fun UserDTO.toUserModel(remoteId: String): UserModel {
    return UserModel(email, nickname, profileImage, friends, logs, places, plans, remoteId)
}