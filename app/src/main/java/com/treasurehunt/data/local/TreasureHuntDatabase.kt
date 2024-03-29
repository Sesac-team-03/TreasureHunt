package com.treasurehunt.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.treasurehunt.data.Converters
import com.treasurehunt.data.local.model.ImageEntity
import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.data.local.model.PlaceEntity
import com.treasurehunt.data.local.model.UserEntity

@Database(
    entities = [LogEntity::class, PlaceEntity::class, UserEntity::class, ImageEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TreasureHuntDatabase : RoomDatabase() {

    abstract fun logDao(): LogDao

    abstract fun placeDao(): PlaceDao

    abstract fun userDao(): UserDao

    abstract fun imageDao(): ImageDao

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