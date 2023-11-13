package com.example.fraud.activity.mainactivity


import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.fraud.R
import com.example.fraud.SharedPreferenceHelper
import com.example.fraud.activity.loginactivity.LoginActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var mainViewModel: MainViewModel
    private lateinit var sharedPreferencesHelper: SharedPreferenceHelper

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel = MainViewModel(this)
//initialize
        sharedPreferencesHelper = SharedPreferenceHelper(this)

//check if already logged in or not
        if (!sharedPreferencesHelper.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

//intent from LoginActivity
        var accountNumber = intent.getStringExtra("accountNumber")
        if (accountNumber.isNullOrEmpty()) {
            accountNumber = sharedPreferencesHelper.getAccount()
        }
        if (accountNumber.isNullOrEmpty()) {
            Toast.makeText(this, "No account number found", Toast.LENGTH_LONG).show()
        } else {
            sharedPreferencesHelper.setAccount(accountNumber)
//            Toast.makeText(this, accountNumber, Toast.LENGTH_LONG).show()
        }
        mainViewModel.getCustomerDetails(accountNumber.toString())

        //token for FCM
        getToken()

//        nav controller
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_main) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        // Set up the ActionBar with the NavController and enable the back arrow
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = "Token:$token"
            Log.d(TAG, msg)
            Firebase.database.getReference("CustomerAccount")
                .child(sharedPreferencesHelper.getAccount().toString()).child("token")
                .setValue(token)
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                // Handle the logout action here
                performLogout()
                true
            }
            R.id.profile -> {
                navController.navigate(R.id.profileFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun performLogout() {
        val sharedPreferencesHelper = SharedPreferenceHelper(this)
        sharedPreferencesHelper.setLoggedIn(false)
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
