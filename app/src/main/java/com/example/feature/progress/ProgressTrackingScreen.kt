package com.example.feature.progress

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.audio.SoundManager
import com.example.core.designsystem.components.CelebratoryParticleEffect
import com.example.core.designsystem.components.KidsTopBar
import com.example.data.content.CharacterRepository
import com.example.data.model.CategoryType
import com.example.data.model.CharacterContent
import com.example.data.model.CharacterProgress
import com.example.data.repository.ProgressRepository
import com.example.ui.theme.StarGold

enum class ProgressTab {
    ALL,
    NUMBERS,
    UPPERCASE,
    LOWERCASE,
    SHAPES,
    ACHIEVEMENTS
}

enum class FilterStatus {
    ALL,
    COMPLETED_ONLY,
    NEEDS_PRACTICE
}

data class MilestoneBadge(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val targetCount: Int,
    val currentCount: Int,
    val isUnlocked: Boolean
)

/**
 * Visual Progress Tracking Screen displaying stars earned, completed badges,
 * and milestone achievements for successfully traced letters and numbers.
 */
@Composable
fun ProgressTrackingScreen(
    onBack: () -> Unit,
    onSelectCharacter: (characterId: String) -> Unit,
    onNavigateCertificates: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val progressRepo = remember { ProgressRepository.getInstance(context) }
    val soundManager = remember { SoundManager.getInstance(context) }
    val progressMap by progressRepo.progressFlow.collectAsState()

    var selectedTab by remember { mutableStateOf(ProgressTab.ALL) }
    var selectedFilter by remember { mutableStateOf(FilterStatus.ALL) }
    var triggerCelebration by remember { mutableStateOf(false) }

    val allCharacters = remember { CharacterRepository.allCharacters }
    val numbersList = remember { CharacterRepository.numbers }
    val uppercaseList = remember { CharacterRepository.uppercase }
    val lowercaseList = remember { CharacterRepository.lowercase }
    val shapesList = remember { CharacterRepository.shapes }

    // Summary Statistics
    val totalCharactersCount = allCharacters.size
    val totalStarsPossible = totalCharactersCount * 3

    val completedCharactersCount = remember(progressMap) {
        allCharacters.count { char -> progressMap[char.id]?.completed == true }
    }

    val totalStarsEarned = remember(progressMap) {
        allCharacters.sumOf { char -> progressMap[char.id]?.stars ?: 0 }
    }

    val threeStarCount = remember(progressMap) {
        allCharacters.count { char -> (progressMap[char.id]?.stars ?: 0) >= 3 }
    }

    val numbersCompleted = remember(progressMap) {
        numbersList.count { char -> progressMap[char.id]?.completed == true }
    }

    val upperCompleted = remember(progressMap) {
        uppercaseList.count { char -> progressMap[char.id]?.completed == true }
    }

    val lowerCompleted = remember(progressMap) {
        lowercaseList.count { char -> progressMap[char.id]?.completed == true }
    }

    val overallPercentage = remember(completedCharactersCount, totalCharactersCount) {
        if (totalCharactersCount > 0) completedCharactersCount.toFloat() / totalCharactersCount.toFloat() else 0f
    }

    // Milestones definition
    val milestones = remember(progressMap, totalStarsEarned, completedCharactersCount, threeStarCount) {
        listOf(
            MilestoneBadge(
                id = "m_first_step",
                title = "First Step Explorer 🌱",
                description = "Successfully trace your very first character!",
                emoji = "🌟",
                targetCount = 1,
                currentCount = completedCharactersCount.coerceAtMost(1),
                isUnlocked = completedCharactersCount >= 1
            ),
            MilestoneBadge(
                id = "m_number_whiz",
                title = "Number Whiz 🔢",
                description = "Successfully trace 5 numbers (0–9)!",
                emoji = "🔢",
                targetCount = 5,
                currentCount = numbersCompleted.coerceAtMost(5),
                isUnlocked = numbersCompleted >= 5
            ),
            MilestoneBadge(
                id = "m_alphabet_adventurer",
                title = "Alphabet Adventurer 🔤",
                description = "Successfully trace 10 letters!",
                emoji = "🔤",
                targetCount = 10,
                currentCount = (upperCompleted + lowerCompleted).coerceAtMost(10),
                isUnlocked = (upperCompleted + lowerCompleted) >= 10
            ),
            MilestoneBadge(
                id = "m_star_collector",
                title = "Star Collector ✨",
                description = "Earn a total of 15 shining stars!",
                emoji = "⭐",
                targetCount = 15,
                currentCount = totalStarsEarned.coerceAtMost(15),
                isUnlocked = totalStarsEarned >= 15
            ),
            MilestoneBadge(
                id = "m_perfectionist",
                title = "Triple-Star Wizard 🧙‍♂️",
                description = "Achieve a full 3-star score on 5 characters!",
                emoji = "✨",
                targetCount = 5,
                currentCount = threeStarCount.coerceAtMost(5),
                isUnlocked = threeStarCount >= 5
            ),
            MilestoneBadge(
                id = "m_rainbow_scholar",
                title = "Rainbow Scholar 🌈",
                description = "Master at least 1 number, 1 uppercase, and 1 lowercase letter!",
                emoji = "🌈",
                targetCount = 3,
                currentCount = (if (numbersCompleted > 0) 1 else 0) +
                        (if (upperCompleted > 0) 1 else 0) +
                        (if (lowerCompleted > 0) 1 else 0),
                isUnlocked = numbersCompleted >= 1 && upperCompleted >= 1 && lowerCompleted >= 1
            ),
            MilestoneBadge(
                id = "m_preschool_champion",
                title = "Preschool Champion 🏆",
                description = "Complete 25 letters and numbers!",
                emoji = "🏆",
                targetCount = 25,
                currentCount = completedCharactersCount.coerceAtMost(25),
                isUnlocked = completedCharactersCount >= 25
            ),
            MilestoneBadge(
                id = "m_grand_master",
                title = "Grand Master Calligrapher 👑",
                description = "Earn 50 or more total stars!",
                emoji = "👑",
                targetCount = 50,
                currentCount = totalStarsEarned.coerceAtMost(50),
                isUnlocked = totalStarsEarned >= 50
            )
        )
    }

    // Characters list filtered by tab and status
    val currentTabCharacters = remember(selectedTab) {
        when (selectedTab) {
            ProgressTab.ALL -> allCharacters
            ProgressTab.NUMBERS -> numbersList
            ProgressTab.UPPERCASE -> uppercaseList
            ProgressTab.LOWERCASE -> lowercaseList
            ProgressTab.SHAPES -> shapesList
            ProgressTab.ACHIEVEMENTS -> emptyList()
        }
    }

    val displayedCharacters = remember(currentTabCharacters, selectedFilter, progressMap) {
        when (selectedFilter) {
            FilterStatus.ALL -> currentTabCharacters
            FilterStatus.COMPLETED_ONLY -> currentTabCharacters.filter { char ->
                progressMap[char.id]?.completed == true
            }
            FilterStatus.NEEDS_PRACTICE -> currentTabCharacters.filter { char ->
                val prog = progressMap[char.id]
                prog == null || !prog.completed || prog.stars < 3
            }
        }
    }

    Scaffold(
        topBar = {
            KidsTopBar(
                title = stringResource(R.string.progress_title),
                onBackClick = onBack,
                onAudioClick = {
                    soundManager.explainProgress(totalStarsEarned)
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("progress_tracking_screen")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (triggerCelebration) {
                CelebratoryParticleEffect(
                    modifier = Modifier.fillMaxSize(),
                    isTriggered = true,
                    particleCount = 100
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 105.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Header Span: Hero Summary Card
                item(span = { GridItemSpan(maxLineSpan) }) {
                    ProgressSummaryHeroCard(
                        totalStars = totalStarsEarned,
                        totalStarsPossible = totalStarsPossible,
                        completedCount = completedCharactersCount,
                        totalCount = totalCharactersCount,
                        numbersDone = numbersCompleted,
                        numbersTotal = numbersList.size,
                        lettersDone = upperCompleted + lowerCompleted,
                        lettersTotal = uppercaseList.size + lowercaseList.size,
                        percentage = overallPercentage,
                        onHeroClick = {
                            triggerCelebration = !triggerCelebration
                            soundManager.playToneSuccess()
                        }
                    )
                }

                // If onNavigateCertificates available, Certificate Banner button
                if (onNavigateCertificates != null) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(4.dp, RoundedCornerShape(20.dp))
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {
                                    soundManager.playButtonTap()
                                    onNavigateCertificates()
                                }
                                .testTag("btn_view_certificates"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("🎓", fontSize = 28.sp)
                                    Column {
                                        Text(
                                            text = "Milestone Diplomas & Awards",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "Custom printable certificates of achievement!",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                Text("📜 ▶", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                // Category Navigation Tabs Row
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("progress_tab_row")
                        ) {
                            items(ProgressTab.values()) { tab ->
                                val isSelected = tab == selectedTab
                                val tabTitle = when (tab) {
                                    ProgressTab.ALL -> "🌟 All (${allCharacters.size})"
                                    ProgressTab.NUMBERS -> "🔢 Numbers (${numbersList.size})"
                                    ProgressTab.UPPERCASE -> "🔤 A–Z (${uppercaseList.size})"
                                    ProgressTab.LOWERCASE -> "✏️ a–z (${lowercaseList.size})"
                                    ProgressTab.SHAPES -> "🔺 Shapes (${shapesList.size})"
                                    ProgressTab.ACHIEVEMENTS -> "🏆 Badges (${milestones.count { it.isUnlocked }}/${milestones.size})"
                                }

                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                                        }
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
                                    modifier = Modifier
                                        .clickable {
                                            selectedTab = tab
                                            soundManager.playTonePop()
                                        }
                                        .testTag("progress_tab_${tab.name.lowercase()}")
                                ) {
                                    Text(
                                        text = tabTitle,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.onPrimary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                    )
                                }
                            }
                        }

                        // Filter Chips for Character views
                        if (selectedTab != ProgressTab.ACHIEVEMENTS) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FilterChip(
                                    title = "All",
                                    selected = selectedFilter == FilterStatus.ALL,
                                    onClick = {
                                        selectedFilter = FilterStatus.ALL
                                        soundManager.playTonePop()
                                    }
                                )
                                FilterChip(
                                    title = "Completed ⭐",
                                    selected = selectedFilter == FilterStatus.COMPLETED_ONLY,
                                    onClick = {
                                        selectedFilter = FilterStatus.COMPLETED_ONLY
                                        soundManager.playTonePop()
                                    }
                                )
                                FilterChip(
                                    title = "Needs Practice ✏️",
                                    selected = selectedFilter == FilterStatus.NEEDS_PRACTICE,
                                    onClick = {
                                        selectedFilter = FilterStatus.NEEDS_PRACTICE
                                        soundManager.playTonePop()
                                    }
                                )
                            }
                        }
                    }
                }

                // Branch 1: Achievement Badges Tab
                if (selectedTab == ProgressTab.ACHIEVEMENTS) {
                    items(
                        items = milestones,
                        key = { it.id },
                        span = { GridItemSpan(maxLineSpan) }
                    ) { milestone ->
                        MilestoneBadgeCard(
                            badge = milestone,
                            onClick = {
                                if (milestone.isUnlocked) {
                                    triggerCelebration = true
                                }
                                soundManager.explainMilestoneBadge(milestone.title, milestone.isUnlocked)
                            }
                        )
                    }
                } else {
                    // Branch 2: Character Stars & Badges Grid
                    if (displayedCharacters.isEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "🎈", fontSize = 48.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No characters in this filter yet!",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = "Trace characters to earn shiny stars!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(
                            items = displayedCharacters,
                            key = { it.id }
                        ) { character ->
                            val progress = progressMap[character.id] ?: CharacterProgress(characterId = character.id)
                            CharacterProgressBadgeCard(
                                character = character,
                                progress = progress,
                                onCardClick = {
                                    val stars = progress.stars
                                    if (progress.completed) {
                                        soundManager.playToneSparkle()
                                        soundManager.speak(
                                            "Yay! You traced ${character.displayValue} and got $stars shiny stars! Super job!"
                                        )
                                    } else {
                                        soundManager.explainCharacter(character)
                                    }
                                },
                                onTraceClick = {
                                    onSelectCharacter(character.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Filter Chip Composable for toggling completion filters.
 */
@Composable
private fun FilterChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (selected) {
            androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary)
        } else null,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (selected) {
                MaterialTheme.colorScheme.onSecondaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

/**
 * Hero Banner Card displaying aggregate star count, mastery level, and progress bar.
 */
@Composable
private fun ProgressSummaryHeroCard(
    totalStars: Int,
    totalStarsPossible: Int,
    completedCount: Int,
    totalCount: Int,
    numbersDone: Int,
    numbersTotal: Int,
    lettersDone: Int,
    lettersTotal: Int,
    percentage: Float,
    onHeroClick: () -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = percentage.coerceIn(0f, 1f),
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "hero_progress"
    )

    val masteryTitle = when {
        completedCount >= 50 -> "👑 Grand Master"
        completedCount >= 25 -> "🏆 Tracing Champion"
        completedCount >= 10 -> "🚀 Star Explorer"
        completedCount >= 1 -> "🌱 Little Sprout"
        else -> "✨ Ready to Learn"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(26.dp))
            .clickable(onClick = onHeroClick)
            .testTag("progress_hero_summary_card"),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = masteryTitle,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$totalStars Stars Earned!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "$completedCount of $totalCount characters completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                }

                // Big Golden Trophy / Star Badge
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF9C4)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (totalStars >= 30) "🏆" else "⭐",
                        fontSize = 38.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.White.copy(alpha = 0.5f),
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Mini stats breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatBadgePill(
                    icon = "🔢",
                    label = "Numbers",
                    value = "$numbersDone / $numbersTotal"
                )
                StatBadgePill(
                    icon = "🔤",
                    label = "Letters",
                    value = "$lettersDone / $lettersTotal"
                )
                StatBadgePill(
                    icon = "🌟",
                    label = "3-Star",
                    value = "${(percentage * 100).toInt()}%"
                )
            }
        }
    }
}

/**
 * Small stat pill for breakdown display inside hero card.
 */
@Composable
private fun StatBadgePill(
    icon: String,
    label: String,
    value: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = icon, fontSize = 14.sp)
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

/**
 * Individual Character Badge Card displaying character letter/number,
 * earned stars, and a direct trace action button.
 */
@Composable
private fun CharacterProgressBadgeCard(
    character: CharacterContent,
    progress: CharacterProgress,
    onCardClick: () -> Unit,
    onTraceClick: () -> Unit
) {
    val isCompleted = progress.completed
    val stars = progress.stars

    val containerColor = when {
        stars >= 3 -> Color(0xFFFFFDE7) // Soft gold tint for mastered
        isCompleted -> MaterialTheme.colorScheme.surface
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    }

    val borderColor = when {
        stars >= 3 -> StarGold.copy(alpha = 0.8f)
        isCompleted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        else -> Color.Transparent
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (isCompleted) 4.dp else 1.dp, RoundedCornerShape(20.dp))
            .border(width = if (isCompleted) 1.5.dp else 0.dp, color = borderColor, shape = RoundedCornerShape(20.dp))
            .clickable(onClick = onCardClick)
            .testTag("character_badge_${character.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Character display with badge label
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        } else {
                            Color.White.copy(alpha = 0.8f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = character.displayValue,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Gray
                    }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Example Word / Emoji
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(text = character.exampleEmoji, fontSize = 12.sp)
                Text(
                    text = character.exampleWord ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Earned Stars Row (3 Stars)
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val isEarned = stars > index
                    Icon(
                        imageVector = if (isEarned) Icons.Default.Star else Icons.Outlined.Star,
                        contentDescription = "Star ${index + 1}",
                        tint = if (isEarned) StarGold else Color.LightGray.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action / Trace button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isCompleted) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        }
                    )
                    .clickable(onClick = onTraceClick)
                    .testTag("badge_trace_button_${character.id}"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isCompleted) "Replay ✏️" else "Trace ✏️",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Milestone Achievement Badge Card for Special Rewards & Achievements.
 */
@Composable
private fun MilestoneBadgeCard(
    badge: MilestoneBadge,
    onClick: () -> Unit
) {
    val isUnlocked = badge.isUnlocked
    val progressFraction = (badge.currentCount.toFloat() / badge.targetCount.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (isUnlocked) 6.dp else 1.dp, RoundedCornerShape(22.dp))
            .border(
                width = if (isUnlocked) 2.dp else 0.dp,
                color = if (isUnlocked) StarGold else Color.Transparent,
                shape = RoundedCornerShape(22.dp)
            )
            .clickable(onClick = onClick)
            .testTag("milestone_badge_${badge.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) {
                Color(0xFFFFFDE7)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Big Medal Emblem
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .shadow(if (isUnlocked) 4.dp else 0.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) {
                            Color(0xFFFFECB3)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Text(text = badge.emoji, fontSize = 32.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked badge",
                        tint = Color.Gray,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Title, Description, and Progress
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = badge.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )

                    if (isUnlocked) {
                        Text(
                            text = "UNLOCKED ✨",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = badge.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (isUnlocked) StarGold else MaterialTheme.colorScheme.primary,
                        trackColor = Color.LightGray.copy(alpha = 0.35f),
                        strokeCap = StrokeCap.Round
                    )

                    Text(
                        text = "${badge.currentCount}/${badge.targetCount}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
