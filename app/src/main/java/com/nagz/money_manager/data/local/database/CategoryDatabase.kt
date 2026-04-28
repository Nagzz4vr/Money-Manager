package com.nagz.money_manager.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nagz.money_manager.data.local.dao.CategoryDao
import com.nagz.money_manager.data.local.entity.CategoryEntity
import com.nagz.money_manager.data.local.entity.TransactionEntity

// 1. List all your entities here. Update the version if you change the schema later.
@Database(entities = [CategoryEntity::class, TransactionEntity::class], version = 1, exportSchema = false)
abstract class CategoryDatabase : RoomDatabase() {

    // 2. Connect your DAOs
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: CategoryDatabase? = null

        // 3. Singleton pattern to prevent multiple instances of the database opening at once
        fun getInstance(context: Context): CategoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CategoryDatabase::class.java,
                    "category_db" // The name of your database file
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}