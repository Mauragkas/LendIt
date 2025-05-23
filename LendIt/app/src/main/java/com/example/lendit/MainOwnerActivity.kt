package com.example.lendit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.lendit.databinding.ActivityMainOwnerBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainOwnerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainOwnerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainOwnerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        navView.setupWithNavController(navController)

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_new_listing -> {
                    val intent = Intent(this, ListingActivity::class.java)
                    startActivity(intent)
                    false // Do not select this item in the navbar
                }
                else -> {
                    // Let the Navigation Component handle the rest
                    NavigationUI.onNavDestinationSelected(item, navController)
                    true
                }
            }
        }
    }
}