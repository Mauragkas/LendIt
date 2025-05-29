package com.example.lendit

import Converters
import EquipmentListing
import ListingCategory
import ListingFilters
import Region
import SortBy
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.lendit.databinding.ActivitySearchBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding


    private var formattedStart: Int? = null
    private var formattedEnd: Int? = null
    private var selectedRegion: Region? = null
    private lateinit var adapter: ListingAdapter
    private var allListings: List<EquipmentListing> = emptyList() // Store all fetched listings
    private var listings: List<EquipmentListing> = emptyList()

    /**
     * Initializes the activity. Sets up UI, event listeners for search, date,
     * region, filters, and sort. Prepares RecyclerView for search results.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dateButton = binding.dateButton
        val filterButton = binding.toggleFiltersButton
        val filtersContainer = binding.filtersContainer
        val regionSelectorButton = binding.regionSelectorButton
        val applyFiltersButton = binding.applyFiltersButton
        val clearFiltersButton = binding.clearFiltersButton



        /** Initialize RecyclerView */
        adapter = ListingAdapter(mutableListOf())
        binding.listingRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = this@SearchActivity.adapter
        }

        /** Handle Enter key in searchEditText */
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                startSearch()
                true
            } else {
                false
            }
        }

        /** Price Sort Radio Group Listener*/
        binding.priceSortGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioAscending, R.id.radioDescending, R.id.radioSuggested -> getFilters()
            }
        }

        /** Date Picker Listener */
        dateButton.setOnClickListener {
            val constraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())      // Disable past dates

            val builder = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Επιλέξτε εύρος ημερομηνιών")
                .setCalendarConstraints(constraintsBuilder.build()) // Set constraints

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

                // Format LocalDateTime -> String using formatter
                formattedStart = Converters().fromLocalDate(startLocalDateTime)
                formattedEnd = Converters().fromLocalDate(endLocalDateTime)

                // ISO_LOCAL_DATE_TIME strings to use
                dateButton.text = "$startDisplay - $endDisplay"
            }
        }

        /** Region Selector Listener */
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

        /** Filter Button Toggle Listener */
        filterButton.setOnClickListener {
            if (filtersContainer.isVisible)
                filtersContainer.visibility = View.GONE
            else
                filtersContainer.visibility = View.VISIBLE
        }

        /** Apply Filters Button Listener */
        applyFiltersButton.setOnClickListener {
            insertFilters()
            filtersContainer.visibility = View.GONE
        }

        /** Clear Filters Button Listener */
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

    private fun startSearch() {
        val searchTerm = binding.searchEditText.text.toString()
        if (searchTerm.isBlank()) {
            Toast.makeText(this, "Please enter a search term.", Toast.LENGTH_SHORT).show()
            allListings = emptyList()
            listings = emptyList()
            adapter.update(listings)
            binding.filterButtonContainer.visibility = View.GONE
            return
        }

        lifecycleScope.launch {
            try {
                // Only filter by title in the initial DB query
                val initialFilters = ListingFilters(title = searchTerm)
                allListings = withContext(Dispatchers.IO) {
                    AppDatabase.getListings(applicationContext, initialFilters)
                }
                insertFilters() // Apply all other filters to the fetched list
                binding.filterButtonContainer.visibility = View.VISIBLE
            } catch (e: Exception) {
                Toast.makeText(this@SearchActivity, "Failed to fetch listings.", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun getFilters(): ListingFilters {
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

    private fun insertFilters() {
        val filters = getFilters()

        // Start with all listings fetched based on the initial title search
        var filteredList = allListings

        // Filter by min price
        filters.minPrice?.let { min ->
            filteredList = filteredList.filter { it.price >= min }
        }

        // Filter by max price
        filters.maxPrice?.let { max ->
            filteredList = filteredList.filter { it.price <= max }
        }

        // Filter by location
        filters.location?.let { region ->
            filteredList = filteredList.filter { it.location == region }
        }

        // Filter by category
        filters.category?.let { category ->
            filteredList = filteredList.filter { it.category == category }
        }

        // TODO: Implement date filtering from formattedStart and formattedEnd

        // Sort the filtered list
        filteredList = when (filters.sortBy) {
            SortBy.ASC -> filteredList.sortedBy { it.price }
            SortBy.DESC -> filteredList.sortedByDescending { it.price }
            else -> filteredList
        }

        listings = filteredList
        adapter.update(listings)
        if (listings.isEmpty()) {
            // informUser()
            Toast.makeText(this@SearchActivity, "No listings match your filters.", Toast.LENGTH_SHORT).show()
        }
    }
}
