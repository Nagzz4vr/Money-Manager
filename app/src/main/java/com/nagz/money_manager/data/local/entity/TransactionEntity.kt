package com.nagz.money_manager.data.local.entity

import android.R
import androidx.room.*
import com.nagz.money_manager.domain.model.TransactionType
import java.util.UUID
@Entity(tableName = "transactions")
data class TransactionEntity(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val amount: Double, // always positive

    val type: TransactionType,

    val category: String,

    val payment_type: Boolean = false,

    val note: String? = null,

    val date: Long, // epoch millis (easy filtering)

    val relatedPerson: String? = null,

    val isSynced: Boolean = false,

    val createdAt: Long = System.currentTimeMillis(),

    val isSettled : Boolean=false
)
