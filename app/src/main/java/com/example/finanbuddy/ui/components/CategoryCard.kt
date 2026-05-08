package com.example.finanbuddy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.finanbuddy.domain.data.expense.CategoryModel
import com.example.finanbuddy.ui.theme.FinanBuddyTheme
import com.example.finanbuddy.utils.ext.handleTextWithEmoji

enum class CategoryCardType { DETAILED, SIMPLE, SMALL }

@Composable
fun CategoryCard(
    item: CategoryModel,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    cardType: CategoryCardType = CategoryCardType.DETAILED
) {

    // Single place to define per-card-type specs (height, width, padding, horizontalArrangement)
    data class CardSpecs(
        val height: Dp,
        val width: Dp,
        val padding: Dp,
        val horizontalArrangement: Arrangement.Horizontal,
        val horizontalAlignment: Alignment.Horizontal,
        val selectedColor: Color,
        val topPadding: Dp
    )

    val specs = when (cardType) {
        CategoryCardType.DETAILED -> CardSpecs(
            height = 128.dp,
            width = 130.dp,
            padding = 16.dp,
            horizontalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start,
            selectedColor = Color(0xFFBD0329),
            topPadding = 0.dp
        )
        CategoryCardType.SIMPLE -> CardSpecs(
            height = 70.dp,
            width = 80.dp,
            padding = 8.dp,
            horizontalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            selectedColor = Color(0xFF07C51A),
            topPadding = 8.dp
        )
        CategoryCardType.SMALL -> CardSpecs(
            height = 50.dp,
            width = 100.dp,
            padding = 8.dp,
            horizontalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            selectedColor = Color(0xFF07C51A),
            topPadding = 8.dp
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) specs.selectedColor.copy(alpha = 0.05f)
                else MaterialTheme.colorScheme.surfaceContainer
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) specs.selectedColor.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onSelect)
            .padding(specs.padding)
            .width(specs.width)
            .height(specs.height),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = specs.horizontalAlignment
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = specs.horizontalArrangement
            ) {
                if (cardType != CategoryCardType.SMALL) {
                    Box(
                        modifier = Modifier
                            .then(Modifier.padding(top = specs.topPadding))
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (cardType == CategoryCardType.SIMPLE && isSelected)
                                    specs.selectedColor.copy(alpha = 0.35f)
                                else MaterialTheme.colorScheme.surface
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.name,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                if (isSelected && cardType == CategoryCardType.DETAILED) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = "Selected",
                        tint = specs.selectedColor,
                        modifier = Modifier.size(20.dp),

                    )
                }
            }

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center
            ) {
                when (cardType) {
                    CategoryCardType.DETAILED,
                    CategoryCardType.SIMPLE -> BodyMediumBold(text = item.name)
                    CategoryCardType.SMALL -> {
                        BodyMediumBold(
                            text = item.name.handleTextWithEmoji(),
                            maxLines = 2,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                if (cardType == CategoryCardType.DETAILED) {
                    Spacer(modifier = Modifier.height(4.dp))

                    BodySmall(text = item.description)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryCardDetailPreview() {
    FinanBuddyTheme {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            CategoryCard(
                item = CategoryModel(
                    id = "1",
                    name = "Groceries",
                    description = "For food",
                    icon = Icons.Rounded.ShoppingCart,
                    color = Color.Transparent
                ),
                isSelected = false,
                onSelect = {}
            )
            Spacer(modifier = Modifier.width(16.dp))
            CategoryCard(
                item = CategoryModel(
                    id = "1",
                    name = "Groceries",
                    description = "For food",
                    icon = Icons.Rounded.ShoppingCart,
                    color = Color.Transparent
                ),
                isSelected = true,
                onSelect = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryCardSimplePreview() {
    FinanBuddyTheme {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            CategoryCard(
                item = CategoryModel(
                    id = "1",
                    name = "\uD83C\uDF10Internet",
                    description = "\uD83C\uDF10Internet",
                    icon = Icons.Rounded.ShoppingCart,
                    color = Color.Transparent
                ),
                isSelected = false,
                onSelect = {},
                cardType = CategoryCardType.SIMPLE
            )
            Spacer(modifier = Modifier.width(16.dp))
            CategoryCard(
                item = CategoryModel(
                    id = "1",
                    name = "Groce",
                    description = "For food",
                    icon = Icons.Rounded.ShoppingCart,
                    color = Color.Transparent
                ),
                isSelected = true,
                onSelect = {},
                cardType = CategoryCardType.SIMPLE
            )
        }
    }
}
// create preview for smaill
@Preview(showBackground = true)
@Composable
private fun CategoryCardSmallPreview() {
    FinanBuddyTheme {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            CategoryCard(
                item = CategoryModel(
                    id = "1",
                    name = "\uD83D\uDED2Mercado",
                    description = "For food",
                    icon = Icons.Rounded.ShoppingCart,
                    color = Color.Transparent
                ),
                isSelected = false,
                onSelect = {},
                cardType = CategoryCardType.SMALL
            )
            Spacer(modifier = Modifier.width(16.dp))
            CategoryCard(
                item = CategoryModel(
                    id = "1",
                    name = "\uD83D\uDED2Mercado",
                    description = "\uD83D\uDED2Mercado",
                    icon = Icons.Rounded.ShoppingCart,
                    color = Color.Transparent
                ),
                isSelected = false,
                onSelect = {},
                cardType = CategoryCardType.SMALL
            )
             Spacer(modifier = Modifier.width(16.dp))

        }
    }
}