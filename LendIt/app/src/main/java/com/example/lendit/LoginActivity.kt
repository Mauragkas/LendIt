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

        val db = AppDatabase.getLogin(applicationContext, lifecycleScope)
        val userDao = db.userDao()

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

        val loginTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val email = emailField.text.toString()
                val password = passwordField.text.toString()

                // Your condition: email AND password is not blank
                val conditionMet = email.isNotBlank() && password.isNotBlank()

                // Enable or disable the button based on the condition
                loginBtn.isEnabled = conditionMet
            }
        }

        if (isLoggedIn) {
            // User is logged in, proceed to main app screen directly
            startActivity(Intent(this, MainActivity::class.java))
            finish() // close login screen
        } else {
            // Show login screen or stay on current activity
        }


       loginBtn = findViewById<Button>(R.id.button)
       emailField = findViewById<EditText>(R.id.editTextTextEmailAddress)
       passwordField = findViewById<EditText>(R.id.editTextTextPassword)

        loginBtn.isEnabled = false

        emailField.addTextChangedListener(loginTextWatcher)
        passwordField.addTextChangedListener(loginTextWatcher)

        loginBtn.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            lifecycleScope.launch {
                val user = userDao.getUserByEmailAndPassword(
                    email = email,
                    password = password
                )

                if (user != null) {
                    val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putBoolean("isLoggedIn", true)
                        putString("email", email)  //store email
                        apply()
                    }

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish() // close login screen
                    // ✅ Login success, go to next activity
                    // startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                }
                else {
                    Toast.makeText(this@LoginActivity, "Invalid Credentials!", Toast.LENGTH_SHORT).show()
                }
            }

        }

        signupBtn = findViewById<Button>(R.id.signUpButtonLogin)

        signupBtn.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}
