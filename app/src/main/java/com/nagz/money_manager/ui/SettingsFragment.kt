package com.nagz.money_manager.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.nagz.money_manager.R
import com.nagz.money_manager.domain.model.TransactionType
import android.widget.Button
import com.nagz.money_manager.data.local.entity.TransactionEntity
import android.widget.Toast
import android.content.Context
import java.io.File
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nagz.money_manager.data.local.database.AppDatabase
import kotlinx.coroutines.launch
import com.nagz.money_manager.data.repository.TransactionRepository
class SettingsFragment: Fragment(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBackup = view.findViewById<Button>(R.id.btnBackup)
        val btnClearDb= view.findViewById<Button>(R.id.btnClearDb)
        val btnManageCategories= view.findViewById<Button>(R.id.btnManageCategories)
        val btnSync= view.findViewById<Button>(R.id.btnSync)


        btnManageCategories.setOnClickListener { findNavController().navigate(R.id.AddCategories) }

        btnSync.setOnClickListener {
            Toast.makeText(requireContext(), "This feature is not yet created the dev is lazy", Toast.LENGTH_SHORT).show()
        }


        btnBackup.setOnClickListener {
            lifecycleScope.launch {
                val dao = AppDatabase.getInstance(requireContext()).transactionDao()
                val repo = TransactionRepository(dao)
                val allTransactions = repo.getAll()
                val csv = transactionsToCsv(allTransactions)
                saveCsvToDownloads(requireContext(), csv)
            }
        }

        btnClearDb.setOnClickListener {
            lifecycleScope.launch {
                val dao = AppDatabase.getInstance(requireContext()).transactionDao()
                val repo = TransactionRepository(dao)

                repo.clearall()

                Toast.makeText(requireContext(), "Database cleared!", Toast.LENGTH_SHORT).show()
            }
        }



    }

    fun transactionsToCsv(transactions: List<TransactionEntity>): String {
        val sb = StringBuilder()
        // Header
        sb.append("ID,Amount,Type,Category,PaymentType,Note,Date,RelatedPerson,IsSettled\n")

        transactions.forEach { tx ->
            sb.append("${tx.id},")
            sb.append("${tx.amount},")
            sb.append("${tx.type},")
            sb.append("${tx.category},")
            sb.append("${if (tx.payment_type) "Bank" else "Cash"},")
            sb.append("${tx.note ?: ""},")
            sb.append("${tx.date},")
            sb.append("${tx.relatedPerson ?: ""},")
            sb.append("${tx.isSettled}\n")
        }
        return sb.toString()
    }
    fun saveCsvToDownloads(context: Context, csv: String, fileName: String = "transactions.csv") {
        val downloads = context.getExternalFilesDir(null)  // Or Environment.DIRECTORY_DOWNLOADS for Android 10+
        val file = File(downloads, fileName)
        file.writeText(csv)
        Toast.makeText(context, "CSV saved: ${file.absolutePath}", Toast.LENGTH_LONG).show()
    }


}
