package com.nagz.money_manager.data.repository

import androidx.room.*
import com.nagz.money_manager.data.local.entity.TransactionEntity
import com.nagz.money_manager.data.local.converter.TransactionTypeConverter
import com.nagz.money_manager.data.local.dao.TransactionDao
import android.content.*
import com.nagz.money_manager.domain.model.TransactionType
import  kotlinx.coroutines.flow.Flow
import com.nagz.money_manager.data.local.dao.CategoryAmount
class TransactionRepository(
    private val dao: TransactionDao
) {

    // CRUD
    suspend fun add(tx: TransactionEntity) = dao.insert(tx)
    suspend fun edit(tx: TransactionEntity) = dao.update(tx)
    suspend fun delete(tx: TransactionEntity) = dao.delete(tx)
    suspend fun clearall()=dao.clearAll()

    // Queries
    suspend fun getAll() = dao.getAll()
    suspend fun getByType(type: TransactionType) = dao.getByType(type)



    suspend fun getTransactionById(id: String) =
        dao.getTransactionById(id)


    fun getTransactionsForDay(
        startOfDay: Long,
        startOfNextDay: Long
    ) = dao.getTransactionsForDay(startOfDay, startOfNextDay)




    suspend fun getByCategory(category: String) =
        dao.getByCategory(category)



    // ---- Computed values (NOT stored) ----

    suspend fun totalSpent(): Double =
        dao.sumByType(TransactionType.SPENT) ?: 0.0

    suspend fun totalReceived(): Double =
        dao.sumByType(TransactionType.RECEIVED) ?: 0.0

    suspend fun totalLent(): Double =
        dao.sumByType(TransactionType.LENT) ?: 0.0

    suspend fun totalSaved(): Double =
        dao.sumByType(TransactionType.SAVED) ?: 0.0

    suspend fun currentBalance(): Double {
        val received = totalReceived()
        val spent = totalSpent()
        val lent = totalLent()
        val saved = dao.sumByType(TransactionType.SAVED) ?: 0.0
        val gifted = dao.sumByType(TransactionType.GIFTED) ?: 0.0

        return received - spent - lent - saved - gifted
    }

    suspend fun currentBankBalance(): Double {
        val received = dao.sumByTypeAndPayment(TransactionType.RECEIVED, true) ?: 0.0
        val spent    = dao.sumByTypeAndPayment(TransactionType.SPENT, true) ?: 0.0
        val lent     = dao.sumByTypeAndPayment(TransactionType.LENT, true) ?: 0.0
        val saved    = dao.sumByTypeAndPayment(TransactionType.SAVED, true) ?: 0.0
        val gifted   = dao.sumByTypeAndPayment(TransactionType.GIFTED, true) ?: 0.0

        return received - spent - lent - saved - gifted
    }
    suspend fun currentCashBalance(): Double {
        val received = dao.sumByTypeAndPayment(TransactionType.RECEIVED, false) ?: 0.0
        val spent    = dao.sumByTypeAndPayment(TransactionType.SPENT, false) ?: 0.0
        val lent     = dao.sumByTypeAndPayment(TransactionType.LENT, false) ?: 0.0
        val saved    = dao.sumByTypeAndPayment(TransactionType.SAVED, false) ?: 0.0
        val gifted   = dao.sumByTypeAndPayment(TransactionType.GIFTED, false) ?: 0.0

        return received - spent - lent - saved - gifted
    }
    fun getRecent20Flow(): Flow<List<TransactionEntity>> = dao.getRecent20Flow()

    suspend fun getRecent20List(): List<TransactionEntity> = dao.getRecent20Static()

    fun selectAllIncome(): Flow<List<TransactionEntity>> =
        dao.selectAllByType(TransactionType.RECEIVED)

    fun selectAllExpense(): Flow<List<TransactionEntity>> =
        dao.selectAllByType(TransactionType.SPENT)

    suspend fun getIncomePieData(): List<CategoryAmount> {
        return dao.getCategoryTotals(TransactionType.RECEIVED.name)
    }

    suspend fun getExpensePieData(): List<CategoryAmount> {
        return dao.getCategoryTotals(TransactionType.SPENT.name)
    }

//    suspend fun getallcategoriesicon():Flow<List<TransactionWithIcon>>



}
