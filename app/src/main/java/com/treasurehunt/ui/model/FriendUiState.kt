package com.treasurehunt.ui.model

data class FriendUiState(
    val signedInAsRegisteredUser: Boolean,
    val uid: String? = null,
    val friends: Map<UserModel, Boolean> = emptyMap(),
    val isOnline: Boolean = false
)