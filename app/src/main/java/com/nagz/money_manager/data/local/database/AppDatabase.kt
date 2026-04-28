package com.nagz.money_manager.data.local.database
import androidx.room.*
import com.nagz.money_manager.data.local.entity.TransactionEntity
import com.nagz.money_manager.data.local.converter.TransactionTypeConverter
import com.nagz.money_manager.data.local.dao.TransactionDao
import android.content.Context
import com.nagz.money_manager.data.local.entity.CategoryEntity

@Database(
    entities = [TransactionEntity::class,CategoryEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(TransactionTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "money_manager_db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
    }
}
