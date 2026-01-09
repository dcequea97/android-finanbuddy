package com.example.finanbuddy.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.finanbuddy.utils.ext.disabled

@Composable
fun FinanCard(
    modifier: Modifier = Modifier,
    shape: Shape = FinanCardDefaults.shape,
    colors: CardColors = FinanCardDefaults.colors,
    elevation: CardElevation = FinanCardDefaults.elevation,
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
    ) {
        content()
    }
}

object FinanCardDefaults {
    val shape: Shape
        @Composable get() = RoundedCornerShape(20.dp)

    val colors: CardColors
        @Composable get() = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh.disabled(),
        )

    val elevation: CardElevation
        @Composable get() = CardDefaults.cardElevation(2.dp)
}