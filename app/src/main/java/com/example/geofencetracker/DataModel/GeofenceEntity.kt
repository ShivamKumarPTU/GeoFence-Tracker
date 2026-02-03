package com.example.geofencetracker.DataModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="geofences")
data class GeofenceEntity(
    @PrimaryKey
    val geofenceId:String,
    val name:String,
    val latitude:Double,
    val longitude:Double,
    val radius:Int,
    val createdAt:Long
)
