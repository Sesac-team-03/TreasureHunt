package com.treasurehunt.ui.model

data class NaverUser(
    val id: String,
    val email: String? = null,
    val nickname: String? = null,
    val profileImage: String? = null
)