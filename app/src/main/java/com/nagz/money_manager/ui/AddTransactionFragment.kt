package com.nagz.money_manager.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import com.nagz.money_manager.R
import java.util.Calendar
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.google.android.material.materialswitch.MaterialSwitch
import com.nagz.money_manager.data.local.entity.TransactionEntity
import com.nagz.money_manager.domain.model.TransactionType
import androidx.lifecycle.lifecycleScope
import com.nagz.money_manager.data.local.database.AppDatabase
import com.nagz.money_manager.data.repository.TransactionRepository
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import com.nagz.money_manager.data.local.database.CategoryDatabase
import com.nagz.money_manager.data.repository.CategoryRepository
import kotlin.getValue
import android.widget.ArrayAdapter
import com.nagz.money_manager.ui.CategoryDropdownAdapter
import android.widget.AutoCompleteTextView

class AddTransactionFragment : Fragment(R.layout.fragment_transaction) {
    private var selectedDateMillis: Long = System.currentTimeMillis()
    private var selectedType: TransactionType = TransactionType.SPENT

    private val viewModel: CategoryViewModel by viewModels {
        val database = CategoryDatabase.getInstance(requireContext())
        CategoryViewModelFactory(CategoryRepository(database.categoryDao()))
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnDatePicker = view.findViewById<View>(R.id.btnDatePicker)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        val etAmount=view.findViewById<TextView>(R.id.etAmount)
        val rbBank =view.findViewById<RadioButton>(R.id.rbBank)
        val rbCash=view.findViewById<RadioButton>(R.id.rbCash)
        val actvCategory = view.findViewById<AutoCompleteTextView>(R.id.actvCategory)
        val etPerson = view.findViewById<TextView>(R.id.etPerson)
        val etNote = view.findViewById<TextView>(R.id.etNote)
        val switchSettled=view.findViewById<MaterialSwitch>(R.id.switchSettled)
        val save=view.findViewById<Button>(R.id.btnSave)
        val btnSpent=view.findViewById<Button>(R.id.btnSpent)
        val btnSaved=view.findViewById<Button>(R.id.btnSaved)
        val btnLent=view.findViewById<Button>(R.id.btnLent)
        val btnReceived=view.findViewById<Button>(R.id.btnReceived)



        viewModel.categories.observe(viewLifecycleOwner) { list ->
            // 1. Pass the actual 'list' of entities, NOT 'categoryNames'
            val iconMap = list.associate { it.name to it.iconRes }

            val adapter = CategoryDropdownAdapter(requireContext(), list, iconMap)
            actvCategory.setAdapter(adapter)

            // 2. Setup behavior
            actvCategory.threshold = 1
            actvCategory.setOnClickListener {
                if (!actvCategory.isPopupShowing) {
                    actvCategory.showDropDown()
                }
            }
        }


        btnSpent.setOnClickListener {
            selectedType = TransactionType.SPENT

        }

        btnSaved.setOnClickListener {
            selectedType = TransactionType.SAVED

        }

        btnLent.setOnClickListener {
            selectedType = TransactionType.LENT

        }

        btnReceived.setOnClickListener {
            selectedType = TransactionType.RECEIVED
        }



        btnDatePicker.setOnClickListener {
            val c = Calendar.getInstance()

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, day ->

                    val cal = Calendar.getInstance()
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, month)
                    cal.set(Calendar.DAY_OF_MONTH, day)
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)

                    selectedDateMillis = cal.timeInMillis

                    tvDate.text = "%02d/%02d/%04d".format(day, month + 1, year)
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.show()
        }




        save.setOnClickListener {

            val amountText = etAmount.text.toString()
            if (amountText.isBlank()) {
                etAmount.error = "Enter amount"
                return@setOnClickListener
            }

            val amount = amountText.toDouble()
            val isBank = rbBank.isChecked
            val isCash = rbBank.isChecked
            val person = etPerson.text.toString().ifBlank { null }
            val note = etNote.text.toString().ifBlank { null }
            val isSettled = switchSettled.isChecked
            val category = actvCategory.text.toString().ifBlank { "" }




            val tx = TransactionEntity(
                amount = amount,
                type = selectedType, // later dynamic
                category = category,
                payment_type = isBank,
                note = note,
                date = selectedDateMillis,
                relatedPerson = person,
                isSettled = isSettled
            )

            viewLifecycleOwner.lifecycleScope.launch {
                val dao = AppDatabase.getInstance(requireContext()).transactionDao()
                val repo = TransactionRepository(dao)
                repo.add(tx)

                findNavController().popBackStack() // back to MainFragment
            }
        }







    }
}
