package com.example.geofencetracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.geofencetracker.DataModel.GeofenceEntity
import com.example.geofencetracker.DataModel.GeofenceHistoryEntity
import com.example.geofencetracker.dao.GeofenceDao
import com.example.geofencetracker.dao.GeofenceHistoryDao

@Database(
    entities = [GeofenceEntity::class, GeofenceHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun geofenceDao(): GeofenceDao
    abstract fun geofenceHistoryDao(): GeofenceHistoryDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "geofence_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
