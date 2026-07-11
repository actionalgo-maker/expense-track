package com.yonko.expensetracker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.yonko.expensetracker.R
import com.yonko.expensetracker.databinding.ActivityMainBinding
import com.yonko.expensetracker.ui.add.AddFragment
import com.yonko.expensetracker.ui.budget.BudgetFragment
import com.yonko.expensetracker.ui.home.HomeFragment
import com.yonko.expensetracker.ui.reports.ReportsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            showFragment(HomeFragment())
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_reports -> ReportsFragment()
                R.id.nav_add -> AddFragment()
                R.id.nav_budget -> BudgetFragment()
                else -> HomeFragment()
            }
            showFragment(fragment)
            true
        }

        binding.fabAdd.setOnClickListener {
            binding.bottomNav.selectedItemId = R.id.nav_add
        }
    }

    fun goHome() {
        binding.bottomNav.selectedItemId = R.id.nav_home
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
