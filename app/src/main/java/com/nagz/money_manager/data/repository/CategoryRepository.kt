package com.nagz.money_manager.data.repository



import com.nagz.money_manager.data.local.dao.CategoryDao
import com.nagz.money_manager.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow
class CategoryRepository(private val dao: CategoryDao) {

    // 1. Get all categories as a stream (updates automatically when data changes)
    val allCategories: Flow<List<CategoryEntity>> = dao.getAllCategories()

    // 2. Insert a new category (must be suspend for background execution)
    suspend fun insertCategory(category: CategoryEntity) {
        dao.insertCategory(category)
    }

    // 3. Update an existing category
    suspend fun updateCategory(category: CategoryEntity) {
        dao.updateCategory(category)
    }

    // 4. Delete a category
    suspend fun deleteCategory(category: CategoryEntity) {
        dao.deleteCategory(category)
    }

    suspend fun isCategoryInUse(name:String): Boolean {
        return dao.isCategoryUsed(name) > 0
    }

    suspend fun getCategoryIcon(name: String): Int?{

        return dao.getIconResByName(name)}


}