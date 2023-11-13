package com.example.fraud.activity.loginactivity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.fraud.R
import com.example.fraud.SharedPreferenceHelper
import com.example.fraud.activity.mainactivity.MainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var sharedPreferencesHelper: SharedPreferenceHelper
    // Use unique codes to identify the permission requests
    private val LOCATION_PERMISSION_REQUEST_CODE = 123
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 456
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Check if the app has the required permissions
        if (areLocationAndNotificationPermissionsGranted()) {
            // Permissions are already granted, proceed with your code
            // e.g., start location updates, post notifications
        } else {
            // Request permissions
            requestPermissions()
        }
        supportActionBar?.hide()
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_login) as NavHostFragment
        navController = navHostFragment.navController

        sharedPreferencesHelper = SharedPreferenceHelper(this)

        if (sharedPreferencesHelper.isLoggedIn()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        supportActionBar?.hide()
    }
    private fun areLocationAndNotificationPermissionsGranted(): Boolean {
        val locationPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManagerCompat.from(this)
                .areNotificationsEnabled()
        } else {
            // For versions below Android M, assuming notifications are always enabled
            true
        }

        return locationPermission && notificationPermission
    }

    private fun requestPermissions() {
        // Request location permission
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        // Request notification permission
        if (!areNotificationsEnabled()) {
            // Open system settings to enable notifications
            startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
        }
    }

    // Check if notifications are enabled
    private fun areNotificationsEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManagerCompat.from(this)
                .areNotificationsEnabled()
        } else {
            // For versions below Android M, assuming notifications are always enabled
            true
        }
    }

    // Override onRequestPermissionsResult to handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Location permission granted, proceed with your location-related code
                    // e.g., start location updates
                } else {
                    // Location permission denied, handle accordingly
                }
            }
            // Handle other permission requests if needed
        }
    }
}
