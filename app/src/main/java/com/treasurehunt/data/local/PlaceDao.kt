package com.treasurehunt.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.treasurehunt.data.model.PlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(place: PlaceEntity): Long

    @Query("SELECT * from places WHERE id = :id")
    fun getPlaceById(id: String): Flow<PlaceEntity>

    @Query("SELECT * from places WHERE `plan` = 0")
    fun getAllPlaces(): Flow<List<PlaceEntity>>

    @Query("SELECT * from places WHERE `plan` = 1")
    fun getAllPlans(): Flow<List<PlaceEntity>>

    @Update
    fun update(place: PlaceEntity): Int

    @Delete
    suspend fun delete(vararg places: PlaceEntity): Int
}