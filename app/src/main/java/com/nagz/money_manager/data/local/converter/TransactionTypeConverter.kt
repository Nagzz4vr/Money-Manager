package com.nagz.money_manager.data.local.converter

import androidx.room.TypeConverter
import com.nagz.money_manager.domain.model.TransactionType

class TransactionTypeConverter {

    @TypeConverter
    fun fromType(type: TransactionType): String = type.name

    @TypeConverter
    fun toType(value: String): TransactionType =
        TransactionType.valueOf(value)
}
