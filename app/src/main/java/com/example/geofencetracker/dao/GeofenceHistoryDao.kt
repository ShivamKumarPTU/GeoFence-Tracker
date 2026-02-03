package com.example.geofencetracker.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.geofencetracker.DataModel.GeofenceHistoryEntity

@Dao
interface GeofenceHistoryDao {
  @Insert
  suspend fun insertHistory(history: GeofenceHistoryEntity)

  @Query("""
      SELECT * FROM geofence_history
      WHERE geofenceId = :geofenceId
      ORDER BY timestamp ASC
  """)
  suspend fun getHistoryForGeofence(geofenceId:String):List<GeofenceHistoryEntity>

  @Query("SELECT * FROM geofence_history ORDER BY timestamp DESC")
  suspend fun getAllHistory():List<GeofenceHistoryEntity>

    // 🔥 FIX IS HERE
    @Query("SELECT * FROM geofence_history ORDER BY timestamp ASC")
    fun getAllHistoryFlow(): kotlinx.coroutines.flow.Flow<List<GeofenceHistoryEntity>>
  @Query("DELETE FROM geofence_history WHERE geofenceId = :geofenceId")
  suspend fun deleteHistoryByGeofenceId(geofenceId: String)


}