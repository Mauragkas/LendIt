package com.example.lendit.ui.report

import AppDatabase
import ListingStatus
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.lendit.ListingDetailsActivity
import com.example.lendit.R
import com.example.lendit.data.local.entities.Report
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReportAdapter(
        private val reports: MutableList<Report>,
        private val scope: CoroutineScope,
        private val context: Context
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reportIdTextView: TextView = itemView.findViewById(R.id.reportId)
        val listingIdTextView: TextView = itemView.findViewById(R.id.listingId)
        val reasonTextView: TextView = itemView.findViewById(R.id.reportReason)
        val commentsTextView: TextView = itemView.findViewById(R.id.reportComments)
        val statusTextView: TextView = itemView.findViewById(R.id.reportStatus)
        val dateTextView: TextView = itemView.findViewById(R.id.reportDate)
        val viewDetailsButton: Button = itemView.findViewById(R.id.viewDetailsButton)
        val markReviewedButton: Button = itemView.findViewById(R.id.markReviewedButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reports[position]

        holder.reportIdTextView.text = "Report #${report.reportId}"
        holder.listingIdTextView.text = "Listing: ${report.listingId}"
        holder.reasonTextView.text = report.reason
        holder.commentsTextView.text = report.comments
        holder.statusTextView.text = report.status

        // Format date
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dateString = sdf.format(Date(report.reportDate))
        holder.dateTextView.text = dateString

        // Set status color
        when (report.status) {
            "PENDING" ->
                    holder.statusTextView.setTextColor(
                            holder.itemView.context.getColor(android.R.color.holo_red_light)
                    )
            "REVIEWED" ->
                    holder.statusTextView.setTextColor(
                            holder.itemView.context.getColor(android.R.color.holo_orange_light)
                    )
            "CLOSED" ->
                    holder.statusTextView.setTextColor(
                            holder.itemView.context.getColor(android.R.color.holo_green_dark)
                    )
        }

        // Handle visibility of buttons based on report status
        if (report.status == "CLOSED") {
            // For closed reports, hide both buttons or show a disabled state
            holder.viewDetailsButton.visibility = View.GONE
            holder.markReviewedButton.text = "Closed"
            holder.markReviewedButton.isEnabled = false
        } else {
            // For active reports (PENDING or REVIEWED), show appropriate buttons
            holder.viewDetailsButton.visibility = View.VISIBLE

            // Set up view details button
            holder.viewDetailsButton.setOnClickListener {
                // First check if the listing still exists before navigating
                scope.launch {
                    try {
                        val db = AppDatabase.getInstance(context)
                        val listing = db.listingDao().getListingById(report.listingId)

                        withContext(Dispatchers.Main) {
                            if (listing != null) {
                                // If listing exists, navigate to details
                                val intent = Intent(context, ListingDetailsActivity::class.java)
                                intent.putExtra("listing_id", report.listingId)
                                context.startActivity(intent)
                            } else {
                                // If listing doesn't exist anymore
                                Toast.makeText(
                                                context,
                                                "This listing is no longer available",
                                                Toast.LENGTH_SHORT
                                        )
                                        .show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                            context,
                                            "Error checking listing: ${e.message}",
                                            Toast.LENGTH_SHORT
                                    )
                                    .show()
                        }
                    }
                }
            }

            // Set up action button based on status
            if (report.status == "PENDING") {
                holder.markReviewedButton.text = "Take Action"
                holder.markReviewedButton.setOnClickListener { showActionDialog(report) }
            } else if (report.status == "REVIEWED") {
                holder.markReviewedButton.text = "Close Report"
                holder.markReviewedButton.setOnClickListener { closeReport(report) }
            }
        }
    }

    private fun showActionDialog(report: Report) {
        val options =
                arrayOf(
                        "Mark as Reviewed",
                        "Remove Listing",
                        "Deactivate Listing",
                        "Dismiss Report"
                )

        AlertDialog.Builder(context)
                .setTitle("Take Action")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> markAsReviewed(report)
                        1 -> showRemoveListingConfirmation(report)
                        2 -> deactivateListing(report)
                        3 -> dismissReport(report)
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
    }

    private fun markAsReviewed(report: Report) {
        scope.launch {
            try {
                val db = AppDatabase.getInstance(context)

                // Create a copy with the updated status
                val updatedReport = report.copy(status = "REVIEWED")
                db.reportDao().updateReport(updatedReport)

                // Update the list item
                val index = reports.indexOfFirst { it.reportId == report.reportId }
                if (index >= 0) {
                    reports[index] = updatedReport
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Report marked as reviewed", Toast.LENGTH_SHORT).show()
                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showRemoveListingConfirmation(report: Report) {
        AlertDialog.Builder(context)
                .setTitle("Remove Listing")
                .setMessage(
                        "Are you sure you want to permanently remove this listing? This action cannot be undone."
                )
                .setPositiveButton("Remove") { _, _ -> removeListing(report) }
                .setNegativeButton("Cancel", null)
                .show()
    }

    private fun removeListing(report: Report) {
        scope.launch {
            try {
                val db = AppDatabase.getInstance(context)

                // Delete the listing
                val listing = db.listingDao().getListingById(report.listingId)
                if (listing != null) {
                    db.listingDao().deleteListing(listing)

                    // Update all reports for this listing
                    val relatedReports = db.reportDao().getReportsForListing(report.listingId)
                    for (relatedReport in relatedReports) {
                        val updatedReport = relatedReport.copy(status = "CLOSED")
                        db.reportDao().updateReport(updatedReport)

                        // Update the report in our list if it exists
                        val index = reports.indexOfFirst { it.reportId == relatedReport.reportId }
                        if (index >= 0) {
                            reports[index] = updatedReport
                        }
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Listing removed successfully", Toast.LENGTH_SHORT)
                                .show()
                        notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deactivateListing(report: Report) {
        scope.launch {
            try {
                val db = AppDatabase.getInstance(context)

                // Deactivate the listing
                val listing = db.listingDao().getListingById(report.listingId)
                if (listing != null) {
                    val updatedListing = listing.copy(status = ListingStatus.INACTIVE)
                    db.listingDao().updateListing(updatedListing)

                    // Update report status
                    val updatedReport = report.copy(status = "REVIEWED")
                    db.reportDao().updateReport(updatedReport)

                    // Update the report in our list
                    val index = reports.indexOfFirst { it.reportId == report.reportId }
                    if (index >= 0) {
                        reports[index] = updatedReport
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Listing deactivated", Toast.LENGTH_SHORT).show()
                        notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun dismissReport(report: Report) {
        scope.launch {
            try {
                val db = AppDatabase.getInstance(context)

                // Update report status to CLOSED
                val updatedReport = report.copy(status = "CLOSED")
                db.reportDao().updateReport(updatedReport)

                // Update the report in our list
                val index = reports.indexOfFirst { it.reportId == report.reportId }
                if (index >= 0) {
                    reports[index] = updatedReport
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Report dismissed", Toast.LENGTH_SHORT).show()
                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun closeReport(report: Report) {
        scope.launch {
            try {
                val db = AppDatabase.getInstance(context)

                // Update report status
                val updatedReport = report.copy(status = "CLOSED")
                db.reportDao().updateReport(updatedReport)

                // Update the report in our list
                val index = reports.indexOfFirst { it.reportId == report.reportId }
                if (index >= 0) {
                    reports[index] = updatedReport
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Report closed", Toast.LENGTH_SHORT).show()
                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount() = reports.size

    fun updateReports(newReports: List<Report>) {
        reports.clear()
        reports.addAll(newReports)
        notifyDataSetChanged()
    }
}
