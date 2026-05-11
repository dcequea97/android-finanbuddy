package com.example.finanbuddy.data.repository.sheets

import com.example.finanbuddy.data.remote.model.toDomain
import com.example.finanbuddy.data.remote.network.FirebaseIdTokenProvider
import com.example.finanbuddy.data.remote.network.SheetsApi
import com.example.finanbuddy.domain.data.Resource
import com.example.finanbuddy.domain.data.expense.CategoryModel
import com.example.finanbuddy.domain.repository.CategoriesRepository

class SheetsCategoriesRepository(
    private val api: SheetsApi,
    private val idTokenProvider: FirebaseIdTokenProvider
): CategoriesRepository {
    override suspend fun getIncomesCategories(): Resource<List<CategoryModel>> {
        return Resource.Success(
            listOf()
        )
    }

    override suspend fun getExpensesCategories(): Resource<List<CategoryModel>> {
        val idToken = idTokenProvider.getIdToken(forceRefresh = false)
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: return Resource.Error("User is not authenticated")

        try {
            val response = api.getCategories(idToken = idToken)

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