package com.treasurehunt.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginDataStore: DataStore<Preferences>
) : LoginRepository {

    private object PreferencesKeys {
        val AUTO_LOGIN_KEY = booleanPreferencesKey("auto_login_check")
    }

    override val getAutoLoginState: Flow<Boolean> = loginDataStore.data
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
        loginDataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_LOGIN_KEY] = isChecked
        }
    }
}