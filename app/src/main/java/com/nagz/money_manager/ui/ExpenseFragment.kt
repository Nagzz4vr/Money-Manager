package com.nagz.money_manager.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nagz.money_manager.R
import com.nagz.money_manager.data.local.database.AppDatabase
import com.nagz.money_manager.data.local.database.CategoryDatabase
import com.nagz.money_manager.data.local.entity.TransactionEntity
import com.nagz.money_manager.data.repository.CategoryRepository
import com.nagz.money_manager.data.repository.TransactionRepository
import com.nagz.money_manager.utils.Delete_Utils
import com.nagz.money_manager.utils.PlotUtils
import kotlinx.coroutines.launch
import kotlin.getValue

class ExpenseFragment : Fragment(R.layout.fragment_expense) {

    private lateinit var adapter: TransactionAdapter

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

    private var userInteracted = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerViewExpense)
        val totalexpense = view.findViewById<TextView>(R.id.TotalExpense)
        val spinnerExpenseType = view.findViewById<Spinner>(R.id.spinnerExpenseType)
        val fab_ai = view.findViewById<FloatingActionButton>(R.id.fab_ai)
        val fab_add = view.findViewById<FloatingActionButton>(R.id.fab_add)
        val Scroll_Expense = view.findViewById<NestedScrollView>(R.id.Scroll_Expense)
        val deleteController = Delete_Utils(
            context = requireContext(),
            rootView = requireView(),
            lifecycleOwner = viewLifecycleOwner,
            dao = AppDatabase.getInstance(requireContext()).transactionDao()
        )


        val ExpensepieChart = view.findViewById<PieChart>(R.id.ExpensepieChart)

        Scroll_Expense.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY) {
                fab_add.hide()
                fab_ai.hide()
            } else if (scrollY < oldScrollY) {
                // Scroll UP
                fab_add.hide()
                fab_ai.hide()
            }

            if (scrollY == 0 || scrollY == (v.getChildAt(0).measuredHeight - v.measuredHeight)) {
                // Reached the TOP or Bottom
                fab_add.show()
                fab_ai.show()
            }

        })

        fab_ai.apply {
            setImageResource(R.drawable.ic_ai)
            imageTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.white)
        }

        fab_add.apply {
            setImageResource(R.drawable.ic_add)
            imageTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.white)
        }

        fab_ai.setOnClickListener {
            findNavController().navigate(R.id.aiFragment)
        }
        fab_add.setOnClickListener {
            findNavController().navigate(R.id.TransactionFragment)
        }
        val incomeCategories = arrayOf("Income", "Expense")

        val adapterSpinner = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item,
            incomeCategories
        )

        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerExpenseType.adapter = adapterSpinner

        //  Show correct selected item on top
        spinnerExpenseType.setOnTouchListener { _, _ ->
            userInteracted = true
            false
        }
        spinnerExpenseType.setSelection(1)

        spinnerExpenseType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (!userInteracted) return
                    userInteracted = false

                    if (position == 0) {
                        findNavController().navigate(
                            R.id.action_expense_to_income
                        )
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        adapter = TransactionAdapter(
            items = emptyList(),
            iconMap = emptyMap(),
            onItemClick = { transaction ->
                val bundle = Bundle().apply {
                    putString("TRANSACTION_ID", transaction.id)
                }
                findNavController().navigate(R.id.ModifyFragment, bundle)
            },
            onItemLongClick = { transaction ->
                deleteController.confirmAndDelete(transaction)
            }
        )
        categoryViewModel.categories.observe(viewLifecycleOwner) { categories ->
            currentIconMap = categories.associate { it.name to it.iconRes }
            adapter.update(currentTransactions, currentIconMap)
        }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter


        lifecycleScope.launch {
            val dao = AppDatabase.getInstance(requireContext()).transactionDao()
            val repo = TransactionRepository(dao)

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                totalexpense.text = "₹ %.2f".format(repo.totalSpent())

                // Transactions list
                launch {
                    repo.selectAllExpense().collect { list ->
                        currentTransactions = list
                        adapter.update(currentTransactions, currentIconMap)
                    }
                }

                // Pie chart
                launch {
                    val data = repo.getExpensePieData()

                    val categoryMap = data
                        .filter { it.total > 0 }
                        .associate { it.category to it.total.toFloat() }

                    if (categoryMap.isNotEmpty()) {
                        PlotUtils.setupIncomePieChart(ExpensepieChart, categoryMap)
                    } else {
                        ExpensepieChart.clear()
                        ExpensepieChart.setNoDataText("No expense data yet")
                        ExpensepieChart.setNoDataTextColor(Color.WHITE)
                        ExpensepieChart.invalidate()
                    }
                }
            }
        }

    }
}





