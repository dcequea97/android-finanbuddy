package com.example.finanbuddy.data.local.transaction

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.ui.graphics.Color
import com.example.finanbuddy.domain.data.expense.CategoryModel

fun CategoryEntity.toDomain(): CategoryModel {
    return CategoryModel(
        id = id,
        name = name,
        description = description,
        icon = getIconById(id),
        color = Color(parseColorHex(colorHex))
    )
}

fun CategoryModel.toEntity(type: String = "EXPENSE"): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        description = description,
        type = type,
        colorHex = String.format("0x%08X", color.value.toLong()),
        iconName = id
    )
}

private fun parseColorHex(hex: String): Long {
    return hex.removePrefix("0x").toLong(16)
}

private fun getIconById(id: String) = when (id) {
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


