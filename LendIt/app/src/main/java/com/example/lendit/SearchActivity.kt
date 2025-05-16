package com.example.lendit

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.DatePickerDialog
import androidx.fragment.app.DialogFragment
import com.example.lendit.databinding.ActivitySearchBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.slider.Slider
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate and set the layout
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Access the slider
        val distanceSlider = binding.distanceSlider

        val dateButton = findViewById<MaterialButton>(R.id.date_button)


        dateButton.setOnClickListener {
            val constraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())      // Disable past dates

            val builder = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Επιλέξτε εύρος ημερομηνιών")
                .setCalendarConstraints(constraintsBuilder.build()) // Set constraints here

            val picker = builder.build()                            // Build Date picker

            picker.show(supportFragmentManager, "DATE_PICKER")

            picker.addOnPositiveButtonClickListener { selection ->
                val startDate = selection.first
                val endDate = selection.second

                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedStart = formatter.format(Date(startDate))
                val formattedEnd = formatter.format(Date(endDate))

                // Set formatted text to button
                dateButton.text = "$formattedStart - $formattedEnd"
            }
        }


        // You can continue accessing other views like filters, togg    les, etc. here
    }
}
