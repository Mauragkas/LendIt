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
