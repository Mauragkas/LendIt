package com.example.lendit

import Converters
import ListingCategory
import ListingFilters
import Region
import SortBy
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.lendit.databinding.ActivitySearchBinding
import com.example.lendit.ui.show_listings.ShowListingsFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import java.util.Locale


class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private var formattedStart: Int? = null
    private var formattedEnd: Int? = null
    private var selectedRegion: Region? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Now safe to access binding:
        val dateButton = binding.dateButton
        val filterButton = binding.toggleFiltersButton
        val filtersContainer = binding.filtersContainer
        val regionSelectorButton = binding.regionSelectorButton
        val applyFiltersButton = binding.applyFiltersButton
        val clearFiltersButton = binding.clearFiltersButton
        // Use these local variables or just use binding.* directly


        binding.searchEditText.requestFocus()
        binding.searchEditText.post {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.showSoftInput(binding.searchEditText, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
        }

        // Handle Enter key in searchEditText
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                startSearch(insertFilters())
                true
            } else {
                false
            }
        }

        // Get dates
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

        // Get region
        regionSelectorButton.setOnClickListener {
            val regionsArray = Region.entries.map { it.name.replace('_', ' ') }.toTypedArray()
            // Replace underscores with spaces for better UI

            AlertDialog.Builder(this)
                .setTitle("Επιλέξτε Περιοχή")
                .setItems(regionsArray) { _, which ->
                    selectedRegion = Region.values()[which]
                    regionSelectorButton.text = selectedRegion?.name?.replace('_', ' ') ?: "Επιλέξτε Περιοχή"
                }
                .show()
        }

        // Toggle filters visibility
        filterButton.setOnClickListener {
            if (filtersContainer.isVisible)
                filtersContainer.visibility = View.GONE
            else
                filtersContainer.visibility = View.VISIBLE
        }

        // Apply filters
        applyFiltersButton.setOnClickListener {
            startSearch(insertFilters())
            filtersContainer.visibility = View.GONE
        }

        // Clear Filters
        clearFiltersButton.setOnClickListener {
            // Clear text inputs
            binding.searchEditText.text?.clear()
            binding.priceFromEditText.text?.clear()
            binding.priceToEditText.text?.clear()

            // Clear radio selection
            binding.toolTypeGroup.clearCheck()

            // Reset region
            selectedRegion = null
            binding.regionSelectorButton.text = "Επιλέξτε Περιοχή"

            // Reset date range
            formattedStart = null
            formattedEnd = null
            binding.dateButton.text = "Επιλογή ημερομηνιών"
        }
    }

    private fun insertFilters(): ListingFilters {
        var category: ListingCategory? = null
        var sortBy: SortBy? = null

        if (binding.toolTypeGroup.checkedRadioButtonId == R.id.radioManual)
            category = ListingCategory.MANUAL
        else if (binding.toolTypeGroup.checkedRadioButtonId == R.id.radioElectric)
            category = ListingCategory.ELECTRIC

        if (binding.priceSortGroup.checkedRadioButtonId == R.id.radioAscending)
            sortBy = SortBy.ASC
        else if (binding.priceSortGroup.checkedRadioButtonId == R.id.radioDescending)
            sortBy = SortBy.DESC
        else if (binding.priceSortGroup.checkedRadioButtonId == R.id.radioSuggested)
            sortBy = SortBy.SUGGESTED

        return ListingFilters(
            title = binding.searchEditText.text.toString(),
            minPrice = binding.priceFromEditText.text.toString().toDoubleOrNull(),
            maxPrice = binding.priceToEditText.text.toString().toDoubleOrNull(),
            location = selectedRegion,
            category = category,
            availableFrom = formattedStart,
            availableUntil = formattedEnd,
            sortBy = sortBy
        )
    }



    private fun startSearch(filters: ListingFilters = ListingFilters()) {
        binding.filterButtonContainer.visibility = View.VISIBLE

        val fragment = ShowListingsFragment().apply {
            arguments = Bundle().apply {
                putParcelable("filters", filters)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.listingFragment, fragment)
            .commit()
    }

}
