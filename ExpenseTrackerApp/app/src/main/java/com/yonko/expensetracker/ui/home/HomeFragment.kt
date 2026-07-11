package com.yonko.expensetracker.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.yonko.expensetracker.data.Expense
import com.yonko.expensetracker.data.Repository
import com.yonko.expensetracker.databinding.FragmentHomeBinding
import com.yonko.expensetracker.util.Formatting
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ExpenseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ExpenseAdapter(onDelete = ::confirmDelete)
        binding.expenseList.layoutManager = LinearLayoutManager(requireContext())
        binding.expenseList.adapter = adapter

        val repo = Repository.getInstance(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            repo.observeExpenses().collect { expenses -> render(expenses) }
        }
    }

    private fun confirmDelete(expense: Expense) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete expense?")
            .setMessage("${expense.note.ifBlank { expense.category }} · ${Formatting.money(expense.amount)}")
            .setPositiveButton("Delete") { _, _ ->
                val repo = Repository.getInstance(requireContext())
                viewLifecycleOwner.lifecycleScope.launch { repo.deleteExpense(expense) }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun render(expenses: List<Expense>) {
        val monthKey = Formatting.currentMonthKey()
        val monthExpenses = expenses.filter { Formatting.monthKey(it.date) == monthKey }
        val total = monthExpenses.sumOf { it.amount }
        binding.monthTotal.text = Formatting.money(total)
        binding.monthCount.text = "${monthExpenses.size} expenses"
        val dayOfMonth = LocalDate.now().dayOfMonth
        binding.monthAvg.text = "${Formatting.money(total / dayOfMonth)}/day"

        if (expenses.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.expenseList.visibility = View.GONE
            return
        }
        binding.emptyState.visibility = View.GONE
        binding.expenseList.visibility = View.VISIBLE

        val items = mutableListOf<ExpenseListItem>()
        var lastDate: String? = null
        expenses.take(200).forEach { e ->
            if (e.date != lastDate) {
                lastDate = e.date
                items.add(ExpenseListItem.Header(Formatting.prettyDate(e.date)))
            }
            items.add(ExpenseListItem.Row(e))
        }
        adapter.submit(items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
