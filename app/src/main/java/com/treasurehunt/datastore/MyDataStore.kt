package com.treasurehunt.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.treasurehunt.App

class MyDataStore {

    private val context = App.context()

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_pref")

    }

    private val myDataStore: DataStore<Preferences> = context.dataStore

    private val First_FALG = booleanPreferencesKey("First_FLAG")

    suspend fun setFirstData() {
        myDataStore.edit { preferences ->
            preferences[First_FALG] = true
        }
    }

    suspend fun getFirstData(): Boolean {

        var currentValue = false

        myDataStore.edit { preferences ->
            currentValue = preferences[First_FALG] ?: false
        }

        return currentValue
    }


}