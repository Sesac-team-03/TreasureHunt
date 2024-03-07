package com.treasurehunt.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LoginRepository {
    private val Context.loginDataStore: DataStore<Preferences> by preferencesDataStore(name = "auto_login")

    private object PreferencesKeys {
        val AUTO_LOGIN_KEY = booleanPreferencesKey("auto_login_check")
    }

    override val getAutoLoginState: Flow<Boolean> = context.loginDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.AUTO_LOGIN_KEY] ?: false
        }

    override suspend fun updateAutoLoginSwitch(isChecked: Boolean) {
        context.loginDataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_LOGIN_KEY] = isChecked
        }
    }
}