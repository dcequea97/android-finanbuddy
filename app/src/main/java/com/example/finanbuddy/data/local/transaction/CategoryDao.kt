package com.example.finanbuddy.data.local.transaction

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("SELECT * FROM categories WHERE type = :type")
    fun getCategoriesByTypeFlow(type: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories")
    fun getAllCategoriesFlow(): Flow<List<CategoryEntity>>

    /** Deletes rows of a type whose id is NOT in the provided list — avoids full clearAll flicker */
    @Query("DELETE FROM categories WHERE type = :type AND id NOT IN (:ids)")
    suspend fun deleteNotInByType(type: String, ids: List<String>)

    @Query("DELETE FROM categories WHERE type = :type")
    suspend fun clearByType(type: String)

    @Query("DELETE FROM categories")
    suspend fun clearAll()
}

