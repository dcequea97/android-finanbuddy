package com.example.finanbuddy.ui.components

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LoadingIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppLoader(
    modifier: Modifier = Modifier
) {
    LoadingIndicator(
        modifier = modifier,
        polygons = LoadingIndicatorDefaults.IndeterminateIndicatorPolygons.take(
            2
        )
    )
}