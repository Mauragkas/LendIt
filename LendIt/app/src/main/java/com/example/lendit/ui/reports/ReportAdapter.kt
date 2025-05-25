package com.example.lendit.ui.reports

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lendit.R
import com.example.lendit.data.local.entities.Report
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportAdapter(private val reports: MutableList<Report>) :
    RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reportIdTextView: TextView = itemView.findViewById(R.id.reportId)
        val listingIdTextView: TextView = itemView.findViewById(R.id.listingId)
        val reasonTextView: TextView = itemView.findViewById(R.id.reportReason)
        val commentsTextView: TextView = itemView.findViewById(R.id.reportComments)
        val statusTextView: TextView = itemView.findViewById(R.id.reportStatus)
        val dateTextView: TextView = itemView.findViewById(R.id.reportDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)
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
            "PENDING" -> holder.statusTextView.setTextColor(holder.itemView.context.getColor(android.R.color.holo_red_light))
            "REVIEWED" -> holder.statusTextView.setTextColor(holder.itemView.context.getColor(android.R.color.holo_orange_light))
            "CLOSED" -> holder.statusTextView.setTextColor(holder.itemView.context.getColor(android.R.color.holo_green_dark))
        }
    }

    override fun getItemCount() = reports.size

    fun updateReports(newReports: List<Report>) {
        reports.clear()
        reports.addAll(newReports)
        notifyDataSetChanged()
    }
}
