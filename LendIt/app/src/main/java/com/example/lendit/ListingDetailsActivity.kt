package com.example.lendit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteConstraintException
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.lendit.ListingActivity
import com.example.lendit.data.local.entities.Report
import com.example.lendit.data.local.entities.UserCart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

class ListingDetailsActivity : AppCompatActivity() {

    private val selectedFiles = mutableListOf<Uri>()
    private var attachmentStatusTextRef: TextView? = null
    private var userId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing_details)

        val listingId = intent.getIntExtra("listing_id", -1)
        val db = AppDatabase.getInstance(applicationContext)

        // Set up report button click listener
        findViewById<Button>(R.id.reportButton).setOnClickListener {
            showReportDialog()
        }

        // Set up report button click listener
        findViewById<Button>(R.id.lendNowButton).setOnClickListener {
            lifecycleScope.launch {
                addToCart(listingId)
            }
        }

        lifecycleScope.launch {
            val listing = db.listingDao().getListingById(listingId)

            if (listing != null) {
                // Populate UI
                val photoList = listing.photos.split(",").filter { it.isNotEmpty() }

                // image
                if (photoList.isNotEmpty()) {
                    val imageView = findViewById<ImageView>(R.id.listingImage)

                    Glide.with(this@ListingDetailsActivity)
                        .load(photoList[0])
                        .into(imageView)
                }

                //text fields
                findViewById<TextView>(R.id.listingTitle).text = listing.title
                findViewById<TextView>(R.id.listingDescription).text = listing.description
                findViewById<TextView>(R.id.listingCategory).text = listing.category.name
                findViewById<TextView>(R.id.listingLocation).text = listing.location.name
                findViewById<TextView>(R.id.listingStatus).text = listing.status.name

                findViewById<TextView>(R.id.listingPrice).text = buildString {
                    append(listing.price)
                    append("€")
                }

                findViewById<TextView>(R.id.listingCreationDate).text = "Creation date: ${listing.creationDate}"

                findViewById<TextView>(R.id.listingCreator).text = "Owner: ${listing.ownerName}"


            } else {
                // Listing not found
                Toast.makeText(this@ListingDetailsActivity, "Listing not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }




    private fun showReportDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_report_listing, null)
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Set up spinner with report reasons
        val spinner = dialogView.findViewById<Spinner>(R.id.reportReasonSpinner)
        val reportReasons = arrayOf(
            "Παραπλανητική περιγραφή",
            "Ακατάλληλο περιεχόμενο",
            "Υπερβολική τιμή",
            "Απάτη",
            "Άλλο"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, reportReasons)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Initialize UI components
        val commentsEditText = dialogView.findViewById<EditText>(R.id.reportComments)
        val termsCheckbox = dialogView.findViewById<CheckBox>(R.id.reportTermsCheckbox)
        val attachFilesButton = dialogView.findViewById<Button>(R.id.attachFilesButton)
        attachmentStatusTextRef = dialogView.findViewById<TextView>(R.id.attachmentStatusText)

        // Set up attachment functionality
        attachFilesButton.setOnClickListener {
            // Request storage permission if needed
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_READ_STORAGE
                )
            } else {
                openFilePicker()
            }
        }

        // Add positive and negative buttons
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Υποβολή") { _, _ ->
            if (!termsCheckbox.isChecked) {
                Toast.makeText(
                    this,
                    "Πρέπει να αποδεχτείτε τους όρους για να υποβάλετε την αναφορά",
                    Toast.LENGTH_SHORT
                ).show()
                return@setButton
            }

            val reason = spinner.selectedItem.toString()
            val comments = commentsEditText.text.toString()

            // Validate content (check for inappropriate language)
            if (containsInappropriateContent(comments)) {
                Toast.makeText(
                    this,
                    "Η αναφορά περιέχει ακατάλληλη γλώσσα. Παρακαλώ αναθεωρήστε το κείμενό σας.",
                    Toast.LENGTH_LONG
                ).show()
                return@setButton
            }

            // Submit report
            submitReport(reason, comments, selectedFiles)
        }

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Άκυρο") { _, _ ->
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, REQUEST_CODE_PICK_FILES)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_FILES && resultCode == Activity.RESULT_OK) {
            selectedFiles.clear()

            // Handle multiple files
            if (data?.clipData != null) {
                val clipData = data.clipData
                for (i in 0 until clipData!!.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    selectedFiles.add(uri)
                }
            }
            // Handle single file
            else if (data?.data != null) {
                val uri = data.data
                uri?.let { selectedFiles.add(it) }
            }

            // Update dialog UI
            if (attachmentStatusTextRef != null) {
                attachmentStatusTextRef?.text = "${selectedFiles.size} αρχεία επιλέχθηκαν"
            } else {
                Toast.makeText(this, "${selectedFiles.size} αρχεία επιλέχθηκαν", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun containsInappropriateContent(text: String): Boolean {
        // Simple check for inappropriate content
        // In a real app, this would be more sophisticated
        val inappropriateWords = listOf("κακή λέξη", "βρισιά", "προσβλητικό")
        return inappropriateWords.any { text.lowercase().contains(it) }
    }

    private fun submitReport(reason: String, comments: String, attachments: List<Uri>) {
        lifecycleScope.launch {
            try {
                val listingId = intent.getIntExtra("listing_id", -1)
                if (listingId == -1) {
                    Toast.makeText(
                        this@ListingDetailsActivity,
                        "Σφάλμα: Δεν βρέθηκε η αγγελία",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                // Convert attachments to string
                val attachmentsString = attachments.joinToString(",") { it.toString() }

                // Create report object
                val report = Report(
                    reportId = 0, // Auto-generated
                    listingId = listingId,
                    reason = reason,
                    comments = comments,
                    status = "PENDING",
                    reportDate = System.currentTimeMillis(),
                    attachments = attachmentsString
                )

                // Save to database
                withContext(Dispatchers.IO) {
                    val db = AppDatabase.getInstance(applicationContext)
                    db.reportDao().insert(report)
                }

                // Show success message
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ListingDetailsActivity,
                        "Η αναφορά υποβλήθηκε με επιτυχία",
                        Toast.LENGTH_LONG
                    ).show()

                    // Show confirmation dialog
                    AlertDialog.Builder(this@ListingDetailsActivity)
                        .setTitle("Επιτυχής Υποβολή")
                        .setMessage("Η αναφορά σας έχει καταχωρηθεί και θα εξεταστεί από την ομάδα μας.")
                        .setPositiveButton("OK") { _, _ ->
                            // Navigate back to search or home screen
                            finish()
                        }
                        .show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ListingDetailsActivity,
                        "Σφάλμα κατά την υποβολή: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private suspend fun addToCart(listingId: Int) {
        try {
            // Get the user ID from SharedPreferences
            val sharedPref: SharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val userId = sharedPref.getInt("userId", -1)

            if (userId == -1) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ListingDetailsActivity, "User not logged in", Toast.LENGTH_SHORT).show()
                }
                return
            }

            // Create UserCart object
            val userCart = UserCart(
                userId = userId,
                listingId = listingId
            )

            // Save to database
            withContext(Dispatchers.IO) {
                val db = AppDatabase.getInstance(applicationContext)
                db.cartDao().insert(userCart)
            }

            // Show success toast on main thread
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ListingDetailsActivity, "Added to cart successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        }  catch (e: SQLiteConstraintException) {
            // Likely a UNIQUE constraint failed (duplicate entry)
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@ListingDetailsActivity,
                    "Item is already in cart",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
            catch (e: Exception) {
            // Show error toast on main thread
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ListingDetailsActivity, "Error adding to cart: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PICK_FILES = 123
        private const val PERMISSIONS_REQUEST_READ_STORAGE = 456
    }
}
