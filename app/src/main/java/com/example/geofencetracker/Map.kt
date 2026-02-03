package com.example.geofencetracker

//import com.example.geofencetracker.databinding.ActivityMapsBinding
import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.geofencetracker.DataModel.GeofenceEntity
import com.example.geofencetracker.databinding.FragmentMapBinding
import com.example.geofencetracker.receiver.GeofenceReceiver
import com.example.geofencetracker.viewmodel.GeofenceViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.UUID


class Map : Fragment(), OnMapReadyCallback {

    private lateinit var binding :FragmentMapBinding
    private val markerCircleMap = mutableMapOf<Marker, Circle>()

    private val geofenceViewModel: GeofenceViewModel by viewModels()
    private val BACKGROUND_LOCATION_REQUEST_CODE =102
    private val NOTIFICATION_PERMISSION_REQUEST_CODE =103

    private lateinit var geofencingClient: GeofencingClient
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(
            "PERMISSION_CHECK",
            "Background location granted = ${
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                else true
            }"
        )

        requestNotificationPermission()
        createNotificationChannel()
        // Getting current location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        requestLocationPermission()
        // Initialising Geofencing client
        geofencingClient = LocationServices.getGeofencingClient(requireContext())

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
          mapFragment.getMapAsync(this)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        // mMap.isTrafficEnabled = true
        mMap.isBuildingsEnabled = true
        mMap.isIndoorEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.apply {
            isZoomGesturesEnabled = true
            isScrollGesturesEnabled = true
            isCompassEnabled = true
            isZoomControlsEnabled = true
            isRotateGesturesEnabled = true
            isTiltGesturesEnabled = true
            isMyLocationButtonEnabled = true
            // isMapToolbarEnabled=true
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
        }
        // 🔥 THIS IS THE KEY FIX
        // ================= REAL OBSERVER (COMMENTED) =================s
        geofenceViewModel.geofences.observe(viewLifecycleOwner) { geofences ->
            mMap.clear()
            geofences.forEach { drawGeofenceOnMap(it) }
        }

// =============================================================
/*
// 🔥 DUMMY GEOFENCES FOR DEMO
        val dummyLocations = listOf(
            LatLng(28.6139, 77.2090) to "Home",
            LatLng(28.5355, 77.3910) to "Office",
            LatLng(28.4595, 77.0266) to "Gym",
            LatLng(28.7041, 77.1025) to "College"
        )

        dummyLocations.forEach { (latLng, name) ->
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(name)
                    .snippet("Radius: 50m")
            )

            mMap.addCircle(
                CircleOptions()
                    .center(latLng)
                    .radius(50.0)
                    .strokeColor(Color.BLUE)
                    .fillColor(0x220000FF)
            )
        }
        */


     //   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(12f)

        mMap.setOnMapLongClickListener  { latLng ->
            showGeofencNameDialog(latLng)
        }
        if(ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==PackageManager.PERMISSION_GRANTED)
            enableMyLocation()
    }
    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                )
            }
        }
    }
    private fun requestLocationPermission(){
        if(ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        )
          requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    if(::mMap.isInitialized){
                        enableMyLocation()
                    }
                    requestBackgroundLocationPermission()
                } else {
                    Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }

            BACKGROUND_LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(requireContext(), "Background location enabled", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Background location is required for geofencing",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            NOTIFICATION_PERMISSION_REQUEST_CODE ->{
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(
                        requireContext(),
                        "Notification permission is required to show geofence alerts",
                        Toast.LENGTH_LONG
                    ).show(
                    )
                }
            }
        }

    }


    private val geofencePendingIntent : PendingIntent by lazy{
        val intent = Intent(requireContext(), GeofenceReceiver::class.java)
        PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    // Providing notification when geofence are getting added
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "GEOFENCE_CHANNEL",
            "Geofence Alerts",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = requireContext().getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    // Request Background Location Permission
    private fun requestBackgroundLocationPermission(){
        if(ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        {
           requestPermissions(
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                BACKGROUND_LOCATION_REQUEST_CODE
            )
        }
    }
    private fun showGeofencNameDialog(latLng:LatLng){
    val dialogView = layoutInflater.inflate(
        R.layout.dialog_add_geofence,
        null
    )
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()
        dialog.show()

        dialogView.findViewById<Button>(R.id.btnAdd).setOnClickListener {
            val geofenceName = dialogView.findViewById<EditText>(R.id.etGeofenceName).text.toString()
        if(geofenceName.isEmpty()){
            Toast.makeText(requireContext(),"Geofence name cannot be empty",Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }
            val radius = dialogView.findViewById<EditText>(R.id.etRadius).text.toString().toFloat()
            if(radius<10 || radius>50){
                Toast.makeText(requireContext(),"Radius must be between 10 and 50",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            addGeofenceWithName(latLng,geofenceName,radius)
            dialog.dismiss()
        }
        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
    private fun addGeofenceWithName(latLng: LatLng, geofenceName: String,radius: Float) {
        val marker=    mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(geofenceName)
                .snippet("Radius: ${radius.toInt()}m")
        )
        val circle =   mMap.addCircle(
            CircleOptions()
                .center(latLng)
                .radius(radius.toDouble())
                .strokeColor(Color.BLUE)
                .fillColor(0x220000FF)
        )

        if(marker !=null){
            markerCircleMap[marker] = circle
        }
        val geofence = com.google.android.gms.location.Geofence.Builder()
            .setRequestId(geofenceName)
            .setCircularRegion(
                latLng.latitude,
                latLng.longitude,
                radius
            )


            .setExpirationDuration(com.google.android.gms.location.Geofence.NEVER_EXPIRE)
            .setTransitionTypes(com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val geofenceRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER or GeofencingRequest.INITIAL_TRIGGER_EXIT)
            .addGeofence(geofence)
            .build()
       geofenceViewModel.addGeofence(
           GeofenceEntity(
               geofenceId = UUID.randomUUID().toString(),
               name = geofenceName,
               latitude = latLng.latitude,
               longitude = latLng.longitude,
               radius = radius.toInt(),
               createdAt = System.currentTimeMillis()
           )
       )
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ){
            Toast.makeText(requireContext(),"Background location permissioin is needed for geofencing",Toast.LENGTH_SHORT).show()
            requestBackgroundLocationPermission()
            return
        }
        geofencingClient.addGeofences(geofenceRequest, geofencePendingIntent).run{
            addOnSuccessListener {
                Toast.makeText(requireContext(),"$geofenceName added",Toast.LENGTH_SHORT).show()
            }
            addOnFailureListener {
                Toast.makeText(requireContext() ,"Failed to add Geofence",Toast.LENGTH_SHORT).show()
            }
        }

    }
    // Notification Permission
    private fun requestNotificationPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){ // TIRAMISU IS ANDROID 13
            if(ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                )!= PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }
    private fun drawGeofenceOnMap(geofence: GeofenceEntity) {
        val latLng = LatLng(geofence.latitude, geofence.longitude)

        val marker = mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(geofence.name)
                .snippet("Radius: ${geofence.radius}m")
        )

        val circle = mMap.addCircle(
            CircleOptions()
                .center(latLng)
                .radius(geofence.radius.toDouble())
                .strokeColor(Color.BLUE)
                .fillColor(0x220000FF)
        )

        if (marker != null) {
            markerCircleMap[marker] = circle
        }
    }

}