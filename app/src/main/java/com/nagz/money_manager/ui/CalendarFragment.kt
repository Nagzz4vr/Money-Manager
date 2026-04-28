package com.nagz.money_manager.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nagz.money_manager.R
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nagz.money_manager.data.local.dao.CategoryAmount
import com.nagz.money_manager.data.local.database.AppDatabase
import com.nagz.money_manager.data.local.entity.TransactionEntity
import com.nagz.money_manager.data.repository.TransactionRepository
import kotlinx.coroutines.launch
import  java.util.Calendar
import com.nagz.money_manager.domain.model.TransactionType
import  com.nagz.money_manager.data.local.database.CategoryDatabase
import com.nagz.money_manager.data.repository.CategoryRepository
import kotlin.getValue

class CalendarFragment : Fragment(R.layout.fragment_calendar) {
    private val categoryViewModel: CategoryViewModel by viewModels {
        CategoryViewModelFactory(
            CategoryRepository(
                CategoryDatabase.getInstance(requireContext()).categoryDao()
            )
        )
    }

    //    val iconMap: StateFlow<Map<String, Int>>
    private var currentTransactions: List<TransactionEntity> = emptyList()
    private var currentIconMap: Map<String, Int> = emptyMap()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        val recycler = view.findViewById<RecyclerView>(R.id.rv_Calendar)
        val tvIncome = view.findViewById<TextView>(R.id.tv_Income)
        val tvExpense = view.findViewById<TextView>(R.id.tv_Expense)
        val tvNet = view.findViewById<TextView>(R.id.tv_Net)



        val adapter = TransactionAdapter(
            items = emptyList(),
            iconMap = emptyMap(),
            onItemClick = { transaction ->

                val bundle = Bundle().apply {
                    putString("TRANSACTION_ID", transaction.id)
                }
                findNavController().navigate(R.id.TransactionFragment, bundle)
            },
            onItemLongClick = { transaction ->
                // Long Press: Show Delete Confirmation
                showDeleteDialog(transaction)
            }
        )
        categoryViewModel.categories.observe(viewLifecycleOwner) { categories ->
            currentIconMap = categories.associate { it.name to it.iconRes }
            adapter.update(currentTransactions, currentIconMap)
        }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        // Latest date = today
        val today = System.currentTimeMillis()


        val dao = AppDatabase.getInstance(requireContext()).transactionDao()
        val repo = TransactionRepository(dao)

        fun loadForDate(dateMillis: Long) {
            lifecycleScope.launch {

                val cal = Calendar.getInstance().apply {
                    timeInMillis = dateMillis
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val startOfDay = cal.timeInMillis
                cal.add(Calendar.DAY_OF_MONTH, 1)
                val startOfNextDay = cal.timeInMillis

                repo.getTransactionsForDay(startOfDay, startOfNextDay)
                    .collect { transactions ->
                        currentTransactions = transactions
                        adapter.update(currentTransactions, currentIconMap)

                        // Optional: calculate totals for that day
                        val income = transactions.filter { it.type == TransactionType.RECEIVED }
                            .sumOf { it.amount }

                        val expense = transactions.filter { it.type == TransactionType.SPENT }
                            .sumOf { it.amount }

                        tvIncome.text = "₹ %.2f".format(income)
                        tvExpense.text = "₹ %.2f".format(expense)
                        tvNet.text = "₹ %.2f".format(income - expense)
                    }
            }
        }


        // Load today by default
        loadForDate(today)

        calendarView.setOnDateChangeListener { _, year, month, day ->
            val cal = Calendar.getInstance().apply {
                set(year, month, day)
            }
            loadForDate(cal.timeInMillis)
        }
    }

    private fun showDeleteDialog(transaction: TransactionEntity) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this entry?")
            .setPositiveButton("Delete") { _, _ ->
                performDelete(transaction)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performDelete(transaction: TransactionEntity) {
        lifecycleScope.launch {
            val dao = AppDatabase.getInstance(requireContext()).transactionDao()

            // 1. Remove from database
            dao.delete(transaction)

            // 2. Show Snackbar with Undo button
            com.google.android.material.snackbar.Snackbar.make(
                requireView(),
                "Transaction deleted",
                com.google.android.material.snackbar.Snackbar.LENGTH_LONG
            ).setAction("UNDO") {
                lifecycleScope.launch {
                    // 3. Re-insert if Undo is clicked
                    dao.insert(transaction)
                }
            }.show()
        }

    }
}
