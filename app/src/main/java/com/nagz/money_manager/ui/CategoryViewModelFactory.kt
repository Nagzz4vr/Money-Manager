package com.nagz.money_manager.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nagz.money_manager.data.repository.CategoryRepository
class CategoryViewModelFactory(private val repository: CategoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Option A: Use isAssignableFrom (Standard)
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(repository) as T
        }

        // Option B: Add a secondary check just in case
        if (modelClass == CategoryViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}