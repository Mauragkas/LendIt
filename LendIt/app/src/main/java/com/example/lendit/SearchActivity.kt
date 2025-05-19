package com.example.lendit

import Converters
import ListingCategory
import ListingFilters
import Region
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.lendit.databinding.ActivitySearchBinding
import com.example.lendit.ui.listing.ListingFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker

import androidx.core.view.isVisible
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import java.util.Locale


class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate and set the layout
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dateButton = findViewById<MaterialButton>(R.id.date_button)
        val perform_search_button = findViewById<MaterialButton>(R.id.perform_search_button)
        val filterButton = findViewById<MaterialButton>(R.id.toggleFiltersButton)
        val filtersContainer = findViewById<View>(R.id.filtersContainer)
        val regionSelectorButton = findViewById<Button>(R.id.regionSelectorButton);

        var formattedStart: Int? = null
        var formattedEnd: Int? = null
        var selectedRegion: Region? = null

        var radioGroup = findViewById<RadioGroup>(R.id.toolTypeGroup)

        dateButton.setOnClickListener {
            val constraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())      // Disable past dates

            val builder = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Επιλέξτε εύρος ημερομηνιών")
                .setCalendarConstraints(constraintsBuilder.build()) // Set constraints here

            val picker = builder.build()                            // Build Date picker

            picker.show(supportFragmentManager, "DATE_PICKER")


            picker.addOnPositiveButtonClickListener { selection ->
                val startDate = selection.first  // Long timestamp in ms
                val endDate = selection.second   // Long timestamp in ms

                // Convert Long timestamp -> LocalDateTime
                val startLocalDateTime = Instant.ofEpochMilli(startDate)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                val endLocalDateTime = Instant.ofEpochMilli(endDate)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val startDisplay = formatter.format(Date(startDate))
                val endDisplay = formatter.format(Date(endDate))

                // Format LocalDateTime -> String using your formatter
                formattedStart = Converters().fromLocalDate(startLocalDateTime)
                formattedEnd = Converters().fromLocalDate(endLocalDateTime)

                // Now you have ISO_LOCAL_DATE_TIME strings to use/store
                dateButton.text = "$startDisplay - $endDisplay"
            }

        }


        regionSelectorButton.setOnClickListener {
            val regionsArray = Region.entries.map { it.name.replace('_', ' ') }.toTypedArray()
            // Replace underscores with spaces for better UI

            AlertDialog.Builder(this)
                .setTitle("Select Region")
                .setItems(regionsArray) { _, which ->
                    selectedRegion = Region.values()[which]
                    regionSelectorButton.text = selectedRegion.name.replace('_', ' ')
                }
                .show()
        }

        filterButton.setOnClickListener {
            if (filtersContainer.isVisible)
                filtersContainer.visibility = View.GONE
            else
                filtersContainer.visibility = View.VISIBLE
        }

        perform_search_button.setOnClickListener {
            filterButton.visibility = View.VISIBLE              // Make filter button visible
            filtersContainer.visibility = View.GONE
            var category: ListingCategory? = null

            if(radioGroup.checkedRadioButtonId == R.id.radioManual)
                category = ListingCategory.MANUAL
            else if(radioGroup.checkedRadioButtonId == R.id.radioElectric)
                category = ListingCategory.ELECTRIC

            val query = ListingFilters(
                title = binding.searchEditText.text.toString(),
                minPrice = binding.priceFromEditText.text.toString().toDoubleOrNull(),
                maxPrice = binding.priceToEditText.text.toString().toDoubleOrNull(),
                location = selectedRegion,
                category = category,
                availableFrom = formattedStart,
                availableUntil = formattedEnd
            )

            val fragment = ListingFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("filters", query)
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.listingFragment, fragment)
                .commit()
            //finish()
        }
    }
}
