package com.example.lendit.ui.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import AppDatabase
import com.example.lendit.databinding.FragmentReportsBinding
import kotlinx.coroutines.launch

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ReportAdapter

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

        // Setup RecyclerView
        adapter = ReportAdapter(mutableListOf())
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
                val reports = db.reportDao().getAllReports()

                if (reports.isNotEmpty()) {
                    adapter.updateReports(reports)
                    binding.emptyStateLayout.visibility = View.GONE
                } else {
                    binding.emptyStateLayout.visibility = View.VISIBLE
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading reports: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
