package com.example.lendit

import Converters
import EquipmentListing
import ListingCategory
import ListingStatus
import Region
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListingActivity : AppCompatActivity() {

    private lateinit var bindingStep1: ActivityListingCreationBinding
    private lateinit var bindingStep2: ActivityListingCreation2Binding
    private var currentStep = 1
    private val selectedImageUris = mutableListOf<Uri>()
    private lateinit var photoAdapter: PhotoAdapter

    // Temporary storage for listing data between steps
    private var listingData = HashMap<String, Any>()

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
        showStep1()
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

        // Set up rental period dropdown
        val periods = arrayOf("Ώρα", "Ημέρα", "Εβδομάδα", "Μήνα")
        val periodAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, periods)
        bindingStep1.dropdownPeriodOfListing.apply {
            setAdapter(periodAdapter)
            // Set dropdown to show on click
            setOnClickListener { showDropDown() }
            // Prevent editing
            keyListener = null
            // Set hint
            hint = "Επιλέξτε περίοδο"
        }

        // Set up back button
        bindingStep1.backButtonSignup.setOnClickListener { finish() }

        // Set up next button
        bindingStep1.buttonPostListing.text = "Επόμενο"
        bindingStep1.buttonPostListing.setOnClickListener {
            // Validate and collect data from step 1
            if (validateStep1()) {
                collectDataFromStep1()
                showStep2()
            }
        }
    }

    private fun validateStep1(): Boolean {
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

        val rentalPeriod = bindingStep1.dropdownPeriodOfListing.text.toString()
        if (rentalPeriod.isBlank()) {
            bindingStep1.dropdownPeriodOfListing.error = "Παρακαλώ επιλέξτε περίοδο ενοικίασης"
            Toast.makeText(this, "Παρακαλώ επιλέξτε περίοδο ενοικίασης", Toast.LENGTH_SHORT).show()
            return false
        }

        val price = bindingStep1.priceField.text.toString()
        if (price.isBlank() || price.toDoubleOrNull() == null) {
            Toast.makeText(this, "Παρακαλώ εισάγετε έγκυρη τιμή", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun collectDataFromStep1() {
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
        listingData["location"] = bindingStep1.locationField.text.toString()

        // Price data
        listingData["price"] = bindingStep1.priceField.text.toString().toDoubleOrNull() ?: 0.0

        // Rental period
        listingData["rentalPeriod"] = bindingStep1.dropdownPeriodOfListing.text.toString()
    }

    private fun showStep2() {
        currentStep = 2
        bindingStep2 = ActivityListingCreation2Binding.inflate(layoutInflater)
        setContentView(bindingStep2.root)

        // Initialize the RecyclerView and adapter
        setupPhotosRecyclerView()

        // Set up back button
        bindingStep2.backButtonListingStep2.setOnClickListener { showStep1() }

        // Set up add photo button
        bindingStep2.addPhotoCard.setOnClickListener { checkPermissionAndOpenPicker() }

        // Set up publish button
        bindingStep2.publishListingButton.setOnClickListener {
            if (validateStep2()) {
                collectDataFromStep2()
                publishListing()
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
        return true
    }

    private fun collectDataFromStep2() {
        // Add usage instructions and photo URIs to listing data
        listingData["usageInstructions"] = bindingStep2.usageInstructionsInput.text.toString()
        listingData["photoUris"] = selectedImageUris.map { it.toString() }
    }

    private fun publishListing() {
        lifecycleScope.launch {
            try {
                // Create an EquipmentListing entity from the collected data
                val listing =
                    EquipmentListing(
                        listingId = 0, // Auto-generated
                        title = listingData["title"] as String,
                        description = listingData["description"] as String,
                        category = listingData["category"] as ListingCategory,
                        location = parseLocation(listingData["location"] as String),
                        status = ListingStatus.AVAILABLE,
                        price = listingData["price"] as Double,
                        photos =
                            Converters()
                                .fromList(
                                    listingData["photoUris"] as? List<String>
                                        ?: emptyList()
                                ),
                        creationDate = Converters().fromLocalDate(LocalDate.now()),
                        availableFrom = Converters().fromLocalDate(LocalDate.now()),
                        availableUntil =
                            Converters().fromLocalDate(LocalDate.now().plusMonths(3)),
                        longTermDiscount = 0.0
                    )

                android.util.Log.d(
                    "ListingActivity",
                    "Creating listing: ${listing.title}, Region: ${listing.location}"
                )

                // Save to database
                withContext(Dispatchers.IO) {
                    val db = AppDatabase.getLogin(this@ListingActivity, lifecycleScope)
                    val id = db.listingDao().insert(listing)
                    android.util.Log.d("ListingActivity", "Listing created with ID: $id")
                }

                Toast.makeText(
                    this@ListingActivity,
                    "Η αγγελία δημοσιεύτηκε με επιτυχία!",
                    Toast.LENGTH_LONG
                )
                    .show()
                finish()
            } catch (e: Exception) {
                android.util.Log.e("ListingActivity", "Error creating listing", e)
                Toast.makeText(this@ListingActivity, "Σφάλμα: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
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