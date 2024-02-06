package com.treasurehunt.ui.model

data class UserModel(
    val email: String?,
    val nickName: String? = null,
    val profileImage: String? = null,
    val friends: Map<String, Boolean> = emptyMap(),
    val logs: Map<String, Boolean> = emptyMap(),
    val places: Map<String, Boolean> = emptyMap(),
    val plans: Map<String, Boolean> = emptyMap(),
    val remoteId: String? = null
)
