package com.example.geofencetracker.repository

import com.example.geofencetracker.DataModel.GeofenceHistoryEntity
import com.example.geofencetracker.DataModel.GeofenceVisitUiModel
import com.example.geofencetracker.dao.GeofenceHistoryDao
import com.example.geofencetracker.database.AppDatabase
import kotlinx.coroutines.flow.map

class GeofenceHistoryRepository(
    private val geofenceHistoryDao: GeofenceHistoryDao
) {

    // 🔹 Public API for ViewModel
    fun getVisitUiModels(): kotlinx.coroutines.flow.Flow<List<GeofenceVisitUiModel>> {
        return geofenceHistoryDao
            .getAllHistoryFlow()
            .map { events ->
                mapToUi(events)
            }
    }

    suspend fun insertHistory(history: GeofenceHistoryEntity) {
        geofenceHistoryDao.insertHistory(history)
    }

    // 🔹 CORE LOGIC: ENTRY → EXIT → DURATION
    private fun mapToUi(
        events: List<GeofenceHistoryEntity>
    ): List<GeofenceVisitUiModel> {

        val visits = mutableListOf<GeofenceVisitUiModel>()
        val entryMap = mutableMapOf<String, Long>()

        for (event in events) {
            when (event.transition) {
                "Enter" -> {
                    entryMap[event.geofenceId] = event.timestamp
                }

                "Exit" -> {
                    val entryTime = entryMap[event.geofenceId] ?: continue
                    val duration = event.timestamp - entryTime

                    visits.add(
                        GeofenceVisitUiModel(
                            geofenceId = event.geofenceId,
                            visitDate = entryTime ,
                            geofenceName = event.geofenceName,
                            entryTime = entryTime,
                            exitTime = event.timestamp,
                            durationMillis = duration
                        )
                    )

                    entryMap.remove(event.geofenceId)
                }
            }
        }
        return visits
    }
}
