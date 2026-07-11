package com.yonko.expensetracker.ui.add

import android.app.DatePickerDialog
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.yonko.expensetracker.data.Expense
import com.yonko.expensetracker.data.Repository
import com.yonko.expensetracker.databinding.FragmentAddBinding
import com.yonko.expensetracker.ui.MainActivity
import com.yonko.expensetracker.util.Categories
import com.yonko.expensetracker.util.Formatting
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private var selectedCategory = Categories.ALL.first().id
    private var selectedDate: LocalDate = LocalDate.now()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buildCategoryChips()

        binding.methodDropdown.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                listOf("Cash", "UPI", "Card", "Other"))
        )
        binding.methodDropdown.setText("Cash", false)

        updateDateText()
        binding.dateField.setOnClickListener { pickDate() }

        binding.saveButton.setOnClickListener { save() }
    }

    private val allChips = mutableListOf<TextView>()

    private fun buildCategoryChips() {
        binding.categoryChipGroup.removeAllViews()
        allChips.clear()
        val dp = resources.displayMetrics.density
        Categories.ALL.chunked(3).forEach { rowCategories ->
            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = (6 * dp).toInt() }
            }
            rowCategories.forEach { cat ->
                val chip = TextView(requireContext()).apply {
                    text = "${cat.emoji}\n${cat.label}"
                    gravity = Gravity.CENTER
                    textSize = 12f
                    setTypeface(typeface, Typeface.NORMAL)
                    setTextColor(ContextCompat.getColor(requireContext(), com.yonko.expensetracker.R.color.text_muted))
                    setPadding((10 * dp).toInt(), (10 * dp).toInt(), (10 * dp).toInt(), (10 * dp).toInt())
                    setBackgroundResource(com.yonko.expensetracker.R.drawable.category_chip_bg)
                    isSelected = cat.id == selectedCategory
                    isClickable = true
                    isFocusable = true
                    layoutParams = LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                    ).apply {
                        marginEnd = (6 * dp).toInt()
                        marginStart = (6 * dp).toInt()
                    }
                    setOnClickListener {
                        selectedCategory = cat.id
                        allChips.forEach { it.isSelected = false }
                        isSelected = true
                    }
                }
                allChips.add(chip)
                row.addView(chip)
            }
            binding.categoryChipGroup.addView(row)
        }
    }

    private fun pickDate() {
        val d = selectedDate
        DatePickerDialog(requireContext(), { _, y, m, day ->
            selectedDate = LocalDate.of(y, m + 1, day)
            updateDateText()
        }, d.year, d.monthValue - 1, d.dayOfMonth).show()
    }

    private fun updateDateText() {
        binding.dateField.setText(selectedDate.toString())
    }

    private fun save() {
        val amountText = binding.amountInput.text.toString()
        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            Toast.makeText(requireContext(), "Enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }
        val note = binding.noteInput.text.toString().trim()
        val method = binding.methodDropdown.text.toString().ifBlank { "Cash" }

        val expense = Expense(
            amount = amount,
            category = selectedCategory,
            note = note,
            date = selectedDate.toString(),
            method = method
        )

        val repo = Repository.getInstance(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            repo.addExpense(expense)
            Toast.makeText(requireContext(), "Expense saved", Toast.LENGTH_SHORT).show()
            binding.amountInput.setText("")
            binding.noteInput.setText("")
            selectedDate = LocalDate.now()
            updateDateText()
            (activity as? MainActivity)?.goHome()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
