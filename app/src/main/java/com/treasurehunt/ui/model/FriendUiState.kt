package com.treasurehunt.ui.model

import com.treasurehunt.data.remote.model.UserDTO

data class FriendUiState(
    val signedInAsRegisteredUser: Boolean,
    val uid: String? = null,
    val friends: List<UserDTO> = emptyList(),
    val isOnline: Boolean = false
)