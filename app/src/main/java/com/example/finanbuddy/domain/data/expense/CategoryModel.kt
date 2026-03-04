package com.example.finanbuddy.domain.data.expense

import androidx.compose.ui.graphics.Color

data class CategoryModel(
    val id: String,
    val name: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)
