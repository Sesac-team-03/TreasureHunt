package com.treasurehunt.data

import kotlinx.coroutines.flow.Flow

interface LoginRepository {

    val getAutoLoginState: Flow<Boolean>

    suspend fun updateAutoLoginSwitch(isChecked: Boolean)
}