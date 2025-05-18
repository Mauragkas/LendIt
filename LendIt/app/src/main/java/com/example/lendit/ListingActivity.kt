package com.example.lendit

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class ListingActivity : AppCompatActivity() {
    val dropdown = findViewById<AutoCompleteTextView>(R.id.dropdown)
    val emptyAdapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, listOf())
    dropdown.setAdapter(emptyAdapter)

}
