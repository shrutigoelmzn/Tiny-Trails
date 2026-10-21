package com.example.feature.stickers

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.audio.SoundManager
import com.example.core.designsystem.components.KidsButton
import com.example.core.designsystem.components.KidsTopBar
import com.example.data.model.StickerCategory
import com.example.data.model.StickerItem
import com.example.data.repository.ProgressRepository

@Composable
fun StickerGalleryScreen(
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val progressRepo = remember { ProgressRepository.getInstance(context) }
    val soundManager = SoundManager.getInstance(context)
    val stickers by progressRepo.stickersFlow.collectAsState()

    var activePreviewSticker by remember { mutableStateOf<StickerItem?>(null) }
    var selectedCategory by remember { mutableStateOf<StickerCategory?>(null) }

    val filteredStickers = remember(stickers, selectedCategory) {
        if (selectedCategory == null) stickers else stickers.filter { it.category == selectedCategory }
    }

    Scaffold(
        topBar = {
            KidsTopBar(
                title = stringResource(R.string.stickers_title),
                onBackClick = onBack,
                onAudioClick = {
                    soundManager.speak("Tap any sticker to hear its name!")
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Category Filter Tabs
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedCategory == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.clickable { selectedCategory = null }
                        ) {
                            Text(
                                text = "All",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedCategory == null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                    items(StickerCategory.values().toList()) { cat ->
                        val isSelected = selectedCategory == cat
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.clickable { selectedCategory = cat }
                        ) {
                            Text(
                                text = cat.title,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "Collect stickers by completing lessons! Tap to pronounce:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredStickers) { sticker ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .shadow(6.dp, RoundedCornerShape(24.dp))
                                .clip(RoundedCornerShape(24.dp))
                                .clickable {
                                    if (sticker.isUnlocked) {
                                        activePreviewSticker = sticker
                                        soundManager.playToneSparkle()
                                        soundManager.speak(sticker.pronunciationText)
                                    } else {
                                        soundManager.playToneGentlePulse()
                                        soundManager.speak("Complete lessons to unlock this sticker!")
                                    }
                                }
                                .testTag("sticker_card_${sticker.id}"),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (sticker.isUnlocked) {
                                    MaterialTheme.colorScheme.surface
                                } else {
                                    Color.LightGray.copy(alpha = 0.25f)
                                }
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                if (sticker.isUnlocked) {
                                    Text(
                                        text = sticker.iconEmoji,
                                        fontSize = 52.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = sticker.displayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Locked",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Enlarged Overlay Dialog with Pronunciation Audio
            activePreviewSticker?.let { sticker ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                        .clickable { activePreviewSticker = null },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .padding(28.dp)
                            .fillMaxWidth(0.85f)
                            .shadow(16.dp, RoundedCornerShape(32.dp))
                            .clickable(enabled = false) {}
                            .testTag("sticker_preview_dialog"),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray.copy(alpha = 0.3f))
                                        .clickable { activePreviewSticker = null },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = Color.DarkGray
                                    )
                                }
                            }

                            Text(
                                text = sticker.iconEmoji,
                                fontSize = 92.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = sticker.displayName,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = sticker.description,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Play Audio Button
                            KidsButton(
                                text = "Listen 🔊",
                                onClick = {
                                    soundManager.speak(sticker.pronunciationText)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "sticker_listen_button"
                            )
                        }
                    }
                }
            }
        }
    }
}
