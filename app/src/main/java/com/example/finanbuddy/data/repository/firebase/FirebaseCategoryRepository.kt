package com.example.finanbuddy.data.repository.firebase

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.ui.graphics.Color
import com.example.finanbuddy.domain.data.Resource
import com.example.finanbuddy.domain.data.expense.CategoryModel
import com.example.finanbuddy.domain.repository.CategoriesRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseCategoryRepository(
    private val firestore: FirebaseFirestore
) : CategoriesRepository {

    override suspend fun getIncomesCategories(): Resource<List<CategoryModel>> {
        return try {
            val categories = categoriesCollection
                .whereEqualTo(FIELD_TYPE, CATEGORY_TYPE_INCOME)
                .get()
                .await()
                .documents
                .mapNotNull { document ->
                    toDomainCategoryOrNull(document.data as? Map<String, Any>?)
                }
            
            if (categories.isEmpty()) {
                initializeDefaultCategories()
            }
            
            Resource.Success(categories)
        } catch (e: Exception) {
            Resource.Error(message = "Failed to fetch income categories: ${e.message}")
        }
    }

    override suspend fun getExpensesCategories(): Resource<List<CategoryModel>> {
        return try {
            val categories = categoriesCollection
                .whereEqualTo(FIELD_TYPE, CATEGORY_TYPE_EXPENSE)
                .get()
                .await()
                .documents
                .mapNotNull { document ->
                    toDomainCategoryOrNull(document.data as? Map<String, Any>?)
                }
            
            if (categories.isEmpty()) {
                initializeDefaultCategories()
            }
            
            Resource.Success(categories)
        } catch (e: Exception) {
            Resource.Error(message = "Failed to fetch expense categories: ${e.message}")
        }
    }

    override suspend fun initializeDefaultCategories(): Resource<Unit> {
        return try {
            val existingCategories = categoriesCollection.get().await()
            
            if (existingCategories.size() > 0) {
                return Resource.Success(Unit)
            }
            
            val defaultCategories = getDefaultCategories()
            
            firestore.runBatch { batch ->
                defaultCategories.forEach { categoryDto ->
                    val docRef = categoriesCollection.document()
                    batch.set(docRef, categoryDto)
                }
            }.await()
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(message = "Failed to initialize categories: ${e.message}")
        }
    }

    private fun getDefaultCategories(): List<Map<String, Any>> {
        val colorMap = mapOf(
            0xFFEF4444 to "0xFFEF4444",
            0xFF6B7280 to "0xFF6B7280",
            0xFF10B981 to "0xFF10B981",
            0xFFF59E0B to "0xFFF59E0B",
            0xFF8B5CF6 to "0xFF8B5CF6"
        )
        
        return listOf(
            // Expense Categories
            mapOf(
                FIELD_ID to "food",
                FIELD_NAME to "Food",
                FIELD_DESCRIPTION to "Groceries, Dining",
                FIELD_TYPE to CATEGORY_TYPE_EXPENSE,
                FIELD_COLOR to colorMap[0xFFEF4444]!!
            ),
            mapOf(
                FIELD_ID to "transport",
                FIELD_NAME to "Transport",
                FIELD_DESCRIPTION to "Uber, Gas, Train",
                FIELD_TYPE to CATEGORY_TYPE_EXPENSE,
                FIELD_COLOR to colorMap[0xFF6B7280]!!
            ),
            mapOf(
                FIELD_ID to "rent",
                FIELD_NAME to "Rent",
                FIELD_DESCRIPTION to "Housing, Bills",
                FIELD_TYPE to CATEGORY_TYPE_EXPENSE,
                FIELD_COLOR to colorMap[0xFF6B7280]!!
            ),
            mapOf(
                FIELD_ID to "shopping",
                FIELD_NAME to "Shopping",
                FIELD_DESCRIPTION to "Clothes, Gadgets",
                FIELD_TYPE to CATEGORY_TYPE_EXPENSE,
                FIELD_COLOR to colorMap[0xFFF59E0B]!!
            ),
            mapOf(
                FIELD_ID to "entertainment",
                FIELD_NAME to "Entertainment",
                FIELD_DESCRIPTION to "Movies, Games, Events",
                FIELD_TYPE to CATEGORY_TYPE_EXPENSE,
                FIELD_COLOR to colorMap[0xFF8B5CF6]!!
            ),
            // Income Categories
            mapOf(
                FIELD_ID to "salary",
                FIELD_NAME to "Salary",
                FIELD_DESCRIPTION to "Monthly salary",
                FIELD_TYPE to CATEGORY_TYPE_INCOME,
                FIELD_COLOR to colorMap[0xFF10B981]!!
            ),
            mapOf(
                FIELD_ID to "freelance",
                FIELD_NAME to "Freelance",
                FIELD_DESCRIPTION to "Freelance work",
                FIELD_TYPE to CATEGORY_TYPE_INCOME,
                FIELD_COLOR to colorMap[0xFF10B981]!!
            ),
            mapOf(
                FIELD_ID to "bonus",
                FIELD_NAME to "Bonus",
                FIELD_DESCRIPTION to "Bonuses, Tips",
                FIELD_TYPE to CATEGORY_TYPE_INCOME,
                FIELD_COLOR to colorMap[0xFFF59E0B]!!
            ),
            mapOf(
                FIELD_ID to "investment",
                FIELD_NAME to "Investment",
                FIELD_DESCRIPTION to "Investment returns",
                FIELD_TYPE to CATEGORY_TYPE_INCOME,
                FIELD_COLOR to colorMap[0xFF10B981]!!
            )
        )
    }

    private fun toDomainCategoryOrNull(data: Map<String, Any>?): CategoryModel? {
        if (data == null) return null
        
        return try {
            val id = data[FIELD_ID] as? String ?: return null
            val name = data[FIELD_NAME] as? String ?: return null
            val description = data[FIELD_DESCRIPTION] as? String ?: return null
            val colorString = data[FIELD_COLOR] as? String ?: return null
            val type = data[FIELD_TYPE] as? String ?: return null
            
            val color = when (colorString) {
                "0xFFEF4444" -> Color(0xFFEF4444)
                "0xFF6B7280" -> Color(0xFF6B7280)
                "0xFF10B981" -> Color(0xFF10B981)
                "0xFFF59E0B" -> Color(0xFFF59E0B)
                "0xFF8B5CF6" -> Color(0xFF8B5CF6)
                else -> Color(0xFF6B7280)
            }
            
            val icon = when (id) {
                "food" -> Icons.Outlined.Restaurant
                "transport" -> Icons.Outlined.DirectionsBus
                "rent" -> Icons.Outlined.Home
                "shopping" -> Icons.Outlined.ShoppingBag
                "entertainment" -> Icons.Outlined.AttachMoney
                "salary" -> Icons.Outlined.Savings
                "freelance" -> Icons.Outlined.AttachMoney
                "bonus" -> Icons.Outlined.AttachMoney
                "investment" -> Icons.Outlined.Savings
                else -> Icons.Outlined.AttachMoney
            }
            
            CategoryModel(
                id = id,
                name = name,
                description = description,
                icon = icon,
                color = color
            )
        } catch (e: Exception) {
            null
        }
    }

    private companion object {
        const val CATEGORIES_COLLECTION = "categories"
        const val FIELD_ID = "id"
        const val FIELD_NAME = "name"
        const val FIELD_DESCRIPTION = "description"
        const val FIELD_TYPE = "type"
        const val FIELD_COLOR = "color"
        const val CATEGORY_TYPE_INCOME = "income"
        const val CATEGORY_TYPE_EXPENSE = "expense"
    }

    private val categoriesCollection = firestore.collection(CATEGORIES_COLLECTION)
}

