package com.nagz.money_manager.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.nagz.money_manager.R
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.nagz.money_manager.data.local.database.AppDatabase
import com.nagz.money_manager.data.local.entity.CategoryEntity
import com.nagz.money_manager.data.repository.CategoryRepository
import com.nagz.money_manager.data.repository.TransactionRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import  androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nagz.money_manager.data.local.database.CategoryDatabase

class AddCategoriesFragment : Fragment(R.layout.fragment_categories) {
    private var selectedIcon: Int = -1
    private var selectedColor: String = ""

    private var userInteracted:Boolean=false
    private val viewModel: CategoryViewModel by viewModels {
        val database = CategoryDatabase.getInstance(requireContext())
        CategoryViewModelFactory(CategoryRepository(database.categoryDao()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val colorPalette = listOf("#7F35FF", "#FF5722", "#4CAF50", "#2196F3", "#E91E63", "#FFC107")
        val iconList = listOf(
            R.drawable.ic_food, R.drawable.ic_transport, R.drawable.ic_entertainment,
            R.drawable.ic_housing, R.drawable.ic_utilities, R.drawable.ic_medical,
            R.drawable.ic_insurance, R.drawable.ic_personal_spending, R.drawable.ic_savings,
            R.drawable.ic_home, R.drawable.ic_calendar,
             R.drawable.ic_day, R.drawable.ic_night,
            R.drawable.ic_share, R.drawable.ic_others,R.drawable.ic_groceries
        )

        val spinnerCategories = view.findViewById<Spinner>(R.id.spinnerCategories)

        val rvIcons = view.findViewById<RecyclerView>(R.id.rvIcons)
        val rvColors = view.findViewById<RecyclerView>(R.id.rvColors)

        // Setup Icons
        rvIcons.layoutManager = GridLayoutManager(requireContext(), 4)
        rvIcons.adapter = IconAdapter(iconList) { iconResId ->
            selectedIcon = iconResId
        }

        // FIX: Use ColorAdapter here instead of IconAdapter
        rvColors.layoutManager = GridLayoutManager(requireContext(), 3)
        rvColors.adapter = ColorAdapter(colorPalette) { colorHex ->
            selectedColor = colorHex
        }

        // Handle Save Button
        view.findViewById<View>(R.id.btnSaveCategory).setOnClickListener {
            saveCategory()
        }

        val Categories = arrayOf("Add", "Delete")

        val adapterSpinner = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item,
            Categories
        )

        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategories.adapter = adapterSpinner

        //  Show correct selected item on top
        spinnerCategories.setOnTouchListener { _, _ ->
            userInteracted = true
            false
        }
        spinnerCategories.setSelection(0)

        spinnerCategories.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (!userInteracted) return
                    userInteracted = false

                    if (position == 1) {
                        findNavController().navigate(
                            R.id.DeleteCategories
                        )
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
    }

    private fun saveCategory() {
        val etName = view?.findViewById<TextInputEditText>(R.id.etCategoryName)
        val name = etName?.text?.toString()?.trim().orEmpty()

        // Validation
        if (name.isEmpty() || selectedIcon == -1 || selectedColor.isEmpty()) {
            Toast.makeText(context, "Please complete all fields", Toast.LENGTH_SHORT).show()
            return
        }

        //  Check duplicate category name
        viewModel.isCategoryInUse(name) { exists ->
            if (exists) {
                Toast.makeText(context, "Category already exists", Toast.LENGTH_SHORT).show()
                return@isCategoryInUse
            }

            // Convert color safely
            val colorInt = try {
                android.graphics.Color.parseColor(selectedColor)
            } catch (e: Exception) {
                android.graphics.Color.GRAY
            }

            // Create entity
            val newCategory = CategoryEntity(
                name = name,
                iconRes = selectedIcon,
                color = colorInt,
                isDefault = false
            )

            // ️Insert
            viewModel.insertCategory(newCategory)

            Toast.makeText(context, "Category saved successfully", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

}