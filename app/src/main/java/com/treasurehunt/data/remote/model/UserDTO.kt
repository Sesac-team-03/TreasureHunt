package com.treasurehunt.data.remote.model

import com.treasurehunt.ui.model.UserModel
import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val email: String?,
    val nickname: String? = null,
    val profileImage: String? = null,
    val isPublic: Boolean = false,
    val remoteFriendIds: Map<String, Boolean> = emptyMap(),
    val remoteLogIds: Map<String, Boolean> = emptyMap(),
    val remoteVisitIds: Map<String, Boolean> = emptyMap(),
    val remotePlanIds: Map<String, Boolean> = emptyMap(),
    val localId: Long = 0,
    val remoteId: String? = null
)

fun UserDTO.toUserModel(remoteId: String? = null) =
    UserModel(
        email,
        nickname,
        profileImage,
        remoteFriendIds,
        remoteLogIds,
        remoteVisitIds,
        remotePlanIds,
        remoteId
    )