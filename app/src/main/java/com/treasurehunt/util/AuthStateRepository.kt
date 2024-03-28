package com.treasurehunt.util

import kotlinx.coroutines.flow.Flow

interface AuthStateRepository {

    fun getUid(): Flow<String?>
}