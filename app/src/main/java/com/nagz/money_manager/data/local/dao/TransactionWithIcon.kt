package com.nagz.money_manager.data.local.dao
import com.nagz.money_manager.data.local.entity.TransactionEntity
import androidx.room.Embedded
import androidx.room.ColumnInfo
data class TransactionWithIcon(
    val id: String,
    val amount: Double,
    val category: String,
    val iconRes: Int,
    val createdAt: Long
)