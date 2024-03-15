package com.treasurehunt.data

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    val getAutoLoginState: Flow<Boolean>

    suspend fun updateAutoLoginSwitch(isChecked: Boolean)
}