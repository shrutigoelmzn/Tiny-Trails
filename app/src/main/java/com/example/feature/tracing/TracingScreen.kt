package com.example.feature.tracing

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.core.designsystem.components.CelebratoryParticleEffect
import com.example.core.designsystem.components.KidsButton
import com.example.core.designsystem.components.KidsTopBar
import com.example.core.designsystem.components.ParticleExplosionEffect
import com.example.data.content.PrototypeCharacters
import com.example.ui.theme.StarGold

@Composable
fun TracingScreen(
    characterId: String,
    onBack: () -> Unit,
    onTracingComplete: (characterId: String, stars: Int) -> Unit,
    viewModel: TracingViewModel = viewModel()
) {
    BackHandler(onBack = onBack)

    LaunchedEffect(characterId) {
        viewModel.loadCharacter(characterId)
    }

    val state by viewModel.uiState.collectAsState()
    val demoState by viewModel.demoState.collectAsState()
    val traceUiState by viewModel.traceUiState.collectAsState()
    val celebrationCount by viewModel.celebrationTriggerCount.collectAsState()
    val characterContent = PrototypeCharacters.findById(characterId) ?: PrototypeCharacters.NUMBER_1

    var showCompletionCard by remember { mutableStateOf(false) }

    LaunchedEffect(state?.isCharacterCompleted, celebrationCount) {
        if (state?.isCharacterCompleted == true) {
            // First let the dramatic confetti explosion burst across the canvas for 750ms
            delay(750)
            showCompletionCard = true
        } else {
            showCompletionCard = false
        }
    }

    Scaffold(
        topBar = {
            KidsTopBar(
                title = stringResource(R.string.trace_instruction),
                onBackClick = onBack,
                onAudioClick = { viewModel.speakCharacter() }
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
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Demo banner or status bar
                if (demoState.isPlaying) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "👀 Watch how to trace '${characterContent.displayValue}'!",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            TextButton(
                                onClick = { viewModel.skipDemo() },
                                modifier = Modifier.testTag("skip_demo_button")
                            ) {
                                Text(
                                    text = "Skip ▶",
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // Header with stars & progress info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Star rating
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(3) { index ->
                            val isEarned = (state?.stars ?: 3) > index
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Star",
                                tint = if (isEarned) StarGold else Color.LightGray.copy(alpha = 0.5f),
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    // Multi-stroke indicator
                    val totalStrokes = state?.characterDef?.strokes?.size ?: 1
                    val currentStrokeNumber = ((state?.currentStrokeIndex ?: 0) + 1).coerceAtMost(totalStrokes)
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            text = if (totalStrokes > 1) {
                                stringResource(R.string.stroke_progress, currentStrokeNumber, totalStrokes)
                            } else {
                                "Stroke 1 of 1"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    // Action buttons: "Watch Demo 💡" & Retry
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Replay / Help Demo button
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .clickable(enabled = traceUiState !is TraceUiState.Demo) { viewModel.replayDemo() }
                                .testTag("replay_demo_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = "💡", fontSize = 16.sp)
                                Text(
                                    text = "Demo",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }

                        // Retry button
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .shadow(2.dp, CircleShape)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .testTag("tracing_retry_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(onClick = { viewModel.retry() }) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = stringResource(R.string.retry),
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Real-time Visual Path Progress Indicator
                val currentProgress = if (demoState.isPlaying) {
                    demoState.progress
                } else {
                    state?.currentStrokeProgress ?: 0f
                }
                val totalStrokes = state?.characterDef?.strokes?.size ?: 1
                val currentStrokeNum = ((state?.currentStrokeIndex ?: 0) + 1).coerceAtMost(totalStrokes)

                TracingProgressIndicator(
                    strokeProgress = currentProgress,
                    currentStroke = currentStrokeNum,
                    totalStrokes = totalStrokes,
                    isCompleted = state?.isCharacterCompleted == true,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                // The Worksheet Tracing Canvas
                state?.let { tracingState ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White)
                            .padding(8.dp)
                    ) {
                        TracingCanvas(
                            state = tracingState,
                            demoState = demoState,
                            onTouchDown = { viewModel.onTouchDown(it) },
                            onTouchMove = { viewModel.onTouchMove(it) },
                            onTouchUp = { viewModel.onTouchUp() },
                            traceUiState = traceUiState
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Child instruction footer
                Text(
                    text = when (traceUiState) {
                        is TraceUiState.Loading -> "Getting ready..."
                        is TraceUiState.Demo -> "👀 Watching how to trace '${characterContent.displayValue}'..."
                        is TraceUiState.ReadyToTrace -> "✨ Your turn! Start at badge ①"
                        is TraceUiState.Tracing -> "Keep going inside the lines! 🌟"
                        is TraceUiState.Completed -> "🎉 You did it! Super star!"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))
            }

            // Particle Explosion Effect using Compose Animation (Confetti, Stars & Sparkles)
            if (state?.isCharacterCompleted == true) {
                ParticleExplosionEffect(
                    modifier = Modifier.fillMaxSize(),
                    isTriggered = true,
                    triggerKey = celebrationCount,
                    particleCount = 180,
                    showShockwave = true,
                    enableTapBurst = true
                )
            }

            // Completion Overlay Dialog (smoothly appears after the initial confetti burst)
            AnimatedVisibility(
                visible = showCompletionCard && state?.isCharacterCompleted == true,
                enter = fadeIn(tween(400)) + scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)),
                modifier = Modifier.align(Alignment.Center)
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "celebration_wobble")
                val bounceScale by infiniteTransition.animateFloat(
                    initialValue = 0.94f,
                    targetValue = 1.10f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(700, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "bounce"
                )

                Card(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(0.9f)
                        .shadow(16.dp, RoundedCornerShape(28.dp))
                        .testTag("tracing_completed_card"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .scale(bounceScale)
                                .size(76.dp)
                                .shadow(6.dp, CircleShape)
                                .clip(CircleShape)
                                .background(Color(0xFFFFF9C4))
                                .clickable { viewModel.replayCelebration() }
                                .testTag("tracing_completed_badge"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🌟",
                                fontSize = 42.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = stringResource(R.string.reward_screen_title),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "You traced '${characterContent.displayValue}' amazingly!",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Audio cheer replay pill
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
                                .clickable { viewModel.replayCelebration() }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("tracing_replay_cheer_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Hear praise again",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Hear Cheer 📣",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(state?.stars ?: 3) { starIdx ->
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable { viewModel.playStarSound(starIdx) }
                                        .testTag("tracing_completed_star_$starIdx")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Star ${starIdx + 1}",
                                        tint = StarGold,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        KidsButton(
                            text = "Play Activity 🎈",
                            onClick = {
                                onTracingComplete(characterId, state?.stars ?: 3)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "tracing_continue_activity_button"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TracingProgressIndicator(
    strokeProgress: Float,
    currentStroke: Int,
    totalStrokes: Int,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = strokeProgress.coerceIn(0f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "tracing_progress"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("tracing_progress_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "✏️ Path Progress",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (totalStrokes > 1) {
                        Text(
                            text = "(Stroke $currentStroke/$totalStrokes)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                val percent = (animatedProgress * 100).toInt()
                val statusText = when {
                    isCompleted || percent >= 98 -> "Done! 🌟"
                    percent > 75 -> "Almost there! 🚀"
                    percent > 35 -> "Keep going! 👏"
                    percent > 8 -> "Tracing... ✨"
                    else -> "Start at star ⭐"
                }

                Text(
                    text = "$percent% • $statusText",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Gliding mascot progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.85f))
                    .testTag("tracing_progress_bar"),
                contentAlignment = Alignment.CenterStart
            ) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("tracing_progress_indicator"),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.LightGray.copy(alpha = 0.30f),
                    strokeCap = StrokeCap.Round
                )

                // Mascot sliding along the progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress.coerceIn(0.05f, 0.98f))
                        .padding(end = 2.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = if (animatedProgress >= 0.95f) "🌟" else "⭐",
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
