package com.yonko.expensetracker.ui.reports

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.yonko.expensetracker.data.Expense
import com.yonko.expensetracker.data.Repository
import com.yonko.expensetracker.databinding.FragmentReportsBinding
import com.yonko.expensetracker.util.Categories
import com.yonko.expensetracker.util.Formatting
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.time.YearMonth

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private var cursor: YearMonth = YearMonth.now()
    private var allExpenses: List<Expense> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repo = Repository.getInstance(requireContext())

        binding.prevMonthButton.setOnClickListener { cursor = cursor.minusMonths(1); renderReport() }
        binding.nextMonthButton.setOnClickListener { cursor = cursor.plusMonths(1); renderReport() }
        binding.exportButton.setOnClickListener { exportCsv() }

        viewLifecycleOwner.lifecycleScope.launch {
            repo.observeExpenses().collect { expenses ->
                allExpenses = expenses
                renderReport()
            }
        }
    }

    private fun renderReport() {
        binding.reportMonthLabel.text = Formatting.monthLabel(cursor.year, cursor.monthValue)
        val key = "%04d-%02d".format(cursor.year, cursor.monthValue)
        val monthExpenses = allExpenses.filter { Formatting.monthKey(it.date) == key }
        val total = monthExpenses.sumOf { it.amount }
        binding.reportTotal.text = Formatting.money(total)

        val byCategory = monthExpenses.groupBy { it.category }.mapValues { it.value.sumOf { e -> e.amount } }
        val bars = Categories.ALL.map { cat ->
            BarChartView.Bar("${cat.emoji} ${cat.label}", byCategory[cat.id] ?: 0.0, cat.color)
        }
        binding.categoryChart.setData(bars)

        val daysInMonth = cursor.lengthOfMonth()
        val byDay = DoubleArray(daysInMonth)
        monthExpenses.forEach { e ->
            val day = e.date.substring(8, 10).toIntOrNull() ?: 1
            if (day in 1..daysInMonth) byDay[day - 1] += e.amount
        }
        binding.dailyChart.setData(byDay.toList())
    }

    private fun exportCsv() {
        if (allExpenses.isEmpty()) {
            Toast.makeText(requireContext(), "No expenses to export", Toast.LENGTH_SHORT).show()
            return
        }
        val dir = File(requireContext().cacheDir, "exports").apply { mkdirs() }
        val file = File(dir, "expenses.csv")
        FileWriter(file).use { w ->
            w.append("Date,Category,Amount,Note,Method\n")
            allExpenses.sortedBy { it.date }.forEach { e ->
                val cat = Categories.byId(e.category).label
                val note = e.note.replace("\"", "\"\"")
                w.append("${e.date},$cat,${e.amount},\"$note\",${e.method}\n")
            }
        }
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Export expenses"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
