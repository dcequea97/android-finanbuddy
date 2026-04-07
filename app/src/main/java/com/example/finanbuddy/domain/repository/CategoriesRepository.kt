package com.example.finanbuddy.domain.repository

import com.example.finanbuddy.domain.data.Resource
import com.example.finanbuddy.domain.data.expense.CategoryModel

interface CategoriesRepository {
    suspend fun getIncomesCategories(): Resource<List<CategoryModel>>
    suspend fun getExpensesCategories(): Resource<List<CategoryModel>>
    suspend fun initializeDefaultCategories(): Resource<Unit>
}