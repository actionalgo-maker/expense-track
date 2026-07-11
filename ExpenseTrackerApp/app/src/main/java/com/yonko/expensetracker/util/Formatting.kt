package com.yonko.expensetracker.util

import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object Formatting {
    private val inr: NumberFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 0
    }

    fun money(amount: Double): String = inr.format(amount)

    fun isoToday(): String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    fun monthKey(isoDate: String): String = isoDate.substring(0, 7)

    fun currentMonthKey(): String = LocalDate.now().toString().substring(0, 7)

    fun prettyDate(isoDate: String): String {
        val d = LocalDate.parse(isoDate)
        return d.format(DateTimeFormatter.ofPattern("EEE, d MMM", Locale.ENGLISH))
    }

    fun monthLabel(year: Int, month: Int): String {
        val d = LocalDate.of(year, month, 1)
        return d.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH))
    }
}
