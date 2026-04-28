package com.nagz.money_manager.ui
import android.view.*
import android.os.Bundle
import android.widget.TextView
import com.nagz.money_manager.R
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.nagz.money_manager.data.repository.TransactionRepository
import kotlinx.coroutines.launch
import com.nagz.money_manager.data.local.database.AppDatabase
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.widget.NestedScrollView
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.Spinner
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nagz.money_manager.data.local.entity.TransactionEntity
import com.nagz.money_manager.utils.Delete_Utils
import com.github.mikephil.charting.charts.PieChart
import  com.github.mikephil.charting.data.PieEntry
import com.nagz.money_manager.utils.PlotUtils
import android.graphics.Color
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.nagz.money_manager.data.local.database.CategoryDatabase
import com.nagz.money_manager.data.repository.CategoryRepository
import kotlin.getValue

class IncomeFragment : Fragment(R.layout.fragment_income) {


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

    private lateinit var adapter: TransactionAdapter
    private var userInteracted = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerViewIncome)
        val totalIncome = view.findViewById<TextView>(R.id.total_income)
        val spinnerIncomeType = view.findViewById<Spinner>(R.id.spinnerIncomeType)
        val fab_ai = view.findViewById<FloatingActionButton>(R.id.fab_ai)
        val fab_add = view.findViewById<FloatingActionButton>(R.id.fab_add)

        val IncomeScroll = view.findViewById<NestedScrollView>(R.id.IncomeScroll)

        val deleteController = Delete_Utils(
            context = requireContext(),
            rootView = requireView(),
            lifecycleOwner = viewLifecycleOwner,
            dao = AppDatabase.getInstance(requireContext()).transactionDao()
        )

        val incomepieChart = view.findViewById<PieChart>(R.id.incomepieChart)






        IncomeScroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
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
        spinnerIncomeType.adapter = adapterSpinner

        //  Show correct selected item on top
        spinnerIncomeType.setOnTouchListener { _, _ ->
            userInteracted = true
            false
        }
        spinnerIncomeType.setSelection(0)

        spinnerIncomeType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (!userInteracted) return
                    userInteracted = false

                    if (position == 1) {
                        findNavController().navigate(
                            R.id.action_income_to_expense
                        )
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        // Recycler

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
                totalIncome.text = "₹ %.2f".format(repo.totalReceived())

                //  Observe transactions (LIST)
                launch {
                    repo.selectAllIncome().collect { list ->
                        currentTransactions = list
                        adapter.update(currentTransactions, currentIconMap)
                    }
                }


                launch {
                    val data = repo.getIncomePieData()

                    val categoryMap = data
                        .filter { it.total > 0 }
                        .associate { it.category to it.total.toFloat() }

                    if (categoryMap.isNotEmpty()) {
                        PlotUtils.setupIncomePieChart(incomepieChart, categoryMap)
                    } else {
                        incomepieChart.clear()
                        incomepieChart.setNoDataText("No income data yet")
                        incomepieChart.setNoDataTextColor(Color.WHITE)
                        incomepieChart.invalidate()
                    }
                }
            }
        }
    }
    }




