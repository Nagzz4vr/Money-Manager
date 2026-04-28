package com.nagz.money_manager.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.materialswitch.MaterialSwitch
import com.nagz.money_manager.R
import com.nagz.money_manager.data.local.database.AppDatabase
import com.nagz.money_manager.data.local.database.CategoryDatabase
import com.nagz.money_manager.data.local.entity.TransactionEntity
import com.nagz.money_manager.data.repository.CategoryRepository
import com.nagz.money_manager.data.repository.TransactionRepository
import com.nagz.money_manager.domain.model.TransactionType
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.getValue

class ModifyFragment : Fragment(R.layout.fragment_modify_transaction) {

    private lateinit var originalTx: TransactionEntity
    private var selectedDateMillis: Long = System.currentTimeMillis()
    private var selectedType: TransactionType = TransactionType.SPENT

    private val viewModel: CategoryViewModel by viewModels {
        val database = CategoryDatabase.getInstance(requireContext())
        CategoryViewModelFactory(CategoryRepository(database.categoryDao()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* ---------- ARGUMENT ---------- */

        val transactionId = arguments?.getString("TRANSACTION_ID")
            ?: run {
                findNavController().popBackStack()
                return
            }

        /* ---------- VIEWS ---------- */

        val btnDelete = view.findViewById<Button>(R.id.btnDelete)
        val btnUpdate = view.findViewById<Button>(R.id.btnUpdate)

        val btnSpent = view.findViewById<Button>(R.id.btnSpent)
        val btnSaved = view.findViewById<Button>(R.id.btnSaved)
        val btnLent = view.findViewById<Button>(R.id.btnLent)
        val btnReceived = view.findViewById<Button>(R.id.btnReceived)

        val etAmount = view.findViewById<EditText>(R.id.etAmount)
        val rbBank = view.findViewById<RadioButton>(R.id.rbBank)
        val rbCash = view.findViewById<RadioButton>(R.id.rbCash)

        val etCategory =  view.findViewById<AutoCompleteTextView>(R.id.actvCategory)
        val etNote = view.findViewById<EditText>(R.id.etNote)


        val btnDatePicker = view.findViewById<View>(R.id.btnDatePicker)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)





        /* ---------- DB ---------- */

        val dao = AppDatabase.getInstance(requireContext()).transactionDao()
        val repo = TransactionRepository(dao)

        /* ---------- LOAD TRANSACTION ---------- */

        lifecycleScope.launch {
            val tx = repo.getTransactionById(transactionId)
                ?: run {
                    findNavController().popBackStack()
                    return@launch
                }

            originalTx = tx

            etAmount.setText(tx.amount.toString())
            etCategory.setText(tx.category, false)
            etNote.setText(tx.note ?: "")

            selectedType = tx.type
            selectedDateMillis = tx.date

            when (tx.type) {
                TransactionType.SPENT -> btnSpent.performClick()
                TransactionType.SAVED -> btnSaved.performClick()
                TransactionType.LENT -> btnLent.performClick()
                TransactionType.RECEIVED -> btnReceived.performClick()
                TransactionType.GIFTED -> {
                    // If you have a Gifted button
                    // btnGifted.performClick()

                    // fallback (safe)
                    selectedType = TransactionType.GIFTED
                }
            }

            if (tx.payment_type) rbBank.isChecked = true else rbCash.isChecked = true

            val cal = Calendar.getInstance().apply { timeInMillis = tx.date }
        }

        /* ---------- TYPE BUTTONS ---------- */

        btnSpent.setOnClickListener { selectedType = TransactionType.SPENT }
        btnSaved.setOnClickListener { selectedType = TransactionType.SAVED }
        btnLent.setOnClickListener { selectedType = TransactionType.LENT }
        btnReceived.setOnClickListener { selectedType = TransactionType.RECEIVED }

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

        /* ---------- DATE PICKER ---------- */

        viewModel.categories.observe(viewLifecycleOwner) { list ->
            // 1. Pass the actual 'list' of entities, NOT 'categoryNames'
            val iconMap = list.associate { it.name to it.iconRes }

            val adapter = CategoryDropdownAdapter(requireContext(), list, iconMap)
            etCategory.setAdapter(adapter)

            // 2. Setup behavior
            etCategory.threshold = 1
            etCategory.setOnClickListener {
                if (!etCategory.isPopupShowing) {
                    etCategory.showDropDown()
                }
            }
        }



        /* ---------- UPDATE ---------- */

        btnUpdate.setOnClickListener {

            val amount = etAmount.text.toString().toDoubleOrNull()
            if (amount == null) {
                etAmount.error = "Invalid amount"
                return@setOnClickListener
            }

            val category = etCategory.text.toString()
            if (category.isBlank()) {
                etCategory.error = "Category required"
                return@setOnClickListener
            }

            val updatedTx = originalTx.copy(
                amount = amount,
                type = selectedType,
                category = category,
                payment_type = rbBank.isChecked,
                note = etNote.text.toString().ifBlank { null },
                date = selectedDateMillis,
            )

            lifecycleScope.launch {
                repo.edit(updatedTx)
                findNavController().popBackStack()
            }
        }

        /* ---------- DELETE ---------- */

        btnDelete.setOnClickListener {
            lifecycleScope.launch {
                repo.delete(originalTx)
                findNavController().popBackStack()
            }
        }
    }
}
