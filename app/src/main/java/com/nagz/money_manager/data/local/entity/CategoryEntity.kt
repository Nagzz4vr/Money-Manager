package com.nagz.money_manager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID
import androidx.room.*

@Entity(
    tableName = "category",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class CategoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "category_id")
    val categoryId: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "icon_res")
    val iconRes: Int,

    @ColumnInfo(name = "color")
    val color: Int,

    @ColumnInfo(name = "is_default")
    val isDefault: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
