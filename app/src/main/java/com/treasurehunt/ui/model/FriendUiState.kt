package com.treasurehunt.ui.model

data class FriendUiState(
    val signedInAsRegisteredUser: Boolean,
    val uid: String? = null,
    val friends: List<UserModel> = emptyList(),
    val isOnline: Boolean = false
)