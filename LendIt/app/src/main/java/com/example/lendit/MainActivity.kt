package com.example.lendit

import com.example.lendit.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.lendit.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment

        val navController = navHostFragment.navController

        navView.setupWithNavController(navController)

        // ✅ Set listener on the search bar
        val searchEditText = findViewById<TextInputEditText?>(R.id.searchEditTextMain)

        searchEditText.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(
                this@MainActivity,
                SearchActivity::class.java
            ) // Replace SearchActivity with your target activity
            startActivity(intent)
        })
    }
}
