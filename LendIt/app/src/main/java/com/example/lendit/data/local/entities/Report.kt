package com.example.lendit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "report")
data class Report(
    @PrimaryKey(autoGenerate = true) val reportId: Int = 0,
    val listingId: Int,
    val reason: String,
    val comments: String,
    val status: String,  // "PENDING", "REVIEWED", "CLOSED"
    val reportDate: Long,
    val attachments: String? = null  // Comma-separated list of file paths
)

class ReportClass(
    val reportId: Int = 0,
    val listingId: Int,
    val reason: String,
    val comments: String,
    val status: String,  // "PENDING", "REVIEWED", "CLOSED"
    val reportDate: Long,
    val attachments: String? = null
) {
    companion object {
        fun convertToReportClassList(reports: List<Report>): List<ReportClass> {
            return reports.map { r ->
                ReportClass(
                    reportId = r.reportId,
                    listingId = r.listingId,
                    reason = r.reason,
                    comments = r.comments,
                    status = r.status,
                    reportDate = r.reportDate,
                    attachments = r.attachments
                )
            }
        }

        fun convertToReportEntityList(reportClasses: List<ReportClass>): List<Report> {
            return reportClasses.map { rc ->
                Report(
                    reportId = rc.reportId,
                    listingId = rc.listingId,
                    reason = rc.reason,
                    comments = rc.comments,
                    status = rc.status,
                    reportDate = rc.reportDate,
                    attachments = rc.attachments
                )
            }
        }

        fun filterReports(
            reports: List<ReportClass>,
            filter: String
        ): List<ReportClass> = when (filter) {
            "Pending" -> reports.filter { it.status == "PENDING" }
            "Reviewed" -> reports.filter { it.status == "REVIEWED" }
            "Closed" -> reports.filter { it.status == "CLOSED" }
            else -> reports
        }

        // Get the most recent report for each listingId (unique reports)
        fun getUniqueLatestReportsByListing(reports: List<ReportClass>): List<ReportClass> =
            reports.groupBy { it.listingId }
                .map { it.value.maxByOrNull { report -> report.reportDate }!! }

        // Sort reports by number of reports per listing (descending)
        // Takes a map of listingId to report count for sorting
        fun sortReportsByReportCount(
            reports: List<ReportClass>,
            reportCountByListing: Map<Int, Int>
        ): List<ReportClass> =
            reports.sortedByDescending { reportCountByListing[it.listingId] ?: 0 }
    }
}

