package com.example.geofencetracker.receiver

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.geofencetracker.DataModel.GeofenceEntity
import com.example.geofencetracker.database.AppDatabase
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        CoroutineScope(Dispatchers.IO).launch {

            val db = AppDatabase.getDatabase(context)
            val geofences = db.geofenceDao().getAllGeofencesOnce()

            val geofencingClient =
                LocationServices.getGeofencingClient(context)

            geofences.forEach { geofence ->

                val geofenceObj = Geofence.Builder()
                    .setRequestId(geofence.geofenceId)
                    .setCircularRegion(
                        geofence.latitude,
                        geofence.longitude,
                        geofence.radius.toFloat()
                    )
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_ENTER or
                                Geofence.GEOFENCE_TRANSITION_EXIT
                    )
                    .build()

                val request = GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofence(geofenceObj)
                    .build()

                if (
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED &&
                    (
                            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                    ) == PackageManager.PERMISSION_GRANTED
                            )
                ) {
                    geofencingClient.addGeofences(request, getPendingIntent(context))
                }

            }
        }
    }

    private fun getPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, GeofenceReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
