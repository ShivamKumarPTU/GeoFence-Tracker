package com.example.geofencetracker

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.geofencetracker.databinding.ActivityMainBinding
import com.example.geofencetracker.service.GeofenceForegroundService

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       enableEdgeToEdge()
        setContentView(binding.root)
        startGeofenceService()
        // Hide System bar code
        val windowInsetsController = WindowInsetsControllerCompat(window,window.decorView)
       windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior=
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Set Toolbar as ActionBar
        setSupportActionBar(binding.toolBar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.NavHostFragment) as androidx.navigation.fragment.NavHostFragment
        navController = navHostFragment.navController

        // Connect Toolbar with Navigation
        setupActionBarWithNavController(navController)

        //  SmoothBottomBar → NavController (MANUAL)
        setupBottomBar()
    }

    private fun setupBottomBar() {
        // Bottom bar → NavController

        binding.bottomBar.onItemSelected = { index ->
            when (index) {
                0 -> navController.navigate(R.id.mapFragment)
                1 -> navController.navigate(R.id.geofenceFragment)
                2 -> navController.navigate(R.id.historyFragment)
            }
        }

        // NavController → Bottom bar (keep state in sync)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomBar.itemActiveIndex = when (destination.id) {
                R.id.mapFragment -> 0
                R.id.geofenceFragment -> 1
                R.id.historyFragment -> 2
                else -> 0
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    private fun startGeofenceService() {
        val intent = Intent(this, GeofenceForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

}
