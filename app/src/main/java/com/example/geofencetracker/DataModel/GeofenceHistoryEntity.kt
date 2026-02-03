package com.example.geofencetracker.DataModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "geofence_history")
data class GeofenceHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id :Int=0,
    val geofenceId:String,
    val geofenceName:String,
    val transition:String, // Enter or Exit
    val timestamp:Long
)
