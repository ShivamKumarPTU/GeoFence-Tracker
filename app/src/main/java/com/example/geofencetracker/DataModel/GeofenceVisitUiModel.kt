package com.example.geofencetracker.DataModel

data class GeofenceVisitUiModel(
    val geofenceId: String,
    val geofenceName: String,
    val entryTime: Long,
    val exitTime: Long?,
    val durationMillis: Long,
    val visitDate: Long
)

