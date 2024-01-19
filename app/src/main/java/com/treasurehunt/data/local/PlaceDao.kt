package com.treasurehunt.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.treasurehunt.data.model.PlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(place: PlaceEntity)

    @Query("SELECT * from places WHERE uid = :id")
    fun getPlaceById(id: String): Flow<PlaceEntity>

    @Query("SELECT * from places WHERE `plan` = 0")
    fun getAllPlaces(): Flow<List<PlaceEntity>>

    @Query("SELECT * from places WHERE `plan` = 1")
    fun getAllPlans(): Flow<List<PlaceEntity>>

    @Delete
    suspend fun delete(place: PlaceEntity)
}