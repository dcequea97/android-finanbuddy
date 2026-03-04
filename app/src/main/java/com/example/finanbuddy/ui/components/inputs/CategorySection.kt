package com.example.finanbuddy.ui.components.inputs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.finanbuddy.domain.data.expense.CategoryModel
import com.example.finanbuddy.ui.components.CategoryCard
import com.example.finanbuddy.ui.components.CategoryCardType
import com.example.finanbuddy.ui.components.LabelMedium
import com.example.finanbuddy.ui.components.LabelSmallBold


@Composable
fun CategorySection(
    categories: List<CategoryModel>,
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit,
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier,
    cardType: CategoryCardType = CategoryCardType.DETAILED
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LabelMedium(text = "Category")

            LabelSmallBold(
                text = "See all",
                modifier = Modifier.clickable(onClick = onSeeAll)
            )
        }

        val lazyItemSpacer = 12.dp
//        val itemHeight = 128.dp
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
//                .height(itemHeight)
            ,
            horizontalArrangement = Arrangement.spacedBy(lazyItemSpacer),
            contentPadding = PaddingValues(horizontal = lazyItemSpacer)
        ) {
            items(categories) { item ->
                CategoryCard(
                    item = item,
                    isSelected = selectedCategory == item.id,
                    onSelect = { onCategorySelected(item.id) },
                    modifier = Modifier
//                        .height(itemHeight)
                    ,
                    cardType = cardType
                )
            }
        }
    }
}