package com.example.lendit.data.local.managers

import android.content.Context
import android.net.Uri
import com.example.lendit.data.local.entities.ListingReportCount
import com.example.lendit.data.local.entities.Report
import com.example.lendit.data.local.entities.ReportClass
import com.example.lendit.data.repository.RepositoryProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Manager class for handling report operations
 */
class ReportManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val onReportSubmitted: () -> Unit,
    private val onStatusUpdated: (Report) -> Unit,
    private val onError: (String) -> Unit
) {
    private val reportRepository by lazy {
        RepositoryProvider.getReportRepository(context)
    }

    private val listingRepository by lazy {
        RepositoryProvider.getListingRepository(context)
    }

    // For identifying inappropriate content
    private val inappropriateWords = listOf("κακή λέξη", "βρισιά", "προσβλητικό")

    /**
     * Checks if text contains inappropriate content
     */
    fun containsInappropriateContent(text: String): Boolean {
        val lowerText = text.lowercase()
        return inappropriateWords.any { lowerText.contains(it) }
    }

    /**
     * Submits a new report
     */
    fun submitReport(listingId: Int, reason: String, comments: String, attachments: List<Uri> = emptyList()) {
        if (containsInappropriateContent(comments)) {
            onError("Η αναφορά περιέχει ακατάλληλη γλώσσα. Παρακαλώ αναθεωρήστε το κείμενό σας.")
            return
        }

        coroutineScope.launch {
            try {
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
                val reportId = withContext(Dispatchers.IO) {
                    reportRepository.insertReport(report)
                }

                withContext(Dispatchers.Main) {
                    onReportSubmitted()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Σφάλμα κατά την υποβολή: ${e.message}")
                }
            }
        }
    }

    /**
     * Gets all reports
     */
    fun getAllReports(onReportsLoaded: (List<Report>, Map<Int, Int>) -> Unit) {
        coroutineScope.launch {
            try {
                val allReports = reportRepository.getAllReports()

                // Group reports by listing ID and count them
                val reportCountByListing = allReports.groupingBy { it.listingId }.eachCount()

                withContext(Dispatchers.Main) {
                    onReportsLoaded(allReports, reportCountByListing)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Error loading reports: ${e.message}")
                }
            }
        }
    }

    /**
     * Gets filtered reports
     */
    fun getFilteredReports(filter: String, onReportsLoaded: (List<Report>, Map<Int, Int>) -> Unit) {
        coroutineScope.launch {
            try {
                val allReports = reportRepository.getAllReports()

                // Group reports by listing ID and count them
                val reportCountByListing = allReports.groupingBy { it.listingId }.eachCount()

                // Filter reports based on selected filter
                val reports = ReportClass.convertToReportClassList(allReports)
                val filteredReports = ReportClass.filterReports(reports, filter)

                if (filteredReports.isNotEmpty()) {
                    // Group by listing ID and take the most recent report for each listing
                    val uniqueReports = ReportClass.getUniqueLatestReportsByListing(filteredReports)
                    val sortedReports = ReportClass.sortReportsByReportCount(uniqueReports, reportCountByListing)
                    val sortedReportEntities = ReportClass.convertToReportEntityList(sortedReports)

                    withContext(Dispatchers.Main) {
                        onReportsLoaded(sortedReportEntities, reportCountByListing)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onReportsLoaded(emptyList(), emptyMap())
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Error loading reports: ${e.message}")
                }
            }
        }
    }

    /**
     * Gets all reports for a specific listing
     */
    fun getReportsForListing(listingId: Int, onReportsLoaded: (List<Report>) -> Unit) {
        coroutineScope.launch {
            try {
                val reports = reportRepository.getReportsForListing(listingId)
                withContext(Dispatchers.Main) {
                    onReportsLoaded(reports)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Error loading reports for listing: ${e.message}")
                }
            }
        }
    }

    /**
     * Updates a report's status
     */
    fun updateReportStatus(report: Report, newStatus: String) {
        coroutineScope.launch {
            try {
                val updatedReport = report.copy(status = newStatus)
                reportRepository.updateReport(updatedReport)

                withContext(Dispatchers.Main) {
                    onStatusUpdated(updatedReport)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Error updating report status: ${e.message}")
                }
            }
        }
    }

    /**
     * Deactivates a listing and marks related reports as reviewed
     */
    fun deactivateListing(report: Report, onDeactivated: () -> Unit) {
        coroutineScope.launch {
            try {
                // Deactivate the listing
                val success = com.example.lendit.data.local.ListingManager.updateListingStatus(
                    context,
                    report.listingId,
                    ListingStatus.INACTIVE
                )

                if (success) {
                    // Update report status
                    val updatedReport = report.copy(status = "REVIEWED")
                    reportRepository.updateReport(updatedReport)

                    withContext(Dispatchers.Main) {
                        onStatusUpdated(updatedReport)
                        onDeactivated()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onError("Failed to deactivate listing")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Error deactivating listing: ${e.message}")
                }
            }
        }
    }

    /**
     * Removes a listing and marks all related reports as closed
     */
    fun removeListing(report: Report, onRemoved: () -> Unit) {
        coroutineScope.launch {
            try {
                val listing = listingRepository.getListingById(report.listingId)

                if (listing != null) {
                    // Delete the listing
                    listingRepository.deleteListing(listing)

                    // Update all related reports
                    val relatedReports = reportRepository.getReportsForListing(report.listingId)
                    for (relatedReport in relatedReports) {
                        val updatedReport = relatedReport.copy(status = "CLOSED")
                        reportRepository.updateReport(updatedReport)
                    }

                    withContext(Dispatchers.Main) {
                        onRemoved()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onError("Listing not found")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Error removing listing: ${e.message}")
                }
            }
        }
    }
}
