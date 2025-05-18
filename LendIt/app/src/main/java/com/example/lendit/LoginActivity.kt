package com.example.lendit

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginBtn: Button

    private lateinit var signupBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

       loginBtn = findViewById<Button>(R.id.button)
       emailField = findViewById<EditText>(R.id.editTextTextEmailAddress)
       passwordField = findViewById<EditText>(R.id.editTextTextPassword)

        loginBtn.isEnabled = false

        emailField.addTextChangedListener(loginTextWatcher)
        passwordField.addTextChangedListener(loginTextWatcher)

        loginBtn.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            // Replace this with real login logic
            if (true) {
                // Start main activity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Close LoginActivity
            }
        }

        signupBtn = findViewById<Button>(R.id.signUpButtonLogin)

        signupBtn.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    private val loginTextWatcher = object : TextWatcher {
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

}
