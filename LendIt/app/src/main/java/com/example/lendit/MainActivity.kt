package com.example.lendit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.lendit.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView
    private lateinit var navController: androidx.navigation.NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navView = binding.navView

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment_activity_main
        ) as NavHostFragment

        navController = navHostFragment.navController

        navView.setupWithNavController(navController)

        // Handle navigation from intent
        if (intent.getBooleanExtra("NAVIGATE_TO_FAVORITES", false)) {
            navView.selectedItemId = R.id.navigation_favorites
        }
    }

    // Handle new intents when activity is already running
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        // Check if we need to navigate to favorites
        if (intent.getBooleanExtra("NAVIGATE_TO_FAVORITES", false)) {
            navView.selectedItemId = R.id.navigation_favorites
        }
    }
}
