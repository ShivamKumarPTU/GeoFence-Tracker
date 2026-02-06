package com.example.geofencetracker.receiver
    import android.Manifest
    import android.content.BroadcastReceiver
    import android.content.Context
    import android.content.Intent
    import android.content.pm.PackageManager
    import android.os.Build
    import android.util.Log
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.app.NotificationCompat
    import androidx.core.app.NotificationManagerCompat
    import androidx.core.content.ContextCompat
    import com.example.geofencetracker.DataModel.GeofenceHistoryEntity
    import com.example.geofencetracker.R
    import com.example.geofencetracker.database.AppDatabase
    import com.google.android.gms.location.Geofence
    import com.google.android.gms.location.GeofencingEvent
    import kotlinx.coroutines.CoroutineScope
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.launch
    import java.text.SimpleDateFormat
    import java.util.Date
    import java.util.Locale

    /**
     * 🔥 IMPORTANT
     * DEMO_MODE = true  → Dummy notifications (client demo)
     * DEMO_MODE = false → Real geofencing (production)
     */
    private const val DEMO_MODE = true

    class GeofenceReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            Log.d("GEOFENCE_DEBUG", "GeofenceReceiver triggered")
            /*
                    // =====================================================
                    // 🔥 DEMO MODE : DUMMY ENTRY / EXIT NOTIFICATIONS
                    // =====================================================
                    if (DEMO_MODE) {

                        val dummyGeofences = listOf(
                            "Home",
                            "Office",
                            "Gym",
                            "College",
                            "Market",
                            "Park",
                            "Cafe",
                            "Hospital",
                            "Mall",
                            "Station"
                        )

                        dummyGeofences.forEachIndexed { index, name ->
                            val transition = if (index % 2 == 0) "Enter" else "Exit"
                            val timestamp = System.currentTimeMillis()

                            showNotification(
                                context = context,
                                geofenceName = name,
                                transition = transition,
                                timestamp = timestamp
                            )

                            Log.d(
                                "GEOFENCE_DEBUG",
                                "DUMMY → $transition $name"
                            )
                        }

                        // ⛔ Stop here so real code below does NOT execute
                        return
                    }
            */
            // =====================================================
            // ✅ REAL GEOFENCING CODE (COMMENTED FOR NOW)
            // =====================================================


            val event = GeofencingEvent.fromIntent(intent) ?: return
            if (event.hasError()) {
                Log.e("GEOFENCE_DEBUG", "Geofencing error: ${event.errorCode}")
                return
            }

            val transition = when (event.geofenceTransition) {
                Geofence.GEOFENCE_TRANSITION_ENTER -> "Enter"
                Geofence.GEOFENCE_TRANSITION_EXIT -> "Exit"
                else -> return
            }

            val geofenceId =
                event.triggeringGeofences?.firstOrNull()?.requestId ?: return

            val timestamp = System.currentTimeMillis()

            // 🔔 Show notification immediately
            showNotification(context, geofenceId, transition, timestamp)

            // 💾 Save history asynchronously
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(context)
                val geofence = db.geofenceDao().getGeofenceById(geofenceId)
                val name = geofence?.name ?: geofenceId

                db.geofenceHistoryDao().insertHistory(
                    GeofenceHistoryEntity(
                        geofenceId = geofenceId,
                        geofenceName = name,
                        transition = transition,
                        timestamp = timestamp
                    )
                )
            }


            // =====================================================
            // END REAL CODE
            // =====================================================
        }

        /**
         * 🔔 Notification helper
         */
        private fun showNotification(
            context: Context,
            geofenceName: String,
            transition: String,
            timestamp: Long
        ) {
            if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("GEOFENCE_DEBUG", "Notification permission not granted")
                return
            }

            val time = SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            ).format(Date(timestamp))

            val message = when (transition) {
                "Enter" -> "Entered $geofenceName at $time"
                "Exit" -> "Exited $geofenceName at $time"
                else -> return
            }

            val notification = NotificationCompat.Builder(context, "GEOFENCE_CHANNEL")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Geofence Alert")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context)
                .notify(timestamp.toInt(), notification)
        }
    }
