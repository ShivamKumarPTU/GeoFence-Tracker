package com.example.geofencetracker.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.geofencetracker.DataModel.GeofenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GeofenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeofence(geofence: GeofenceEntity)
    @Delete
    suspend fun deleteGeofence(geofence: GeofenceEntity)
    @Query("SELECT * FROM geofences ORDER BY createdAt DESC")
    fun getAllGeofence() : Flow<List<GeofenceEntity>>

    @Query("DELETE FROM geofences WHERE geofenceId = :geofenceId")
    suspend fun deleteById(geofenceId: String)
    @Query("SELECT * FROM geofences")
    suspend fun getAllGeofencesOnce(): List<GeofenceEntity>
    @Update
    suspend fun updateGeofence(geofence: GeofenceEntity)
    @Query("SELECT * FROM geofences WHERE geofenceId = :id LIMIT 1")
    suspend fun getGeofenceById(id: String): GeofenceEntity?

}