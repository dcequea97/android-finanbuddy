package com.example.finanbuddy.domain.repository

import com.example.finanbuddy.domain.data.Resource
import com.example.finanbuddy.domain.data.expense.CategoryModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface CategoriesRepository {
    suspend fun getIncomesCategories(): Resource<List<CategoryModel>>
    suspend fun getExpensesCategories(): Resource<List<CategoryModel>>
    suspend fun initializeDefaultCategories(): Resource<Unit>

    /**
     * Flow-based methods: emit cached data immediately, then update when remote syncs.
     * Default implementations return empty Flow for non-cached repositories.
     */
    fun getIncomesCategoriesFlow(): Flow<List<CategoryModel>> = flowOf(emptyList())
    fun getExpensesCategoriesFlow(): Flow<List<CategoryModel>> = flowOf(emptyList())
}

