package com.example.lendit

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginBtn: Button
    private lateinit var signupBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize database and DAO
        val db = AppDatabase.getLogin(applicationContext, lifecycleScope)
        val userDao = db.userDao()

        // Check if user is already logged in
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPref.getInt("userId", -1)
        val savedUserType = sharedPref.getString("userType", "renter")

        if (isLoggedIn != -1) {
            // Redirect based on saved user type
            val destination = when (savedUserType?.lowercase()) {
                "owner" -> MainOwnerActivity::class.java
//                "admin" -> AdminActivity::class.java
                else -> MainActivity::class.java
            }
            startActivity(Intent(this, destination))
            finish()
            return
        }

        // Initialize UI components
        loginBtn = findViewById(R.id.button)
        emailField = findViewById(R.id.editTextTextEmailAddress)
        passwordField = findViewById(R.id.editTextTextPassword)
        signupBtn = findViewById(R.id.signUpButtonLogin)

        // Set up text watcher for login button state
        val loginTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                loginBtn.isEnabled = emailField.text.toString().isNotBlank() &&
                        passwordField.text.toString().isNotBlank()
            }
        }

        emailField.addTextChangedListener(loginTextWatcher)
        passwordField.addTextChangedListener(loginTextWatcher)
        loginBtn.isEnabled = false

        // Login button click handler
        loginBtn.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val user = userDao.getUserByEmailAndPassword(email, password)

                    if (user != null) {
                        // Save login state and user info
                        with(sharedPref.edit()) {
                            putString("email", email)
                            putString("userType", user.userType)
<<<<<<< Updated upstream
                            putString("userName", user.name)
=======
                            putInt("userId", user.userId)
                            putString("userName", user.name)
                            // Also save premium status from database to SharedPreferences
>>>>>>> Stashed changes
                            putBoolean("isPremium", user.premiumStatus ?: false)
                            putString("premiumPlan", user.premiumPlan)
                            apply()
                        }

                        // Redirect based on user type
                        val destination = when (user.userType?.lowercase()) {
                            "owner" -> MainOwnerActivity::class.java
                            "admin" -> AdminActivity::class.java
                            else -> MainActivity::class.java
                        }

                        startActivity(Intent(this@LoginActivity, destination))
                        finish()
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@LoginActivity,
                                "Invalid email or password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login failed: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        // Signup button click handler
        signupBtn.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}
