package com.example.geofencetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.geofencetracker.DataModel.GeofenceVisitUiModel
import com.example.geofencetracker.database.AppDatabase
import com.example.geofencetracker.repository.GeofenceHistoryRepository

class GeofenceHistoryViewModel(application: Application): AndroidViewModel(application) {
    private val geofenceHistoryRepository : GeofenceHistoryRepository
    val historyUiList: LiveData<List<GeofenceVisitUiModel>>

init{
    val db = AppDatabase.getDatabase(application)
    geofenceHistoryRepository = GeofenceHistoryRepository(db.geofenceHistoryDao())
    historyUiList = geofenceHistoryRepository.getVisitUiModels().asLiveData()

}
}