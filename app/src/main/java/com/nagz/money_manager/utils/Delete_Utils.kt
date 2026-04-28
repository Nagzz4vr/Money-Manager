package com.nagz.money_manager.utils

import androidx.lifecycle.lifecycleScope
import com.nagz.money_manager.data.local.database.AppDatabase
import com.nagz.money_manager.data.local.entity.TransactionEntity
import com.nagz.money_manager.data.repository.TransactionRepository
import kotlinx.coroutines.launch
import androidx.core.content.ContentProviderCompat.requireContext
import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.nagz.money_manager.data.local.dao.TransactionDao
import android.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.nagz.money_manager.R

class Delete_Utils (private val context: Context,
private val rootView: View,
private val lifecycleOwner: LifecycleOwner,
private val dao: TransactionDao
) {

    fun confirmAndDelete(transaction: TransactionEntity) {
        AlertDialog.Builder(context, R.style.Theme_MyApp_Dialog)
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this entry?")
            .setPositiveButton("Delete") { _, _ ->
                deleteWithUndo(transaction)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteWithUndo(transaction: TransactionEntity) {
        lifecycleOwner.lifecycleScope.launch {
            dao.delete(transaction)

            Snackbar.make(
                rootView,
                "Transaction deleted",
                Snackbar.LENGTH_LONG
            ).setAction("UNDO") {
                lifecycleOwner.lifecycleScope.launch {
                    dao.insert(transaction)
                }
            }.show()
        }
    }
}