package com.nagz.money_manager.data.local.dao
import com.nagz.money_manager.data.local.entity.TransactionEntity

import androidx.room.*
import com.nagz.money_manager.data.local.entity.CategoryEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCategory(category: CategoryEntity)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("SELECT * FROM category")
    fun getAllCategories(): Flow<List<CategoryEntity>>


    @Query("""
    SELECT COUNT(*) FROM category
    WHERE name = :categoryName
""")
    suspend fun isCategoryUsed(categoryName: String): Int

    @Query("SELECT icon_res FROM category WHERE name = :name")
    fun getIconResByName(name: String): Int?

    @Query("SELECT * FROM category")
    suspend fun getAllCategoriesOnce(): List<CategoryEntity>



}
