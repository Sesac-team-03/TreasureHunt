package com.treasurehunt.data

data class User(
    val email: String,
    val id: String,
    val nickname: String? = null,
    val profileImage: String? = null
)