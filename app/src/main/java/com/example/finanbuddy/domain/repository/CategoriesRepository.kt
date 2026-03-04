package com.example.finanbuddy.domain.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.ui.graphics.Color
import com.example.finanbuddy.domain.data.Resource
import com.example.finanbuddy.domain.data.expense.CategoryModel

interface CategoriesRepository {
    //    suspend fun getExpenses(): List<Expense>
    suspend fun getCategories(): Resource<List<CategoryModel>>

}

class ExpenseRepositoryDummy() : CategoriesRepository {
    override suspend fun getCategories(): Resource<List<CategoryModel>> {
        return Resource.Success(
            listOf(
                CategoryModel(
                    "food",
                    "Food",
                    "Groceries, Dining",
                    Icons.Outlined.Restaurant,
                    Color(0xFFEF4444)
                ),
                CategoryModel(
                    "transport",
                    "Transport",
                    "Uber, Gas, Train",
                    Icons.Outlined.DirectionsBus,
                    Color(0xFF6B7280)
                ),
                CategoryModel(
                    "rent",
                    "Rent",
                    "Housing, Bills",
                    Icons.Outlined.Home,
                    Color(0xFF6B7280)
                ),
                CategoryModel(
                    "shopping",
                    "Shopping",
                    "Clothes, Gadgets",
                    Icons.Outlined.ShoppingBag,
                    Color(0xFF6B7280)
                )
            )
        )
    }
}