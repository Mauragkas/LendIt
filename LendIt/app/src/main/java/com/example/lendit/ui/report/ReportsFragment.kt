package com.example.lendit.ui.report

import AppDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lendit.databinding.FragmentReportsBinding
import kotlinx.coroutines.launch

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding
        get() = _binding!!
    private lateinit var adapter: ReportAdapter
    private var currentFilter = "All"

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

        // Load reports
        loadReports()
    }

    private fun loadReports() {
        lifecycleScope.launch {
            try {
                val db = AppDatabase.getInstance(requireContext())
                val reports =
                        when (currentFilter) {
                            "Pending" -> db.reportDao().getReportsByStatus("PENDING")
                            "Reviewed" -> db.reportDao().getReportsByStatus("REVIEWED")
                            "Closed" -> db.reportDao().getReportsByStatus("CLOSED")
                            else -> db.reportDao().getAllReports()
                        }

                if (reports.isNotEmpty()) {
                    adapter.updateReports(reports)
                    binding.emptyStateLayout.visibility = View.GONE
                    binding.reportCountText.text = "Reports found: ${reports.size}"
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
                        )
                        .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
