package com.example.lendit

import Converters
import EquipmentListing
import ListingCategory
import ListingStatus
import Region
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lendit.databinding.ActivityListingCreation2Binding
import com.example.lendit.databinding.ActivityListingCreationBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListingActivity : AppCompatActivity() {

    private lateinit var bindingStep1: ActivityListingCreationBinding
    private lateinit var bindingStep2: ActivityListingCreation2Binding
    private var currentStep = 1
    private val selectedImageUris = mutableListOf<Uri>()
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var userName: String

    private var formattedStart: Int? = null
    private var formattedEnd: Int? = null
    private var selectedRegion: Region? = null

    // Temporary storage for listing data between steps
    private var listingData = HashMap<String, Any>()

    // For edit mode
    private var editingListingId: Int = -1
    private var isEditMode = false

    // Add this variable to store the discount
    private var selectedDiscount: Double = 0.0

    // Image picker result
    private val getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { uri ->
                        if (selectedImageUris.size < 5) {
                            selectedImageUris.add(uri)
                            updatePhotosList()
                        } else {
                            Toast.makeText(
                                            this,
                                            "Μέγιστος αριθμός φωτογραφιών: 5",
                                            Toast.LENGTH_SHORT
                                    )
                                    .show()
                        }
                    }
                }
            }

    // Permission request
    private val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    openImagePicker()
                } else {
                    Toast.makeText(this, "Η άδεια απορρίφθηκε", Toast.LENGTH_SHORT).show()
                }
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if we're editing an existing listing
        editingListingId = intent.getIntExtra("edit_listing_id", -1)
        isEditMode = editingListingId != -1

        // Get the user Name to properly save it
        val sharedPref: SharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userName = sharedPref.getString("userName", "unknown").toString()

        if (isEditMode) {
            // Load the existing listing data
            loadExistingListing(editingListingId)
        } else {
            // Normal flow for new listing
            showStep1()
        }
    }

    private fun loadExistingListing(listingId: Int) {
        lifecycleScope.launch {
            try {
                val db = AppDatabase.getInstance(this@ListingActivity)
                val listing = db.listingDao().getListingById(listingId)

                if (listing != null) {
                    // Pre-populate the form with existing data
                    showStep1WithData(listing)
                } else {
                    Toast.makeText(
                                    this@ListingActivity,
                                    "Η αγγελία δεν βρέθηκε",
                                    Toast.LENGTH_SHORT
                            )
                            .show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ListingActivity, "Σφάλμα: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                finish()
            }
        }
    }

    private fun showStep1WithData(listing: EquipmentListing) {
        showStep1()

        // Populate the form fields with the existing data
        bindingStep1.toolNameField.setText(listing.title)
        bindingStep1.descriptionField.setText(listing.description)
        bindingStep1.priceField.setText(listing.price.toString())

        // Set category dropdown
        when (listing.category) {
            ListingCategory.ELECTRIC -> bindingStep1.dropdownCategoryListing.setText("Ηλεκτρικό")
            ListingCategory.MANUAL -> bindingStep1.dropdownCategoryListing.setText("Χειροκίνητο")
        }

        // Update button text
        bindingStep1.buttonPostListing.text = "Ενημέρωση"
    }

    private fun showStep1() {
        bindingStep1 = ActivityListingCreationBinding.inflate(layoutInflater)
        setContentView(bindingStep1.root)

        // Set up category dropdown
        val categories = arrayOf("Ηλεκτρικό", "Χειροκίνητο")
        val categoryAdapter =
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        bindingStep1.dropdownCategoryListing.apply {
            setAdapter(categoryAdapter)
            // Set dropdown to show on click
            setOnClickListener { showDropDown() }
            // Prevent editing
            keyListener = null
            // Set hint
            hint = "Επιλέξτε κατηγορία"
        }

        /** Date Picker Listener */
        bindingStep1.dateButton.setOnClickListener {
            val constraintsBuilder =
                    CalendarConstraints.Builder()
                            .setValidator(DateValidatorPointForward.now()) // Disable past dates

            val builder =
                    MaterialDatePicker.Builder.dateRangePicker()
                            .setTitleText("Επιλέξτε εύρος ημερομηνιών")
                            .setCalendarConstraints(constraintsBuilder.build()) // Set constraints

            val picker = builder.build() // Build Date picker

            picker.show(supportFragmentManager, "DATE_PICKER")

            picker.addOnPositiveButtonClickListener { selection ->
                val startDate = selection.first // Long timestamp in ms
                val endDate = selection.second // Long timestamp in ms

                // Convert Long timestamp -> LocalDateTime
                val startLocalDateTime =
                        Instant.ofEpochMilli(startDate).atZone(ZoneId.systemDefault()).toLocalDate()
                val endLocalDateTime =
                        Instant.ofEpochMilli(endDate).atZone(ZoneId.systemDefault()).toLocalDate()

                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val startDisplay = formatter.format(Date(startDate))
                val endDisplay = formatter.format(Date(endDate))

                // Format LocalDateTime -> String using formatter
                formattedStart = Converters().fromLocalDate(startLocalDateTime)
                formattedEnd = Converters().fromLocalDate(endLocalDateTime)

                // ISO_LOCAL_DATE_TIME strings to use
                bindingStep1.dateButton.text = "$startDisplay - $endDisplay"
            }
        }

        bindingStep1.regionSelectorButton.setOnClickListener {
            val regionsArray = Region.entries.map { it.name.replace('_', ' ') }.toTypedArray()
            // Replace underscores with spaces for better UI

            AlertDialog.Builder(this)
                    .setTitle("Επιλέξτε Περιοχή")
                    .setItems(regionsArray) { _, which ->
                        selectedRegion = Region.values()[which]
                        bindingStep1.regionSelectorButton.text =
                                selectedRegion?.name?.replace('_', ' ') ?: "Επιλέξτε Περιοχή"
                    }
                    .show()
        }
        // Set up back button
        bindingStep1.backButtonSignup.setOnClickListener { finish() }

        // Set up next/update button
        bindingStep1.buttonPostListing.text = if (isEditMode) "Ενημέρωση" else "Επόμενο"
        bindingStep1.buttonPostListing.setOnClickListener {
            // Validate and collect data from step 1
            if (detailsCheck()) {
                temporarySave1()
                showStep2()
            }
        }
    }

    private fun detailsCheck(): Boolean {
        val toolName = bindingStep1.toolNameField.text.toString()
        if (toolName.isBlank()) {
            Toast.makeText(this, "Παρακαλώ εισάγετε όνομα εργαλείου", Toast.LENGTH_SHORT).show()
            return false
        }

        val category = bindingStep1.dropdownCategoryListing.text.toString()
        if (category.isBlank()) {
            bindingStep1.dropdownCategoryListing.error = "Παρακαλώ επιλέξτε κατηγορία"
            Toast.makeText(this, "Παρακαλώ επιλέξτε κατηγορία", Toast.LENGTH_SHORT).show()
            return false
        }

        val rentalPeriod = bindingStep1.dateButton.text.toString()
        if (rentalPeriod == "Επιλέξτε εύρος ημερομηνιών") {
            Toast.makeText(this, "Παρακαλώ επιλέξτε περίοδο ενοικίασης", Toast.LENGTH_SHORT).show()
            return false
        }

        val price = bindingStep1.priceField.text.toString()
        if (price.isBlank() || price.toDoubleOrNull() == null) {
            Toast.makeText(this, "Παρακαλώ εισάγετε έγκυρη τιμή", Toast.LENGTH_SHORT).show()
            return false
        }
        val db = AppDatabase.getInstance(this@ListingActivity)

        lifecycleScope.launch {
            val exists =
                    db.listingDao()
                            .listingExists(
                                    title = toolName,
                                    description = bindingStep1.descriptionField.toString(),
                                    price = price.toDouble(),
                                    location = bindingStep1.regionSelectorButton.text.toString(),
                                    category = category,
                                    ownerName = userName,
                                    availableFrom = formattedStart,
                                    availableUntil = formattedEnd
                            )
            if (exists) {
                Toast.makeText(this@ListingActivity, "Το εργαλείο υπάρχει ήδη", Toast.LENGTH_SHORT)
                        .show()
            }
        }
        return true
    }

    private fun temporarySave1() {
        // Collect all data from step 1 fields
        listingData["title"] = bindingStep1.toolNameField.text.toString()
        listingData["description"] = bindingStep1.descriptionField.text.toString() // Add this

        // Get selected category from dropdown
        val categoryText = bindingStep1.dropdownCategoryListing.text.toString()
        listingData["category"] =
                when (categoryText) {
                    "Ηλεκτρικό" -> ListingCategory.ELECTRIC
                    "Χειροκίνητο" -> ListingCategory.MANUAL
                    else -> ListingCategory.MANUAL // Default value
                }

        // Location data
        listingData["location"] = bindingStep1.regionSelectorButton.text

        // Price data
        listingData["price"] = bindingStep1.priceField.text.toString().toDoubleOrNull() ?: 0.0

        // Rental period
        listingData["startDate"] = formattedStart as Any
        listingData["endDate"] = formattedEnd as Any
    }

    private fun showStep2() {
        currentStep = 2
        bindingStep2 = ActivityListingCreation2Binding.inflate(layoutInflater)
        setContentView(bindingStep2.root)

        // If in edit mode and first time, prepopulate usage instructions and photos
        if (isEditMode && listingData.isNotEmpty()) {
            // Usage instructions
            val usageInstructions = listingData["usageInstructions"] as? String
            if (usageInstructions != null) {
                bindingStep2.usageInstructionsInput.setText(usageInstructions)
            }
            // Photos
            val photoUris = listingData["photoUris"] as? List<String>
            if (photoUris != null) {
                selectedImageUris.clear()
                selectedImageUris.addAll(photoUris.map { Uri.parse(it) })
            }
        }

        // Initialize the RecyclerView and adapter
        setupPhotosRecyclerView()

        // Set up back button
        bindingStep2.backButtonListingStep2.setOnClickListener { showStep1() }

        // Set up add photo button
        bindingStep2.addPhotoCard.setOnClickListener { checkPermissionAndOpenPicker() }

        // Set up publish/update button
        bindingStep2.publishListingButton.text = if (isEditMode) "Ενημέρωση" else "Δημοσίευση"
        bindingStep2.publishListingButton.setOnClickListener {
            if (validateStep2()) {
                temporarySave2()

                // Check for duplicates before proceeding to querySale()
                checkForDuplicatesAndProceed()
            }
        }
    }

    private fun checkForDuplicatesAndProceed() {
        lifecycleScope.launch {
            try {
                val db = AppDatabase.getInstance(this@ListingActivity)

                // Skip duplicate check in edit mode
                if (isEditMode) {
                    querySale()
                    return@launch
                }

                val title = listingData["title"] as String
                val description = listingData["description"] as String
                val price = listingData["price"] as Double
                val location = (listingData["location"] as String).toString()
                val category = (listingData["category"] as ListingCategory).name
                val startDate = listingData["startDate"] as Int?
                val endDate = listingData["endDate"] as Int?

                val exists =
                        db.listingDao()
                                .listingExists(
                                        title = title,
                                        description = description,
                                        price = price,
                                        location = location,
                                        category = category,
                                        ownerName = userName,
                                        availableFrom = startDate,
                                        availableUntil = endDate
                                )

                if (exists) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                                        this@ListingActivity,
                                        "Έχετε ήδη δημοσιεύσει παρόμοια αγγελία. Δεν μπορείτε να δημοσιεύσετε διπλότυπες αγγελίες.",
                                        Toast.LENGTH_LONG
                                )
                                .show()
                    }
                } else {
                    // No duplicates found, proceed with sale query
                    querySale()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                                    this@ListingActivity,
                                    "Σφάλμα κατά τον έλεγχο διπλότυπων: ${e.message}",
                                    Toast.LENGTH_SHORT
                            )
                            .show()
                    // Proceed anyway in case of error
                    querySale()
                }
            }
        }
    }

    // Update the querySale() method to publish the listing after selecting a discount
    private fun querySale() {
        val discountOptions = (0..100 step 5).map { "$it%" }.toTypedArray()

        AlertDialog.Builder(this)
                .setTitle("Επιλέξτε Ποσοστό Έκπτωσης")
                .setItems(discountOptions) { _, which ->
                    val discount = which * 5 // 0%, 5%, 10%, etc.
                    selectedDiscount = discount / 100.0

                    // Now publish the listing with the selected discount
                    SaveListingandSale()
                }
                .setCancelable(false) // Prevent dismissing without selection
                .show()
    }

    private fun validateStep2(): Boolean {
        // Check if terms are accepted
        if (!bindingStep2.termsCheckBox.isChecked) {
            Toast.makeText(
                            this,
                            "Πρέπει να αποδεχτείτε τους όρους και προϋποθέσεις",
                            Toast.LENGTH_SHORT
                    )
                    .show()
            return false
        }

        // Check if usage instructions are filled
        if (bindingStep2.usageInstructionsInput.text.toString().isBlank()) {
            Toast.makeText(this, "Παρακαλώ συμπληρώστε τις οδηγίες χρήσης", Toast.LENGTH_SHORT)
                    .show()
            return false
        }

        // No need to check for at least one photo

        return true
    }

    private fun temporarySave2() {
        // Add usage instructions and photo URIs to listing data
        listingData["usageInstructions"] = bindingStep2.usageInstructionsInput.text.toString()
        listingData["photoUris"] = selectedImageUris.map { it.toString() }
    }

    // Rename publishListing() to SaveListingandSale()
    private fun SaveListingandSale() {
        lifecycleScope.launch {
            try {
                // Create or update EquipmentListing entity
                val listing =
                        EquipmentListing(
                                listingId = if (isEditMode) editingListingId else 0,
                                title = listingData["title"] as String,
                                description = listingData["description"] as String,
                                ownerName = userName.toString(),
                                category = listingData["category"] as ListingCategory,
                                location = parseLocation(listingData["location"] as String),
                                status = ListingStatus.AVAILABLE, // Always set to available when
                                // publishing
                                price = listingData["price"] as Double,
                                photos =
                                        Converters()
                                                .fromList(
                                                        listingData["photoUris"] as? List<String>
                                                                ?: emptyList()
                                                ),
                                creationDate =
                                        if (isEditMode) null
                                        else Converters().fromLocalDate(LocalDate.now()),
                                availableFrom = listingData["startDate"] as Int?,
                                availableUntil = listingData["endDate"] as Int?,
                                longTermDiscount =
                                        selectedDiscount // Use our selected discount value
                        )

                withContext(Dispatchers.IO) {
                    val db = AppDatabase.getInstance(this@ListingActivity)

                    if (isEditMode) {
                        // Update existing listing
                        db.listingDao().updateListing(listing)
                        android.util.Log.d(
                                "ListingActivity",
                                "Listing updated with ID: ${listing.listingId}"
                        )
                    } else {
                        // Insert new listing
                        val id = db.listingDao().insert(listing)
                        android.util.Log.d("ListingActivity", "Listing created with ID: $id")
                    }
                }

                val message =
                        if (isEditMode) "Η αγγελία ενημερώθηκε με επιτυχία!"
                        else "Η αγγελία δημοσιεύτηκε με επιτυχία!"
                Toast.makeText(this@ListingActivity, message, Toast.LENGTH_SHORT).show()

                // Show the tool after saving
                showTool()
            } catch (e: Exception) {
                android.util.Log.e("ListingActivity", "Error creating/updating listing", e)
                Toast.makeText(this@ListingActivity, "Σφάλμα: ${e.message}", Toast.LENGTH_LONG)
                        .show()
            }
        }
    }

    // Keep the showTool() implementation as before
    private fun showTool() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val db = AppDatabase.getInstance(this@ListingActivity)

                // If we're in edit mode, we already know the ID
                val listingId =
                        if (isEditMode) {
                            editingListingId
                        } else {
                            // For new listings, find the most recently created listing by this user
                            val recentListing =
                                    db.listingDao().getMostRecentListingByOwner(userName)
                            recentListing?.listingId ?: -1
                        }

                if (listingId != -1) {
                    withContext(Dispatchers.Main) {
                        // Navigate to ListingDetailsActivity to show the listing
                        val intent =
                                Intent(this@ListingActivity, ListingDetailsActivity::class.java)
                        intent.putExtra("listing_id", listingId)
                        startActivity(intent)
                        finish() // Close the creation activity
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                                        this@ListingActivity,
                                        "Couldn't find the created listing",
                                        Toast.LENGTH_SHORT
                                )
                                .show()
                        finish()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                                    this@ListingActivity,
                                    "Error showing listing: ${e.message}",
                                    Toast.LENGTH_SHORT
                            )
                            .show()
                    finish()
                }
            }
        }
    }

    private fun setupPhotosRecyclerView() {
        // Setup RecyclerView with horizontal layout
        bindingStep2.photosRecyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Initialize adapter with the remove photo callback
        photoAdapter =
                PhotoAdapter(selectedImageUris) { position ->
                    // Handle photo removal
                    if (position >= 0 && position < selectedImageUris.size) {
                        selectedImageUris.removeAt(position)
                        photoAdapter.notifyItemRemoved(position)
                        photoAdapter.notifyItemRangeChanged(
                                position,
                                selectedImageUris.size - position
                        )
                    }
                }

        // Set adapter to RecyclerView
        bindingStep2.photosRecyclerView.adapter = photoAdapter
    }

    private fun parseLocation(locationString: String): Region {
        return try {
            // Try direct match first
            Region.valueOf(locationString.uppercase().replace(" ", "_"))
        } catch (e: Exception) {
            try {
                // Try with a more flexible approach for partial matches
                val bestMatch =
                        Region.entries.find {
                            locationString.uppercase().contains(it.name.replace("_", " "))
                        }
                bestMatch ?: Region.ATTICA // Default if no match
            } catch (e: Exception) {
                Region.ATTICA // Default region
            }
        }
    }

    private fun checkPermissionAndOpenPicker() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED -> {
                openImagePicker()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                // Show permission explanation dialog
                Toast.makeText(
                                this,
                                "Απαιτείται άδεια για πρόσβαση στις φωτογραφίες",
                                Toast.LENGTH_LONG
                        )
                        .show()
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        getContent.launch(intent)
    }

    private fun updatePhotosList() {
        // Just notify the adapter about the new data
        photoAdapter.notifyDataSetChanged()
        Toast.makeText(
                        this,
                        "Προστέθηκε φωτογραφία (${selectedImageUris.size}/5)",
                        Toast.LENGTH_SHORT
                )
                .show()
    }
}
