package com.nagz.money_manager.ui

import androidx.navigation.fragment.findNavController
import androidx.navigation.NavController
import androidx.fragment.app.Fragment
import android.view.View
import android.os.Bundle
import com.nagz.money_manager.R
import android.widget.Button
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.nagz.money_manager.data.repository.TransactionRepository
import kotlinx.coroutines.launch
import com.nagz.money_manager.data.local.dao.TransactionDao
import com.nagz.money_manager.data.local.database.AppDatabase
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.widget.NestedScrollView
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels

import com.nagz.money_manager.data.local.entity.TransactionEntity
import com.nagz.money_manager.data.repository.CategoryRepository
import com.nagz.money_manager.utils.Delete_Utils
import kotlin.getValue
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import com.nagz.money_manager.data.local.database.CategoryDatabase
import kotlinx.coroutines.flow.StateFlow
class MainFragment : Fragment(R.layout.fragment_main) {
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use View instead of Button
        val incomeBtn = view.findViewById<View>(R.id.nav_income)
        val calendarBtn = view.findViewById<View>(R.id.nav_calendar)
        val expenseBtn = view.findViewById<View>(R.id.nav_expense)
        val fab_ai = view.findViewById<FloatingActionButton>(R.id.fab_ai)
        val fab_add = view.findViewById<FloatingActionButton>(R.id.fab_add)
        val CurrentBalance=view.findViewById<TextView>(R.id.tvTotalBalance)
        val cashBalance=view.findViewById<TextView>(R.id.tvCashBalance)
        val bankBalance=view.findViewById<TextView>(R.id.tvBankBalance)
        val Spent=view.findViewById<TextView>(R.id.Spent)
        val Received=view.findViewById<TextView>(R.id.Received)
        val Lent=view.findViewById<TextView>(R.id.Lent)
        val Saved=view.findViewById<TextView>(R.id.Saved)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerViewRecent)
        val btnMoreOptions = view.findViewById<ImageView>(R.id.btnMoreOptions)

        val cardSpent=view.findViewById<CardView>(R.id.cardSpent)
        val cardReceived=view.findViewById<CardView>(R.id.cardReceived)
        val cardLent=view.findViewById<CardView>(R.id.cardLent)
        val cardSaved=view.findViewById<CardView>(R.id.cardSaved)


        cardSpent.setOnClickListener{
            findNavController().navigate(R.id.TransactionFragment)
        }

        cardReceived.setOnClickListener{
            findNavController().navigate(R.id.TransactionFragment)
        }

        cardLent.setOnClickListener{
            findNavController().navigate(R.id.TransactionFragment)
        }

        cardSaved.setOnClickListener{
            findNavController().navigate(R.id.TransactionFragment)
        }

        val deleteController = Delete_Utils(
            context = requireContext(),
            rootView = requireView(),
            lifecycleOwner = viewLifecycleOwner,
            dao = AppDatabase.getInstance(requireContext()).transactionDao()
        )


//        adapter = TransactionAdapter(
//            items = emptyList(),
//            categoryDao = CategoryDatabase.getInstance(requireContext()).categoryDao(),
//            onItemClick = { transaction ->
//                val bundle = Bundle().apply {
//                    putString("TRANSACTION_ID", transaction.id)
//                }
//                findNavController().navigate(R.id.ModifyFragment, bundle)
//            },
//            onItemLongClick = { transaction ->
//                deleteController.confirmAndDelete(transaction)
//
//            }
//        )

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







        val scroll =view.findViewById<NestedScrollView>(R.id.Scroll)


        scroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener {
                v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY) {
                fab_add.hide()
                fab_ai.hide()
            } else if (scrollY < oldScrollY) {
                // Scroll UP
                fab_add.hide()
                fab_ai.hide()
            }

            if (scrollY == 0 ||scrollY == (v.getChildAt(0).measuredHeight - v.measuredHeight)) {
                // Reached the TOP or Bottom
                fab_add.show()
                fab_ai.show()
            }


        })

        btnMoreOptions.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_settings)
        }

        incomeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_income)
        }

        calendarBtn.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_calendar)
        }

        expenseBtn.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_expense)
        }
        fab_ai.apply {
            setImageResource(R.drawable.ic_ai)
            imageTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.white)
        }

        fab_add.apply {
            setImageResource(R.drawable.ic_add)
            imageTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.white)
        }

        fab_ai.setOnClickListener{
            findNavController().navigate(R.id.aiFragment)
        }
        fab_add.setOnClickListener{
            findNavController().navigate(R.id.TransactionFragment)
        }




        // Inside onViewCreated

        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            val dao = AppDatabase.getInstance(requireContext()).transactionDao()
            val repo = TransactionRepository(dao)

            // Wrap in repeatOnLifecycle to handle navigating back and forth
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Launch balance updates in parallel so they don't block the list
                launch {
                    Received.text = "₹ %.2f".format(repo.totalReceived())
                    CurrentBalance.text = "₹ %.2f".format(repo.currentBalance())
                    Spent.text = "₹ %.2f".format(repo.totalSpent())
                    Lent.text = "₹ %.2f".format(repo.totalLent())
                    Saved.text = "₹ %.2f".format(repo.totalSaved())
                    cashBalance.text = "₹ %.2f".format(repo.currentCashBalance())
                    bankBalance.text = "₹ %.2f".format(repo.currentBankBalance())
                }

                // Collect the recent transactions
                repo.getRecent20Flow().collect { list ->
                    currentTransactions = list
                    adapter.update(currentTransactions, currentIconMap)
                }

            }
        }



    }


    }
