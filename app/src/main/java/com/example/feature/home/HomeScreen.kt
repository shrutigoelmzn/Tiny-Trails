package com.example.feature.home

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.core.audio.SoundManager
import com.example.core.designsystem.components.KidsButton
import com.example.data.content.PrototypeCharacters
import com.example.data.model.CategoryType
import com.example.data.model.ThemeMode
import com.example.data.repository.ProgressRepository
import com.example.ui.theme.LocalCustomThemeTokens

import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import com.example.data.content.CharacterRepository
import com.example.data.journey.DailyJourneyManager
import com.example.feature.parent.ParentGateDialog

@Composable
fun HomeScreen(
    currentTheme: ThemeMode,
    currentLanguage: String = "en",
    audioFeedbackEnabled: Boolean = true,
    backgroundMusicEnabled: Boolean = true,
    appVolume: Float = 0.80f,
    onThemeChange: (ThemeMode) -> Unit,
    onLanguageChange: (String) -> Unit = {},
    onAudioFeedbackChange: (Boolean) -> Unit = {},
    onBackgroundMusicChange: (Boolean) -> Unit = {},
    onAppVolumeChange: (Float) -> Unit = {},
    onNavigateCategory: (CategoryType) -> Unit,
    onStartTodayLesson: (String) -> Unit,
    onNavigateGarden: () -> Unit,
    onNavigateStickers: () -> Unit,
    onNavigateParentArea: () -> Unit = {},
    onNavigateProgress: () -> Unit = {},
    onNavigateColoring: () -> Unit = {},
    onNavigatePet: () -> Unit = {},
    onNavigateCertificates: () -> Unit = {}
) {
    val context = LocalContext.current
    val progressRepo = remember { ProgressRepository.getInstance(context) }
    val petRepo = remember { com.example.data.repository.PetRepository.getInstance(context) }
    val soundManager = SoundManager.getInstance(context)
    val progressMap by progressRepo.progressFlow.collectAsState()
    val petState by petRepo.petStateFlow.collectAsState()
    val themeTokens = LocalCustomThemeTokens.current

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showParentGate by remember { mutableStateOf(false) }

    // Adaptive Daily Journey
    val journeySteps = remember(progressMap) {
        DailyJourneyManager.generateDailySession(progressMap)
    }

    val activeStep = remember(journeySteps) {
        journeySteps.firstOrNull { !it.isCompleted } ?: journeySteps.first()
    }

    if (showParentGate) {
        ParentGateDialog(
            onDismiss = { showParentGate = false },
            onSuccess = {
                showParentGate = false
                onNavigateParentArea()
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header with App Title & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.clickable {
                        soundManager.playBabyChirp()
                        soundManager.speak("Hello little explorer! Let's trace and learn together! What would you like to draw today?")
                    }
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.hello_learner),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }

                // Header Action Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Star Progress Button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(3.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF9C4))
                            .clickable(onClick = onNavigateProgress)
                            .testTag("home_header_progress_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⭐", fontSize = 22.sp)
                    }

                    // Certificate Award Button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(3.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color(0xFFE8EAF6))
                            .clickable(onClick = onNavigateCertificates)
                            .testTag("home_header_certificates_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🎓", fontSize = 22.sp)
                    }

                    // Parent Zone Button (Shield)
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(3.dp, CircleShape)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { showParentGate = true }
                            .testTag("home_parent_zone_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🛡️", fontSize = 22.sp)
                    }

                    // Theme Emoji Button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(3.dp, CircleShape)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { showSettingsDialog = true }
                            .testTag("home_theme_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = themeTokens.themeEmoji,
                            fontSize = 22.sp
                        )
                    }

                    // Settings Button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(3.dp, CircleShape)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { showSettingsDialog = true }
                            .testTag("home_settings_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings_title),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Prominent "Today's Learning" Hero Card with Journey Steps
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(28.dp))
                    .clip(RoundedCornerShape(28.dp))
                    .clickable { onStartTodayLesson(activeStep.character.id) }
                    .testTag("home_todays_learning_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.todays_learning),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Next: ${activeStep.title}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                            )
                        }

                        Text(
                            text = activeStep.character.exampleEmoji,
                            fontSize = 54.sp,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Daily Journey Step Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        journeySteps.forEachIndexed { idx, step ->
                            val isCurrent = step.id == activeStep.id
                            val isDone = step.isCompleted
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        when {
                                            isDone -> Color(0xFF4CAF50)
                                            isCurrent -> MaterialTheme.colorScheme.primary
                                            else -> Color.White.copy(alpha = 0.4f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isDone) "✓" else "${idx + 1}",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    KidsButton(
                        text = stringResource(R.string.continue_journey),
                        onClick = { onStartTodayLesson(activeStep.character.id) },
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "home_continue_button"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.learn_practice_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2 Primary Category Cards: Numbers 0–9 & Alphabet A–Z
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Numbers
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(160.dp)
                        .shadow(6.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { onNavigateCategory(CategoryType.NUMBERS) }
                        .testTag("home_category_numbers"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "🔢", fontSize = 38.sp)
                        Column {
                            Text(
                                text = stringResource(R.string.numbers_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Numbers 0–9",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Alphabet
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(160.dp)
                        .shadow(6.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { onNavigateCategory(CategoryType.ALPHABET_UPPER) }
                        .testTag("home_category_alphabet"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "🔤", fontSize = 38.sp)
                        Column {
                            Text(
                                text = stringResource(R.string.alphabet_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "A–Z & a–z",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Shapes & Pre-Writing Lines Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .shadow(5.dp, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .clickable { onNavigateCategory(CategoryType.SHAPES) }
                    .testTag("home_category_shapes"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(text = "🔺", fontSize = 38.sp)
                        Column {
                            Text(
                                text = stringResource(R.string.shapes_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Circles, Squares, Zigzags & Waves",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Text("◯ ⚡ 🌊 ▶", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.explore_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Row 1 of Explore: Learning Garden & Sticker Gallery
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Garden
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp)
                        .shadow(4.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(onClick = onNavigateGarden)
                        .testTag("home_nav_garden"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "🌱", fontSize = 36.sp)
                        Column {
                            Text(
                                text = stringResource(R.string.garden_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(R.string.garden_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Stickers
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp)
                        .shadow(4.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(onClick = onNavigateStickers)
                        .testTag("home_nav_stickers"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "⭐", fontSize = 36.sp)
                        Column {
                            Text(
                                text = stringResource(R.string.stickers_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(R.string.stickers_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Row 2 of Explore: Coloring Studio & Pet Pip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Coloring Studio
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp)
                        .shadow(4.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(onClick = onNavigateColoring)
                        .testTag("home_nav_coloring"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "🎨", fontSize = 36.sp)
                        Column {
                            Text(
                                text = stringResource(R.string.coloring_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(R.string.coloring_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Pet Pip
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp)
                        .shadow(4.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(onClick = onNavigatePet)
                        .testTag("home_nav_pet"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "🦕", fontSize = 36.sp)
                        Column {
                            Text(
                                text = "Pet Pip",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Level ${petState.level} • Play",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Featured Visual Progress Tracking Card
            val earnedStarsTotal = remember(progressMap) {
                progressMap.values.sumOf { it.stars }
            }
            val completedCount = remember(progressMap) {
                progressMap.values.count { it.completed }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .clickable(onClick = onNavigateProgress)
                    .testTag("home_nav_progress"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF9C4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🌟", fontSize = 32.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.progress_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "$earnedStarsTotal Stars Earned • $completedCount Traced",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
                        )
                    }

                    Text(
                        text = "▶",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Milestone Achievement Certificates Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(5.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .clickable(onClick = onNavigateCertificates)
                    .testTag("home_nav_certificates"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color(0xFFEDE7F6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🎓", fontSize = 32.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.certificates_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "Print & Share Achievement Diplomas!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f)
                        )
                    }

                    Text(
                        text = "📜 ▶",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Settings Dialog
        if (showSettingsDialog) {
            AlertDialog(
                onDismissRequest = { showSettingsDialog = false },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings_title),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = stringResource(R.string.settings_title),
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Section 1: Audio Controls
                        Text(
                            text = stringResource(R.string.parent_controls_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Audio Feedback Toggle
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = stringResource(R.string.audio_feedback_title),
                                        tint = if (audioFeedbackEnabled) MaterialTheme.colorScheme.primary else Color.Gray,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Column {
                                        Text(
                                            text = stringResource(R.string.audio_feedback_title),
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = stringResource(R.string.audio_feedback_desc),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                Switch(
                                    checked = audioFeedbackEnabled,
                                    onCheckedChange = onAudioFeedbackChange,
                                    modifier = Modifier.testTag("toggle_audio_feedback")
                                )
                            }
                        }

                        // Background Music Toggle
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = stringResource(R.string.background_music_title),
                                        tint = if (backgroundMusicEnabled) MaterialTheme.colorScheme.primary else Color.Gray,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Column {
                                        Text(
                                            text = stringResource(R.string.background_music_title),
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = stringResource(R.string.background_music_desc),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                Switch(
                                    checked = backgroundMusicEnabled,
                                    onCheckedChange = onBackgroundMusicChange,
                                    modifier = Modifier.testTag("toggle_background_music")
                                )
                            }
                        }

                        // App Volume Slider
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.GraphicEq,
                                            contentDescription = stringResource(R.string.app_volume_title),
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Text(
                                            text = stringResource(R.string.app_volume_title),
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = "${(appVolume * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Slider(
                                    value = appVolume,
                                    onValueChange = onAppVolumeChange,
                                    valueRange = 0f..1f,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("slider_app_volume")
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        // Section 2: Theme Selector
                        Text(
                            text = stringResource(R.string.choose_theme),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            ThemeOptionCard(
                                theme = ThemeMode.HAPPY_GARDEN,
                                isSelected = currentTheme == ThemeMode.HAPPY_GARDEN,
                                onClick = { onThemeChange(ThemeMode.HAPPY_GARDEN) }
                            )
                            ThemeOptionCard(
                                theme = ThemeMode.DINO_WORLD,
                                isSelected = currentTheme == ThemeMode.DINO_WORLD,
                                onClick = { onThemeChange(ThemeMode.DINO_WORLD) }
                            )
                            ThemeOptionCard(
                                theme = ThemeMode.SPACE_ADVENTURE,
                                isSelected = currentTheme == ThemeMode.SPACE_ADVENTURE,
                                onClick = { onThemeChange(ThemeMode.SPACE_ADVENTURE) }
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        // Section 3: Language Selector
                        Text(
                            text = stringResource(R.string.language_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val isEn = currentLanguage != "hi"
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onLanguageChange("en") }
                                    .testTag("lang_en_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isEn) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = stringResource(R.string.lang_english),
                                        fontWeight = FontWeight.Bold,
                                        color = if (isEn) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            val isHi = currentLanguage == "hi"
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onLanguageChange("hi") }
                                    .testTag("lang_hi_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isHi) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = stringResource(R.string.lang_hindi),
                                        fontWeight = FontWeight.Bold,
                                        color = if (isHi) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        // Section 4: Parent Area Shortcut
                        Button(
                            onClick = {
                                showSettingsDialog = false
                                showParentGate = true
                            },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("settings_parent_area_button")
                        ) {
                            Text(text = "Parent Area & Progress 🛡️", fontWeight = FontWeight.Bold)
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { showSettingsDialog = false },
                        modifier = Modifier.testTag("settings_done_button")
                    ) {
                        Text(
                            text = "Done",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun ThemeOptionCard(
    theme: ThemeMode,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = theme.emoji, fontSize = 28.sp)
            Text(
                text = theme.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
