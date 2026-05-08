package com.example.finanbuddy.data.repository.sheets

import com.example.finanbuddy.data.remote.model.toDomain
import com.example.finanbuddy.data.remote.network.SheetsApi
import com.example.finanbuddy.domain.data.Resource
import com.example.finanbuddy.domain.data.expense.CategoryModel
import com.example.finanbuddy.domain.repository.CategoriesRepository

class SheetsCategoriesRepository(
    val api: SheetsApi
): CategoriesRepository {
    override suspend fun getIncomesCategories(): Resource<List<CategoryModel>> {
        return Resource.Success(
            listOf()
        )
    }

    override suspend fun getExpensesCategories(): Resource<List<CategoryModel>> {
        try {
            val response = api.getCategories()

            if (response.isSuccessful) {
                val categories = response.body()?.map { it.toDomain() } ?: emptyList()
                return Resource.Success(categories)
            } else {
                return Resource.Error("Failed to fetch categories: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            return Resource.Error("An error occurred while fetching categories: ${e.localizedMessage}")
        }
    }

    override suspend fun initializeDefaultCategories(): Resource<Unit> {
        return Resource.Success(Unit)
    }

}