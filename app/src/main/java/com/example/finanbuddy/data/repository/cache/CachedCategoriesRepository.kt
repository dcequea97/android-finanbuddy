package com.example.finanbuddy.data.repository.cache

import com.example.finanbuddy.data.local.transaction.CategoryDao
import com.example.finanbuddy.data.local.transaction.toDomain
import com.example.finanbuddy.data.local.transaction.toEntity
import com.example.finanbuddy.domain.data.Resource
import com.example.finanbuddy.domain.data.expense.CategoryModel
import com.example.finanbuddy.domain.repository.CategoriesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Cache-first repository:
 * - Immediately returns categories from Room via Flow
 * - Fetches from remote in background and updates Room
 */
class CachedCategoriesRepository(
    private val remoteRepository: CategoriesRepository,
    private val categoryDao: CategoryDao,
    private val externalScope: CoroutineScope
) : CategoriesRepository {

    override suspend fun getIncomesCategories(): Resource<List<CategoryModel>> = Resource.Success(emptyList())

    override fun getIncomesCategoriesFlow(): Flow<List<CategoryModel>> {
        externalScope.launch {
            when (val result = remoteRepository.getIncomesCategories()) {
                is Resource.Success -> {
                    val entities = result.data.map { it.toEntity("INCOME") }
                    categoryDao.insertCategories(entities)
                    if (entities.isNotEmpty()) {
                        categoryDao.deleteNotInByType("INCOME", entities.map { it.id })
                    }
                }
                else -> {}
            }
        }
        return categoryDao.getCategoriesByTypeFlow("INCOME")
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getExpensesCategories(): Resource<List<CategoryModel>> = Resource.Success(emptyList())

    override fun getExpensesCategoriesFlow(): Flow<List<CategoryModel>> {
        externalScope.launch {
            when (val result = remoteRepository.getExpensesCategories()) {
                is Resource.Success -> {
                    val entities = result.data.map { it.toEntity("EXPENSE") }
                    categoryDao.insertCategories(entities)
                    if (entities.isNotEmpty()) {
                        categoryDao.deleteNotInByType("EXPENSE", entities.map { it.id })
                    }
                }
                else -> {}
            }
        }
        return categoryDao.getCategoriesByTypeFlow("EXPENSE")
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun initializeDefaultCategories(): Resource<Unit> =
        remoteRepository.initializeDefaultCategories()
}
