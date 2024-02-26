package com.treasurehunt.ui.model

data class UserModel(
    val email: String?,
    val nickName: String? = null,
    val profileImage: String? = null,
    val remoteFriendIds: Map<String, Boolean> = emptyMap(),
    val remoteLogIds: Map<String, Boolean> = emptyMap(),
    val remoteVisitIds: Map<String, Boolean> = emptyMap(),
    val remotePlanIds: Map<String, Boolean> = emptyMap(),
    val remoteId: String? = null
)
