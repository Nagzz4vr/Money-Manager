package com.nagz.money_manager.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import com.nagz.money_manager.R
import com.nagz.money_manager.data.local.entity.CategoryEntity
import com.nagz.money_manager.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val repository: CategoryRepository
) : ViewModel() {

    // Existing (unchanged)
    val categories = repository.allCategories.asLiveData()

    private val _iconMap = MutableStateFlow<Map<String, Int>>(emptyMap())
    val iconMap: StateFlow<Map<String, Int>> = _iconMap

    init {
        viewModelScope.launch {
            repository.allCategories.collect { list ->
                _iconMap.value = list.associate {
                    it.name to it.iconRes
                }
            }
        }
    }


    fun getCategoryIcon(name: String?): Int {
        if (name.isNullOrBlank()) return R.drawable.ic_others
        return _iconMap.value[name] ?: R.drawable.ic_others
    }

    fun insertCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.insertCategory(category)
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    fun isCategoryInUse(name: String, result: (Boolean) -> Unit) {
        viewModelScope.launch {
            result(repository.isCategoryInUse(name))
        }
    }
}
