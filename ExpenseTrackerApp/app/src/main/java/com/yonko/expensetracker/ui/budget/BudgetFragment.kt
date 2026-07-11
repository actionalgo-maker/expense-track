package com.yonko.expensetracker.ui.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.yonko.expensetracker.R
import com.yonko.expensetracker.data.Budget
import com.yonko.expensetracker.data.Expense
import com.yonko.expensetracker.data.Repository
import com.yonko.expensetracker.databinding.FragmentBudgetBinding
import com.yonko.expensetracker.databinding.ItemBudgetRowBinding
import com.yonko.expensetracker.util.Categories
import com.yonko.expensetracker.util.Formatting
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!
    private val rowInputs = mutableMapOf<String, EditText>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repo = Repository.getInstance(requireContext())

        buildRows()

        viewLifecycleOwner.lifecycleScope.launch {
            combine(repo.observeExpenses(), repo.observeBudgets()) { expenses, budgets ->
                Pair(expenses, budgets)
            }.collect { (expenses, budgets) ->
                render(expenses, budgets)
            }
        }

        binding.saveBudgetsButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                rowInputs.forEach { (categoryId, editText) ->
                    val value = editText.text.toString().toDoubleOrNull()
                    if (value != null && value > 0.0) {
                        repo.setBudget(categoryId, value)
                    }
                }
                Toast.makeText(requireContext(), "Budgets saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val rowBindings = mutableMapOf<String, ItemBudgetRowBinding>()

    private fun buildRows() {
        binding.budgetRowsContainer.removeAllViews()
        rowInputs.clear()
        rowBindings.clear()
        Categories.ALL.forEach { cat ->
            val rowBinding = ItemBudgetRowBinding.inflate(layoutInflater, binding.budgetRowsContainer, false)
            rowBinding.categoryLabel.text = "${cat.emoji} ${cat.label}"
            binding.budgetRowsContainer.addView(rowBinding.root)
            rowInputs[cat.id] = rowBinding.budgetInput
            rowBindings[cat.id] = rowBinding
        }
    }

    private fun render(expenses: List<Expense>, budgets: List<Budget>) {
        val monthKey = Formatting.currentMonthKey()
        val monthExpenses = expenses.filter { Formatting.monthKey(it.date) == monthKey }
        val spentByCategory = monthExpenses.groupBy { it.category }.mapValues { it.value.sumOf { e -> e.amount } }
        val budgetByCategory = budgets.associateBy({ it.category }, { it.amount })

        Categories.ALL.forEach { cat ->
            val rowBinding = rowBindings[cat.id] ?: return@forEach
            val spent = spentByCategory[cat.id] ?: 0.0
            val budgetAmount = budgetByCategory[cat.id]

            if (rowInputs[cat.id]?.text?.toString().isNullOrBlank() && budgetAmount != null) {
                rowInputs[cat.id]?.setText(budgetAmount.toString())
            }

            rowBinding.spentLabel.text = if (budgetAmount != null) {
                "${Formatting.money(spent)} / ${Formatting.money(budgetAmount)}"
            } else {
                "${Formatting.money(spent)} spent"
            }

            val pct = if (budgetAmount != null && budgetAmount > 0) {
                ((spent / budgetAmount) * 100).coerceIn(0.0, 100.0).toInt()
            } else 0
            rowBinding.progressBar.progress = pct
            rowBinding.progressBar.progressTintList = android.content.res.ColorStateList.valueOf(
                when {
                    budgetAmount == null -> 0xFF262B38.toInt()
                    pct >= 100 -> 0xFFFF5C72.toInt()
                    pct >= 80 -> 0xFFFFB84D.toInt()
                    else -> 0xFF3ECF8E.toInt()
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
