package com.example.feature.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.audio.SoundManager
import com.example.core.designsystem.components.KidsTopBar
import com.example.data.model.ThemeMode

/**
 * Dedicated, uncrowded Settings screen displaying all options:
 * - Prominent, beautiful Theme Selection at the top with live preview colors
 * - Language switcher (English / Hindi)
 * - Background music and SFX toggles
 * - Volume slider
 * - Quick shortcuts to Parent Zone, Progress Tracker, and Milestone Certificates
 */
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    currentTheme: ThemeMode,
    currentLanguage: String,
    audioFeedbackEnabled: Boolean,
    backgroundMusicEnabled: Boolean,
    appVolume: Float,
    onThemeChange: (ThemeMode) -> Unit,
    onLanguageChange: (String) -> Unit,
    onAudioFeedbackChange: (Boolean) -> Unit,
    onBackgroundMusicChange: (Boolean) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onNavigateParent: () -> Unit,
    onNavigateProgress: () -> Unit,
    onNavigateCertificates: () -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val soundManager = remember { SoundManager.getInstance(context) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            KidsTopBar(
                title = stringResource(R.string.settings_title),
                onBackClick = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ==========================================
            // 1. PROMINENT THEME SELECTION SECTION
            // ==========================================
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(24.dp))
                    .testTag("settings_theme_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("🎨", fontSize = 28.sp)
                        Column {
                            Text(
                                text = stringResource(R.string.choose_theme),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Personalize colors, background, and character vibes",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // 3 Distinct Theme Cards
                    ThemeSelectCard(
                        emoji = "🌸",
                        title = "Happy Garden",
                        subtitle = "Gentle floral greens, daisy yellow & soft pastel pink",
                        palette = listOf(Color(0xFF81C784), Color(0xFFF48FB1), Color(0xFFFFF59D)),
                        isSelected = currentTheme == ThemeMode.HAPPY_GARDEN,
                        onClick = {
                            soundManager.playCorrectChime()
                            onThemeChange(ThemeMode.HAPPY_GARDEN)
                        },
                        tag = "settings_theme_happy_garden"
                    )

                    ThemeSelectCard(
                        emoji = "🦕",
                        title = "Dino World",
                        subtitle = "Vibrant emerald jungle, cheerful baby dinos & earth tones",
                        palette = listOf(Color(0xFF4CAF50), Color(0xFFFFA726), Color(0xFF26A69A)),
                        isSelected = currentTheme == ThemeMode.DINO_WORLD,
                        onClick = {
                            soundManager.playCorrectChime()
                            onThemeChange(ThemeMode.DINO_WORLD)
                        },
                        tag = "settings_theme_dino_world"
                    )

                    ThemeSelectCard(
                        emoji = "🚀",
                        title = "Space Adventure",
                        subtitle = "Deep cosmic purple, twinkling stars & supersonic rockets",
                        palette = listOf(Color(0xFF5C6BC0), Color(0xFFFFD54F), Color(0xFFAB47BC)),
                        isSelected = currentTheme == ThemeMode.SPACE_ADVENTURE,
                        onClick = {
                            soundManager.playCorrectChime()
                            onThemeChange(ThemeMode.SPACE_ADVENTURE)
                        },
                        tag = "settings_theme_space_adventure"
                    )
                }
            }

            // ==========================================
            // 2. LANGUAGE SELECTION SECTION
            // ==========================================
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(24.dp))
                    .testTag("settings_language_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("🌐", fontSize = 28.sp)
                        Column {
                            Text(
                                text = stringResource(R.string.language_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Narration voice, phonics & UI language",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LanguageOptionCard(
                            flag = "🇺🇸",
                            label = "English",
                            subLabel = "Phonics & Voice",
                            isSelected = currentLanguage != "hi",
                            onClick = {
                                soundManager.playButtonTap()
                                onLanguageChange("en")
                            },
                            modifier = Modifier.weight(1f),
                            tag = "settings_lang_en"
                        )

                        LanguageOptionCard(
                            flag = "🇮🇳",
                            label = "हिन्दी",
                            subLabel = "स्वर एवं वर्णमाला",
                            isSelected = currentLanguage == "hi",
                            onClick = {
                                soundManager.playButtonTap()
                                onLanguageChange("hi")
                            },
                            modifier = Modifier.weight(1f),
                            tag = "settings_lang_hi"
                        )
                    }
                }
            }

            // ==========================================
            // 3. SOUND & BACKGROUND MUSIC SETTINGS
            // ==========================================
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(24.dp))
                    .testTag("settings_sound_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("🎶", fontSize = 28.sp)
                        Column {
                            Text(
                                text = "Music & Sound Effects",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Calm background music and encouragement voice",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Background Music Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Background Music",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (backgroundMusicEnabled) "Gentle lullaby active 🎵" else "Music paused 🔇",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = backgroundMusicEnabled,
                            onCheckedChange = {
                                soundManager.playButtonTap()
                                onBackgroundMusicChange(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("settings_switch_bg_music")
                        )
                    }

                    // Voice & SFX Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Voice Guidance & Chimes",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (audioFeedbackEnabled) "Child voice narration on 🗣️" else "Narration muted",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = audioFeedbackEnabled,
                            onCheckedChange = {
                                soundManager.playButtonTap()
                                onAudioFeedbackChange(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.secondary,
                                checkedTrackColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            modifier = Modifier.testTag("settings_switch_sfx")
                        )
                    }

                    // App Volume Slider
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "App Volume",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "${(appVolume * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Slider(
                            value = appVolume,
                            onValueChange = onVolumeChange,
                            valueRange = 0.10f..1.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_slider_volume")
                        )
                    }
                }
            }

            // ==========================================
            // 4. QUICK SHORTCUTS SECTION
            // ==========================================
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "More Options & Parent Hub",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    ShortcutCard(
                        emoji = "🛡️",
                        title = "Parent Zone & Safety",
                        subtitle = "Session limits, PIN protection, curriculum logs",
                        onClick = {
                            soundManager.playButtonTap()
                            onNavigateParent()
                        },
                        tag = "settings_shortcut_parent"
                    )

                    ShortcutCard(
                        emoji = "⭐",
                        title = "Progress & Star Tracker",
                        subtitle = "Track mastered letters, numbers, and shapes",
                        onClick = {
                            soundManager.playButtonTap()
                            onNavigateProgress()
                        },
                        tag = "settings_shortcut_progress"
                    )

                    ShortcutCard(
                        emoji = "🎓",
                        title = "Milestone Certificates",
                        subtitle = "View and print completed achievement awards",
                        onClick = {
                            soundManager.playButtonTap()
                            onNavigateCertificates()
                        },
                        tag = "settings_shortcut_certificates"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Beautiful full-width Theme selection card with preview dots and active badge
 */
@Composable
private fun ThemeSelectCard(
    emoji: String,
    title: String,
    subtitle: String,
    palette: List<Color>,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "border_color"
    )
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.40f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = Modifier
            .fillMaxWidth()
            .border(width = if (isSelected) 3.dp else 1.dp, color = borderColor, shape = RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = emoji, fontSize = 32.sp)

                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Active ✓",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Palette color preview dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        palette.forEach { c ->
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(c)
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Clean Language option card
 */
@Composable
private fun LanguageOptionCard(
    flag: String,
    label: String,
    subLabel: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tag: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        modifier = modifier
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(flag, fontSize = 28.sp)
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (isSelected) {
                Text(
                    text = "Selected ✓",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Shortcut navigation card
 */
@Composable
private fun ShortcutCard(
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tag: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(emoji, fontSize = 24.sp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text("➔", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
        }
    }
}
