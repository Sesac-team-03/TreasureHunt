package com.treasurehunt.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.data.local.model.PlaceEntity


@Database(entities = [LogEntity::class, PlaceEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TreasureHuntDatabase : RoomDatabase() {
    abstract fun logDao(): LogDao
    abstract fun placeDao(): PlaceDao

    companion object {
        @Volatile
        private var INSTANCE: TreasureHuntDatabase? = null

        fun from(context: Context): TreasureHuntDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, TreasureHuntDatabase::class.java, "db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}