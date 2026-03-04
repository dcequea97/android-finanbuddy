package com.example.finanbuddy.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// ============= Vertical Spacing =============

@Composable
fun SpacerXSmall(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.then(Modifier.height(4.dp)))
}

@Composable
fun SpacerSmall(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.then(Modifier.height(8.dp)))
}

@Composable
fun SpacerMedium(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.then(Modifier.height(12.dp)))
}

@Composable
fun SpacerLarge(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.then(Modifier.height(16.dp)))
}

@Composable
fun SpacerXLarge(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.then(Modifier.height(24.dp)))
}

// ============= Horizontal Spacing =============

@Composable
fun SpacerHorizontalXSmall(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.then(Modifier.width(4.dp)))
}

@Composable
fun SpacerHorizontalSmall(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.then(Modifier.width(8.dp)))
}

@Composable
fun SpacerHorizontalMedium(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.then(Modifier.width(12.dp)))
}

@Composable
fun SpacerHorizontalLarge(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.then(Modifier.width(16.dp)))
}

@Composable
fun SpacerHorizontalXLarge(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.then(Modifier.width(24.dp)))
}