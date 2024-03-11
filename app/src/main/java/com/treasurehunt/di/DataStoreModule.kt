package com.treasurehunt.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val AUTO_LOGIN = "auto_login"
private val Context.loginDataStore: DataStore<Preferences> by preferencesDataStore(name = AUTO_LOGIN)

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun provideLoginDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.loginDataStore
    }
}