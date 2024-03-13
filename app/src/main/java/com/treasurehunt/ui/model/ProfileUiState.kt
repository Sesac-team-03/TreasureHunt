package com.treasurehunt.ui.model

import com.treasurehunt.data.remote.model.UserDTO

data class ProfileUiState(
    val uid: String? = null,
    val user: UserDTO? = null,
    val imageContentUri: String? = null,
    val imageStorageUrl: String? = null
)