package com.treasurehunt.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val email: String,
    val nickname: String? = null,
    val profileImage: String? = null,
    val public: Boolean = false,
    val friends: List<String> = emptyList(),
    val logs: List<String> = emptyList(),
    val places: List<String> = emptyList(),
    val plans: List<String> = emptyList(),
)