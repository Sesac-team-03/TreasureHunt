package com.treasurehunt.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.treasurehunt.data.local.model.PlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(place: PlaceEntity): Long

    @Query("SELECT * from places WHERE localId = :id")
    fun getLocalPlaceById(id: String): Flow<PlaceEntity>

    @Query("SELECT * from places WHERE isPlan = 0")
    fun getAllLocalVisits(): Flow<List<PlaceEntity>>

    @Query("SELECT * from places WHERE isPlan = 1")
    fun getAllLocalPlans(): Flow<List<PlaceEntity>>

    @Update
    suspend fun update(place: PlaceEntity): Int

    @Delete
    suspend fun delete(vararg places: PlaceEntity): Int

    @Query("DELETE FROM places")
    suspend fun deleteAllLocalPlaces()
}