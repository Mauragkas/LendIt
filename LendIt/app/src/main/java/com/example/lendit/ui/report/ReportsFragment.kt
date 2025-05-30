package com.example.lendit.ui.report

import AppDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lendit.data.local.entities.Report
import com.example.lendit.databinding.FragmentReportsBinding
import kotlinx.coroutines.launch
import androidx.appcompat.app.AlertDialog
import com.example.lendit.R
import com.example.lendit.data.local.entities.ReportClass
import com.example.lendit.data.repository.RepositoryProvider

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ReportAdapter
    private var currentFilter = "All"
    private val reportRepository by lazy {
        RepositoryProvider.getReportRepository(requireContext())
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup filter spinner
        val filterOptions = arrayOf("All", "Pending", "Reviewed", "Closed")
        val spinnerAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filterOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.filterSpinner.adapter = spinnerAdapter

        binding.filterSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                    ) {
                        currentFilter = filterOptions[position]
                        loadReports()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Do nothing
                    }
                }

        // Setup RecyclerView
        adapter =
                ReportAdapter(mutableListOf(), viewLifecycleOwner.lifecycleScope, requireContext())
        binding.reportsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ReportsFragment.adapter
        }

        // Add listener for "View All Report Reasons" button
        binding.viewAllReasonsButton.setOnClickListener {
            showReportReasonsDialog()
        }

        // Load reports
        loadReports()
    }


    private fun loadReports() {
        lifecycleScope.launch {
            try {
                // Get all reports
                val allReports = reportRepository.getAllReports()

                // Group reports by listing ID and count them
                val reportCountByListing = allReports.groupingBy { it.listingId }.eachCount()

                // Filter reports based on selected filter
                val reports = ReportClass.convertToReportClassList(allReports)
                val filteredReports = ReportClass.filterReports(reports, currentFilter)

                if (filteredReports.isNotEmpty()) {
                    // Group by listing ID and take the most recent report for each listing
                    val uniqueReports = ReportClass.getUniqueLatestReportsByListing(filteredReports)
                    val sortedReports = ReportClass.sortReportsByReportCount(uniqueReports, reportCountByListing)
                    val sortedReportEntities = ReportClass.convertToReportEntityList(sortedReports)


                    adapter.updateReports(sortedReportEntities, reportCountByListing)
                    binding.emptyStateLayout.visibility = View.GONE
                    binding.reportCountText.text = "Reports found: ${filteredReports.size} across ${reportCountByListing.size} listings"
                    binding.reportCountText.visibility = View.VISIBLE
                } else {
                    binding.emptyStateLayout.visibility = View.VISIBLE
                    binding.reportCountText.visibility = View.GONE
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error loading reports: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showReportReasonsDialog() {
        lifecycleScope.launch {
            try {
                val allReports = reportRepository.getAllReports()

                // Create a formatted list of all report reasons with their comments and media
                val reportDetailsBuilder = StringBuilder()

                allReports.forEach { report ->
                    reportDetailsBuilder.append("Report #${report.reportId} (Listing #${report.listingId}):\n")
                    reportDetailsBuilder.append("Reason: ${report.reason}\n")
                    reportDetailsBuilder.append("Comments: ${report.comments}\n")

                    // Check if there are attachments
                    if (!report.attachments.isNullOrEmpty()) {
                        reportDetailsBuilder.append("Attachments: ${report.attachments}\n")
                    } else {
                        reportDetailsBuilder.append("Attachments: None\n")
                    }

                    reportDetailsBuilder.append("Status: ${report.status}\n")
                    reportDetailsBuilder.append("----------------------------\n\n")
                }

                val reportDetails = reportDetailsBuilder.toString()

                // Create and show the dialog using a simple TextView
                val textView = TextView(requireContext()).apply {
                    text = reportDetails
                    setPadding(20, 20, 20, 20)
                    textSize = 14f
                }

                // Use AlertDialog.Builder properly
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("All Report Reasons")
                builder.setView(textView)
                builder.setPositiveButton("Close", null)
                builder.create().show()

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error loading report reasons: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
