package com.example.lendit

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.lendit.databinding.ActivityReportDetailsBinding
import kotlinx.coroutines.launch

class ReportDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val reportId = intent.getIntExtra("report_id", -1)
        if (reportId == -1) {
            Toast.makeText(this, "Error: Report not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadReportDetails(reportId)

        binding.backButton.setOnClickListener { finish() }
    }

    private fun loadReportDetails(reportId: Int) {
        lifecycleScope.launch {
            try {
                val db = AppDatabase.getInstance(applicationContext)
                val report = db.reportDao().getReportById(reportId)

                if (report != null) {
                    binding.reportIdText.text = "Report #${report.reportId}"
                    binding.reportReasonText.text = report.reason
                    binding.reportCommentsText.text = report.comments
                    binding.reportStatusText.text = report.status

                    // Load the listing details
                    val listing = db.listingDao().getListingById(report.listingId)
                    if (listing != null) {
                        binding.listingTitleText.text = listing.title
                        binding.listingDescriptionText.text = listing.description
                        binding.listingOwnerText.text = "Owner: ${listing.ownerName}"
                        binding.listingStatusText.text = "Status: ${listing.status}"
                    }

                    // Handle attachments if available
                    if (!report.attachments.isNullOrEmpty()) {
                        // Here you would show attachments, but we'll skip for now
                        binding.attachmentsGroup.visibility = android.view.View.VISIBLE
                    } else {
                        binding.attachmentsGroup.visibility = android.view.View.GONE
                    }
                } else {
                    Toast.makeText(
                                    this@ReportDetailsActivity,
                                    "Report not found",
                                    Toast.LENGTH_SHORT
                            )
                            .show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(
                                this@ReportDetailsActivity,
                                "Error: ${e.message}",
                                Toast.LENGTH_SHORT
                        )
                        .show()
                finish()
            }
        }
    }
}
