package com.example.finanbuddy.data.remote.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.ui.graphics.Color
import com.example.finanbuddy.domain.data.expense.CategoryModel
import kotlinx.serialization.Serializable

typealias CategoriesResponse = List<CategoriesResponseItem>

@Serializable
data class CategoriesResponseItem(
    val name: String,
    val type: String
)

fun CategoriesResponseItem.toDomain(): CategoryModel {
    return CategoryModel(
        id = name,
        name = name,
        description = name,
        icon =  Icons.Rounded.ShoppingCart,
        color = Color.Transparent
    )
}