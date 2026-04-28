package com.nagz.money_manager.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

import androidx.recyclerview.widget.RecyclerView
import com.nagz.money_manager.R

import com.nagz.money_manager.data.local.entity.CategoryEntity
import com.nagz.money_manager.data.repository.CategoryRepository

import  androidx.fragment.app.viewModels
import com.nagz.money_manager.data.local.database.CategoryDatabase
import androidx.recyclerview.widget.LinearLayoutManager
import android.app.AlertDialog

class DeleteCategoriesFragment: Fragment(R.layout.fragment_delete_categories) {

    private val viewModel: CategoryViewModel by viewModels {
        val database = CategoryDatabase.getInstance(requireContext())
        CategoryViewModelFactory(CategoryRepository(database.categoryDao()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvManageCategories)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        // Pass a lambda to the adapter to handle the delete click
        val adapter = ManageCategoryAdapter { categoryToDelete ->
            showDeleteConfirmation(categoryToDelete)
        }
        recyclerView.adapter = adapter


        viewModel.categories.observe(viewLifecycleOwner) { categories: List<CategoryEntity> ->
            adapter.submitList(categories)
        }
    }


    private fun showDeleteConfirmation(category: CategoryEntity) {
        AlertDialog.Builder(requireContext(),R.style.Theme_MyApp_Dialog)
            .setTitle("Delete Category")
            .setMessage("Are you sure you want to delete '${category.name}'? This will not delete transactions linked to it.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteCategory(category)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}


