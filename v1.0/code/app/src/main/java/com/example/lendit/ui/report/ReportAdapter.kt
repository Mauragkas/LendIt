package com.example.lendit.ui.report

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.ScrollView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.lendit.ListingDetailsActivity
import com.example.lendit.R
import com.example.lendit.data.local.entities.Report
import com.example.lendit.data.local.managers.ReportManager
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
        private val context: Context,
        private val reportCounts: MutableMap<Int, Int> = mutableMapOf()
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    // Keep track of expanded items
    private val expandedItems = mutableSetOf<Int>()

    // Create report manager
    private val reportManager = ReportManager(
        context = context,
        coroutineScope = scope,
        onReportSubmitted = {
            // This callback won't be used in the adapter
        },
        onStatusUpdated = { updatedReport ->
            val position = reports.indexOfFirst { it.reportId == updatedReport.reportId }
            if (position != -1) {
                reports[position] = updatedReport
                notifyItemChanged(position)
            }
        },
        onError = { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    )

    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Compact view elements
        val compactView: LinearLayout = itemView.findViewById(R.id.compactView)
        val listingIdTextView: TextView = itemView.findViewById(R.id.listingId)
        val statusTextView: TextView = itemView.findViewById(R.id.reportStatus)

        // Expanded view elements
        val expandedView: LinearLayout = itemView.findViewById(R.id.expandedView)
        val reportIdTextView: TextView = itemView.findViewById(R.id.reportId)
        val reasonTextView: TextView = itemView.findViewById(R.id.reportReason)
        val commentsTextView: TextView = itemView.findViewById(R.id.reportComments)
        val dateTextView: TextView = itemView.findViewById(R.id.reportDate)
        val viewDetailsButton: Button = itemView.findViewById(R.id.viewDetailsButton)
        val markReviewedButton: Button = itemView.findViewById(R.id.markReviewedButton)
        val viewAllReportsButton: Button = itemView.findViewById(R.id.viewAllReportsButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    private fun showReportDetailsDialog(report: Report) {
        // Create a formatted string with all details
        val detailsBuilder = StringBuilder()

        detailsBuilder.append("Report #${report.reportId}\n\n")
        detailsBuilder.append("Listing ID: ${report.listingId}\n")
        detailsBuilder.append("Reason: ${report.reason}\n\n")
        detailsBuilder.append("Comments:\n${report.comments}\n\n")

        // Format date
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dateString = sdf.format(Date(report.reportDate))
        detailsBuilder.append("Reported on: $dateString\n")
        detailsBuilder.append("Status: ${report.status}\n\n")

        // Check if there are attachments
        if (!report.attachments.isNullOrEmpty()) {
            detailsBuilder.append("Attachments: ${report.attachments}\n")
        } else {
            detailsBuilder.append("No attachments provided")
        }

        // Create and show the dialog
        val textView = TextView(context).apply {
            text = detailsBuilder.toString()
            setPadding(30, 30, 30, 30)
            textSize = 16f
        }

        AlertDialog.Builder(context)
            .setTitle("Report Details")
            .setView(textView)
            .setPositiveButton("Close", null)
            .create()
            .show()
    }

    private fun showAllReportsForListingDialog(listingId: Int) {
        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    reportManager.getReportsForListing(listingId) { reports ->
                        if (reports.isEmpty()) {
                            Toast.makeText(context, "No reports found for this listing", Toast.LENGTH_SHORT).show()
                            return@getReportsForListing
                        }

                        // Create a formatted list of all reports for this listing
                        val reportsBuilder = StringBuilder()

                        reportsBuilder.append("Listing #$listingId\n")
                        reportsBuilder.append("Total Reports: ${reports.size}\n")
                        reportsBuilder.append("----------------------------------------\n\n")

                        // Sort reports by date (newest first)
                        val sortedReports = reports.sortedByDescending { it.reportDate }

                        sortedReports.forEach { report ->
                            // Format date
                            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            val dateString = sdf.format(Date(report.reportDate))

                            reportsBuilder.append("REPORT #${report.reportId} (${dateString})\n")
                            reportsBuilder.append("Status: ${report.status}\n")
                            reportsBuilder.append("Reason: ${report.reason}\n")
                            reportsBuilder.append("Comments: ${report.comments}\n")

                            // Add attachment info if available
                            if (!report.attachments.isNullOrEmpty()) {
                                reportsBuilder.append("Attachments: ${report.attachments}\n")
                            }

                            reportsBuilder.append("----------------------------------------\n\n")
                        }

                        // Create scrollable text view for the dialog
                        val scrollView = ScrollView(context).apply {
                            val textView = TextView(context).apply {
                                text = reportsBuilder.toString()
                                setPadding(30, 30, 30, 30)
                                textSize = 14f
                            }
                            addView(textView)
                        }

                        // Show dialog with all reports
                        scope.launch(Dispatchers.Main) {
                            AlertDialog.Builder(context)
                                .setTitle("All Reports for This Listing")
                                .setView(scrollView)
                                .setPositiveButton("Close", null)
                                .create()
                                .show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error loading reports: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reports[position]

        // Get report count for this listing
        val reportCount = reportCounts[report.listingId] ?: 1

        // Set up compact view
        holder.listingIdTextView.text = "Listing: ${report.listingId} (${reportCount} reports)"
        holder.statusTextView.text = report.status

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

        // Set up expanded view
        holder.reportIdTextView.text = "Report #${report.reportId}"
        holder.reasonTextView.text = report.reason
        holder.commentsTextView.text = report.comments

        // Format date
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dateString = sdf.format(Date(report.reportDate))
        holder.dateTextView.text = dateString

        // Handle visibility of expanded view based on expanded state
        holder.expandedView.visibility = if (expandedItems.contains(position)) View.VISIBLE else View.GONE

        // Set click listener on compact view to toggle expanded view
        holder.compactView.setOnClickListener {
            if (expandedItems.contains(position)) {
                expandedItems.remove(position)
                holder.expandedView.visibility = View.GONE
            } else {
                expandedItems.add(position)
                holder.expandedView.visibility = View.VISIBLE
            }
        }

        // Handle buttons visibility and setup
        if (report.status == "CLOSED") {
            holder.viewDetailsButton.visibility = View.GONE
            holder.markReviewedButton.text = "Closed"
            holder.markReviewedButton.isEnabled = false
        } else {
            holder.viewDetailsButton.visibility = View.VISIBLE

            // Set up view details button
            holder.viewDetailsButton.setOnClickListener {
                // Use reportManager to check if listing exists before navigating
                scope.launch {
                    try {
                        // Navigate to listing details
                        val intent = Intent(context, ListingDetailsActivity::class.java)
                        intent.putExtra("listing_id", report.listingId)
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                    context,
                                    "Error accessing listing: ${e.message}",
                                    Toast.LENGTH_SHORT
                            ).show()
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

        // Set up the View All Reports button
        holder.viewAllReportsButton.setOnClickListener {
            showAllReportsForListingDialog(report.listingId)
        }
    }

    override fun getItemCount(): Int {
        return reports.size
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
        reportManager.updateReportStatus(report, "REVIEWED")
        Toast.makeText(context, "Report marked as reviewed", Toast.LENGTH_SHORT).show()
    }

    private fun showRemoveListingConfirmation(report: Report) {
        AlertDialog.Builder(context)
                .setTitle("Remove Listing")
                .setMessage("Are you sure you want to permanently remove this listing? This action cannot be undone.")
                .setPositiveButton("Remove") { _, _ -> removeListing(report) }
                .setNegativeButton("Cancel", null)
                .show()
    }

    private fun removeListing(report: Report) {
        reportManager.removeListing(report) {
            Toast.makeText(context, "Listing removed successfully", Toast.LENGTH_SHORT).show()
            // Refresh the adapter with updated data
            val reportsToUpdate = reports.filter { it.listingId == report.listingId }
            for (reportToUpdate in reportsToUpdate) {
                val position = reports.indexOf(reportToUpdate)
                if (position != -1) {
                    reports[position] = reportToUpdate.copy(status = "CLOSED")
                    notifyItemChanged(position)
                }
            }
        }
    }

    private fun deactivateListing(report: Report) {
        reportManager.deactivateListing(report) {
            Toast.makeText(context, "Listing deactivated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun dismissReport(report: Report) {
        reportManager.updateReportStatus(report, "CLOSED")
        Toast.makeText(context, "Report dismissed", Toast.LENGTH_SHORT).show()
    }

    private fun closeReport(report: Report) {
        reportManager.updateReportStatus(report, "CLOSED")
        Toast.makeText(context, "Report closed", Toast.LENGTH_SHORT).show()
    }

    // Update this method to include handling of expanded items
    fun updateReports(newReports: List<Report>, newReportCounts: Map<Int, Int> = emptyMap()) {
        reports.clear()
        reports.addAll(newReports)

        reportCounts.clear()
        reportCounts.putAll(newReportCounts)

        // Reset expanded items when data changes
        expandedItems.clear()

        notifyDataSetChanged()
    }
}
