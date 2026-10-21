package com.example.feature.reward

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.audio.SoundManager
import com.example.core.designsystem.components.CelebratoryParticleEffect
import com.example.core.designsystem.components.KidsButton
import com.example.core.designsystem.components.KidsTopBar
import com.example.data.content.PrototypeCharacters
import com.example.data.model.RewardResult
import com.example.data.repository.ProgressRepository
import com.example.ui.theme.StarGold

@Composable
fun RewardScreen(
    characterId: String,
    starsEarned: Int,
    onNextLesson: (String) -> Unit,
    onBackHome: () -> Unit
) {
    BackHandler(onBack = onBackHome)
    val context = LocalContext.current
    val progressRepo = remember { ProgressRepository.getInstance(context) }
    val soundManager = SoundManager.getInstance(context)

    var rewardResult by remember { mutableStateOf<RewardResult?>(null) }
    var starAnimStarted by remember { mutableStateOf(false) }

    LaunchedEffect(characterId) {
        val result = progressRepo.recordCompletion(characterId, starsEarned)
        rewardResult = result
        soundManager.playToneSuccess()
        starAnimStarted = true
        soundManager.speak("Yippee yay! We did it! You earned $starsEarned shiny stars! High five, superstar!")
    }

    val starScale by animateFloatAsState(
        targetValue = if (starAnimStarted) 1.15f else 0.5f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "star_scale"
    )

    Scaffold(
        topBar = {
            KidsTopBar(
                title = stringResource(R.string.reward_screen_title),
                onBackClick = onBackHome,
                onAudioClick = {
                    soundManager.speak("You are doing so super-duper amazing! You're my favorite superstar!")
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
            // Background Celebratory Confetti & Star Particles
            CelebratoryParticleEffect(
                modifier = Modifier.fillMaxSize(),
                isTriggered = true,
                particleCount = 100
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
            // Main Celebration Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(8.dp, RoundedCornerShape(32.dp)),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🏆",
                        fontSize = 72.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Lesson Complete!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stars
                    Row(
                        modifier = Modifier.scale(starScale),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        repeat(starsEarned) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Star",
                                tint = StarGold,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Milestone Sticker / Garden Reward Card if unlocked!
                    rewardResult?.newlyUnlockedSticker?.let { sticker ->
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            modifier = Modifier.fillMaxWidth().testTag("reward_unlocked_sticker_card")
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(text = sticker.iconEmoji, fontSize = 42.sp)
                                Column {
                                    Text(
                                        text = "New Sticker: ${sticker.displayName}!",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = "Added to your Sticker Gallery!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                    }

                    rewardResult?.newlyUnlockedGardenItem?.let { gardenItem ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Garden Update: $gardenItem",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Find next lesson
                val all = PrototypeCharacters.ALL_ITEMS
                val currentIdx = all.indexOfFirst { it.id == characterId }
                val nextItem = if (currentIdx in 0 until all.size - 1) all[currentIdx + 1] else all[0]

                KidsButton(
                    text = "Next: ${nextItem.displayValue} ${nextItem.exampleWord} ▶",
                    onClick = { onNextLesson(nextItem.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    testTag = "reward_next_lesson_button"
                )

                KidsButton(
                    text = stringResource(R.string.back_to_home),
                    onClick = onBackHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    testTag = "reward_back_home_button"
                )
            }
        }
    }
}
}
