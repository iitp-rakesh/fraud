package com.example.fraud.activity.loginactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.fraud.R

class LoginActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_login) as NavHostFragment
        navController = navHostFragment.navController
    }
    override fun onStart() {
        super.onStart()}
}
