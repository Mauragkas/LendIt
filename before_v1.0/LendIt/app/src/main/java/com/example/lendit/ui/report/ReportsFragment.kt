package com.example.lendit.ui.report

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
import com.example.lendit.data.local.managers.ReportManager
import com.example.lendit.databinding.FragmentReportsBinding
import kotlinx.coroutines.launch
import androidx.appcompat.app.AlertDialog
import com.example.lendit.R
import com.example.lendit.data.local.entities.ReportClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ReportAdapter
    private var currentFilter = "All"
    private lateinit var reportManager: ReportManager

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

        // Initialize the report manager
        reportManager = ReportManager(
            context = requireContext(),
            coroutineScope = CoroutineScope(Dispatchers.Main),
            onReportSubmitted = {
                // Not used in this fragment
            },
            onStatusUpdated = {
                // Not used directly in this fragment
            },
            onError = { errorMessage ->
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        )

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
        reportManager.getFilteredReports(currentFilter) { reports, reportCountByListing ->
            if (reports.isNotEmpty()) {
                adapter.updateReports(reports, reportCountByListing)
                binding.emptyStateLayout.visibility = View.GONE
                binding.reportCountText.text = "Reports found: ${reports.size} across ${reportCountByListing.size} listings"
                binding.reportCountText.visibility = View.VISIBLE
            } else {
                binding.emptyStateLayout.visibility = View.VISIBLE
                binding.reportCountText.visibility = View.GONE
            }
        }
    }

    private fun showReportReasonsDialog() {
        reportManager.getAllReports { allReports, _ ->
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
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
