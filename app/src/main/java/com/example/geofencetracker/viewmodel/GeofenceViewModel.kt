package com.example.geofencetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.geofencetracker.DataModel.GeofenceEntity
import com.example.geofencetracker.database.AppDatabase
import com.example.geofencetracker.repository.GeofenceRepository
import com.google.android.gms.location.Geofence
import kotlinx.coroutines.launch

class GeofenceViewModel(application: Application) : AndroidViewModel(application) {

    private val geofenceRepository: GeofenceRepository

    val geofences: LiveData<List<GeofenceEntity>>

    init {
        val db = AppDatabase.getDatabase(application)
        geofenceRepository = GeofenceRepository(db)
        geofences = geofenceRepository.getAllGeofences().asLiveData()
    }

    fun addGeofence(geofence: GeofenceEntity) {
        viewModelScope.launch {
            geofenceRepository.addGeofence(geofence)
        }
    }

    fun deleteGeofence(geofence: GeofenceEntity) {
        viewModelScope.launch {
            geofenceRepository.deleteGeofence(geofence)
        }
    }

    fun updateGeofence(geofence: GeofenceEntity) {
        viewModelScope.launch {
            geofenceRepository.updateGeofence(geofence)
        }
    }
}
