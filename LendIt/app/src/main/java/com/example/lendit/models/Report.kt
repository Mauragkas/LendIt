package com.example.lendit.models

import java.time.LocalDateTime

enum class ReportStatus {
    PENDING,
    PROCESSED,
    REJECTED
}

data class Report(
    val reportId: String,
    val reason: String,
    val comments: String,
    val attachments: List<Media> = listOf(),
    var status: ReportStatus = ReportStatus.PENDING,
    val date: LocalDateTime = LocalDateTime.now()
) {
    fun submitReport(): Boolean {
        // Implementation
        return true
    }

    fun processReport(): Boolean {
        // Implementation
        return true
    }
}
