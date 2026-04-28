package com.nagz.money_manager.data.local.dao

import com.nagz.money_manager.data.local.entity.TransactionEntity

import androidx.room.*
import com.nagz.money_manager.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow



@Dao
interface TransactionDao {

    // ---- CRUD ----

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("DELETE FROM transactions")
    suspend fun clearAll()

    // ---- Queries ----

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    suspend fun getAll(): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE type = :type")
    suspend fun getByType(type: TransactionType): List<TransactionEntity>

    @Query("""
    SELECT * FROM transactions
    WHERE date BETWEEN :startOfDay AND :startOfNextDay
    ORDER BY date DESC
""")
    fun getTransactionsForDay(
        startOfDay: Long,
        startOfNextDay: Long
    ): Flow<List<TransactionEntity>>



    @Query("SELECT * FROM transactions WHERE category = :category")
    suspend fun getByCategory(category: String): List<TransactionEntity>

    // ---- Aggregations (for balance logic) ----
    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT 20")
    suspend fun getRecent20Static(): List<TransactionEntity>
    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type")
    suspend fun sumByType(type: TransactionType): Double?

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: String): TransactionEntity?


    @Query("SELECT DISTINCT category FROM transactions WHERE type = :type")
    suspend fun getDistinctCategoriesByType(type: String): List<String>

    @Query("""
    SELECT SUM(amount)
    FROM transactions
    WHERE type = :transactionType
      AND payment_type = :isCash
""")
    suspend fun sumByTypeAndPayment(
        transactionType: TransactionType,
        isCash: Boolean
    ): Double?

    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT 20")
    fun getRecent20Flow(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun selectAllByType(type: TransactionType): Flow<List<TransactionEntity>>

    @Query("""
    SELECT category, SUM(amount) AS total 
    FROM transactions 
    WHERE type = :type
    GROUP BY category
""")
    suspend fun getCategoryTotals(type: String): List<CategoryAmount>


//    @Query("""
//    SELECT transactions.*, category.icon_res
//    FROM transactions
//    LEFT JOIN category ON transactions.category = category.name
//""")
//    fun getAllTransactionsWithIcons(): Flow<List<TransactionWithIcon>>


}
