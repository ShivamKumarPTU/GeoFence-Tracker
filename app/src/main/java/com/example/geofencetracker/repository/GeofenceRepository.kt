package com.example.geofencetracker.repository

import com.example.geofencetracker.DataModel.GeofenceEntity
import com.example.geofencetracker.DataModel.GeofenceHistoryEntity
import com.example.geofencetracker.database.AppDatabase
import kotlinx.coroutines.flow.Flow

class GeofenceRepository(private val db: AppDatabase)
 {
    private val geofenceDao = db.geofenceDao()



     //Geofences
     fun getAllGeofences(): Flow<List<GeofenceEntity>> =
     geofenceDao.getAllGeofence()

     suspend fun addGeofence(geofence: GeofenceEntity){
         geofenceDao.insertGeofence(geofence)
     }
     suspend fun deleteGeofence(geofence: GeofenceEntity) {
         geofenceDao.deleteGeofence(geofence)
         //geofenceHistoryDao.deleteHistoryByGeofenceId(geofence.geofenceId)
     }
     suspend fun updateGeofence(geofence: GeofenceEntity) {
         geofenceDao.updateGeofence(geofence)
     }


}