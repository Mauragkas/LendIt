package com.example.lendit.data.repository

import ReportDao
import com.example.lendit.data.local.entities.ListingReportCount
import com.example.lendit.data.local.entities.Report

class ReportRepository(private val reportDao: ReportDao) {

    suspend fun insertReport(report: Report): Long =
        reportDao.insert(report)

    suspend fun updateReport(report: Report) =
        reportDao.updateReport(report)

    suspend fun getReportsForListing(listingId: Int): List<Report> =
        reportDao.getReportsForListing(listingId)

    suspend fun getAllReports(): List<Report> =
        reportDao.getAllReports()

    suspend fun getReportsByStatus(status: String): List<Report> =
        reportDao.getReportsByStatus(status)

    suspend fun deleteReport(reportId: Int) =
        reportDao.deleteReport(reportId)

    suspend fun getReportById(reportId: Int): Report? =
        reportDao.getReportById(reportId)

    suspend fun getListingReportCounts(): List<ListingReportCount> =
        reportDao.getListingReportCounts()
}
