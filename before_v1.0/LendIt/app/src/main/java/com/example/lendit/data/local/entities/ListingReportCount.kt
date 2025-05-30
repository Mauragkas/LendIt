package com.example.lendit.data.local.entities

import androidx.room.ColumnInfo

data class ListingReportCount(
    @ColumnInfo(name = "listingId") val listingId: Int,
    @ColumnInfo(name = "reportCount") val reportCount: Int
)
