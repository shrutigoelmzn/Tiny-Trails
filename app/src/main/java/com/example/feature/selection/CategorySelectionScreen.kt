package com.example.feature.selection

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.designsystem.components.KidsTopBar
import com.example.data.content.CharacterRepository
import com.example.data.content.PrototypeCharacters
import com.example.data.model.CategoryType
import com.example.data.repository.ProgressRepository
import com.example.ui.theme.StarGold

@Composable
fun CategorySelectionScreen(
    categoryType: CategoryType,
    onBack: () -> Unit,
    onSelectCharacter: (String) -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val progressRepo = remember { ProgressRepository.getInstance(context) }
    val progressMap by progressRepo.progressFlow.collectAsState()

    var selectedAlphabetCategory by remember { mutableStateOf(categoryType) }

    val activeCategory = when (categoryType) {
        CategoryType.NUMBERS -> CategoryType.NUMBERS
        CategoryType.SHAPES -> CategoryType.SHAPES
        else -> selectedAlphabetCategory
    }

    val title = when (categoryType) {
        CategoryType.NUMBERS -> stringResource(R.string.select_number)
        CategoryType.SHAPES -> stringResource(R.string.select_shape)
        else -> stringResource(R.string.select_letter)
    }

    val characters = remember(activeCategory) {
        CharacterRepository.getByCategory(activeCategory)
    }

    Scaffold(
        topBar = {
            KidsTopBar(
                title = title,
                onBackClick = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            when (categoryType) {
                CategoryType.NUMBERS -> {
                    Text(
                        text = "Practice Numbers 0–9",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                CategoryType.SHAPES -> {
                    Text(
                        text = "Trace Shapes & Pre-Writing Lines 🎨",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                else -> {
                    // Uppercase / Lowercase selector tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val isUpper = activeCategory == CategoryType.ALPHABET_UPPER
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedAlphabetCategory = CategoryType.ALPHABET_UPPER }
                                .testTag("tab_uppercase"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUpper) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Uppercase A–Z",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUpper) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        val isLower = activeCategory == CategoryType.ALPHABET_LOWER
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedAlphabetCategory = CategoryType.ALPHABET_LOWER }
                                .testTag("tab_lowercase"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isLower) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Lowercase a–z",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLower) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(characters) { item ->
                    val progress = progressMap[item.id]
                    val isDone = progress?.completed == true
                    val stars = progress?.stars ?: 0

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .shadow(6.dp, RoundedCornerShape(24.dp))
                            .clip(RoundedCornerShape(24.dp))
                            .clickable { onSelectCharacter(item.id) }
                            .testTag("selection_card_${item.id}"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Top stars badge
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                repeat(3) { idx ->
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Star",
                                        tint = if (isDone && idx < stars) StarGold else Color.LightGray.copy(alpha = 0.4f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // Big character glyph
                            Text(
                                text = item.displayValue,
                                fontSize = 56.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Associated Word pill
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = item.exampleEmoji, fontSize = 22.sp)
                                Text(
                                    text = item.exampleWord,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
