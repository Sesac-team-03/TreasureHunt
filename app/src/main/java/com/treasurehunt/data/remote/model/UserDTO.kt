package com.treasurehunt.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val email: String?,
    val nickname: String? = null,
    val profileImage: String? = null,
    val public: Boolean = false,
<<<<<<< HEAD
    val friends: Map<String, Boolean> = emptyMap(),
    val logs: Map<String, Boolean> = emptyMap(),
    val places: Map<String, Boolean> = emptyMap(),
    val plans: Map<String, Boolean> = emptyMap(),
=======
    val friends: List<String> = emptyList(),
    val logs: Map<String, Boolean> = emptyMap(),
    val places: Map<String, Boolean> = emptyMap(),
    val plans: Map<String, Boolean> = emptyMap()
>>>>>>> 1af2985 (fix: UserDTO에 대해 logs, places, plans 프로퍼티를 리스트 -> 맵 타입으로 수정)
)