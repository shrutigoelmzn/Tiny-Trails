package com.example.feature.pet

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.audio.SoundManager
import com.example.core.designsystem.components.KidsButton
import com.example.core.designsystem.components.KidsTopBar
import com.example.core.designsystem.components.ParticleExplosionEffect
import com.example.data.model.PetAccessory
import com.example.data.model.PetMood
import com.example.data.repository.PetRepository
import com.example.data.repository.ProgressRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun VirtualPetScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val petRepo = remember { PetRepository.getInstance(context) }
    val progressRepo = remember { ProgressRepository.getInstance(context) }
    val soundManager = remember { SoundManager.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()

    val petState by petRepo.petStateFlow.collectAsState()
    val progressMap by progressRepo.progressFlow.collectAsState()

    val totalStarsEarned = remember(progressMap) {
        progressMap.values.sumOf { it.stars }
    }
    val availableStars = (totalStarsEarned - petState.totalStarsFed).coerceAtLeast(0)

    var currentMood by remember { mutableStateOf(PetMood.HAPPY) }
    var levelUpExplosionCount by remember { mutableIntStateOf(0) }
    var showHeartBubble by remember { mutableStateOf(false) }
    var actionMessage by remember { mutableStateOf("Pip is happy to see you! ⭐") }

    // Breathing & Idle Bounce Animation
    val infiniteTransition = rememberInfiniteTransition(label = "pet_idle")
    val idleBounce by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_bounce"
    )

    val eyeBlink by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "eye_blink"
    )

    // Interactive Jump Scale
    val jumpScale = remember { Animatable(1f) }
    val danceRotation = remember { Animatable(0f) }

    fun triggerJump() {
        coroutineScope.launch {
            jumpScale.animateTo(1.22f, spring(dampingRatio = 0.35f, stiffness = 600f))
            jumpScale.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = 400f))
        }
    }

    Scaffold(
        topBar = {
            KidsTopBar(
                title = "Pip the Pet Dino 🦕",
                onBackClick = onBack,
                onAudioClick = {
                    soundManager.speak("Meet Pip your little dino buddy! Feed Pip shiny stars to level up and unlock funny hats!")
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Star Bank & Friendship Level
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Friendship Level Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFD54F)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👑", fontSize = 18.sp)
                        }
                        Column {
                            Text(
                                text = "Friendship Level ${petState.level}",
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${petState.starsNeededForNextLevel - petState.starsFedThisLevel} stars to level up!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Available Stars Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFFFF9C4))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("⭐", fontSize = 16.sp)
                            Text(
                                text = "$availableStars",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFFF57F17)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Level Progress Bar
                LinearProgressIndicator(
                    progress = { petState.progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = Color(0xFFFFB300),
                    trackColor = Color(0xFFFFECB3)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Pip Speech / Reaction Bubble
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = actionMessage,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Animated Pet Pip Canvas
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .offset { IntOffset(0, idleBounce.roundToInt()) }
                        .scale(jumpScale.value)
                        .rotate(danceRotation.value)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            soundManager.playCorrectChime()
                            triggerJump()
                            currentMood = PetMood.GIGGLING
                            actionMessage = "Tee-hee! That tickles! 😄💖"
                            petRepo.ticklePet()
                            soundManager.speakPetReaction("tickle")
                        }
                        .testTag("virtual_pet_pip"),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawPetPip(
                            mood = currentMood,
                            eyeBlink = eyeBlink,
                            accessory = petState.equippedAccessory
                        )
                    }

                    // Floating Heart on Feeding / Tickling
                    if (showHeartBubble) {
                        Text(
                            text = "💖",
                            fontSize = 36.sp,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-20).dp, y = (-10).dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons Row (Feed, Tickle, Dance)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Feed Button
                    KidsButton(
                        text = "Feed ⭐",
                        onClick = {
                            if (availableStars > 0) {
                                soundManager.playStarEarned()
                                currentMood = PetMood.EATING
                                showHeartBubble = true
                                triggerJump()
                                val leveledUp = petRepo.feedStar()
                                if (leveledUp) {
                                    levelUpExplosionCount++
                                    soundManager.playCharacterTraceCompletion("Pip", stars = 3, isHindi = false)
                                    actionMessage = "Hooray! Pip leveled up to Level ${petState.level + 1}! 🎉🥳"
                                    soundManager.speak("Yippee! Pip grew bigger and leveled up! High five, superstar!")
                                } else {
                                    actionMessage = "Yummy! Pip loved that star treat! 🍓😋"
                                    soundManager.speakPetReaction("feed")
                                }
                                coroutineScope.launch {
                                    delay(1600)
                                    showHeartBubble = false
                                    currentMood = PetMood.HAPPY
                                }
                            } else {
                                soundManager.playButtonTap()
                                actionMessage = "Trace more letters & numbers to earn stars for Pip! ⭐"
                                soundManager.speak("Trace more letters and numbers with me to earn shiny stars for Pip! You can do it!")
                            }
                        },
                        backgroundColor = Color(0xFFFF7043),
                        contentColor = Color.White,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pet_action_feed")
                    )

                    // Tickle Button
                    KidsButton(
                        text = "Tickle 😄",
                        onClick = {
                            soundManager.playCorrectChime()
                            currentMood = PetMood.GIGGLING
                            triggerJump()
                            actionMessage = "Giggle giggle! Pip loves playtime! 🎈"
                            petRepo.ticklePet()
                            soundManager.speakPetReaction("tickle")
                            coroutineScope.launch {
                                delay(1200)
                                currentMood = PetMood.HAPPY
                            }
                        },
                        backgroundColor = Color(0xFF42A5F5),
                        contentColor = Color.White,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pet_action_tickle")
                    )

                    // Dance Button
                    KidsButton(
                        text = "Dance 🎵",
                        onClick = {
                            soundManager.playStarEarned()
                            currentMood = PetMood.DANCING
                            actionMessage = "Pip is doing the Dino Wiggle dance! 🎶"
                            soundManager.speakPetReaction("dance")
                            coroutineScope.launch {
                                repeat(3) {
                                    danceRotation.animateTo(12f, tween(140))
                                    danceRotation.animateTo(-12f, tween(140))
                                }
                                danceRotation.animateTo(0f, spring())
                                currentMood = PetMood.HAPPY
                            }
                        },
                        backgroundColor = Color(0xFFAB47BC),
                        contentColor = Color.White,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pet_action_dance")
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Dress-up Accessories Closet
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Costumes & Hats 🎩",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(PetAccessory.entries) { acc ->
                            val isUnlocked = petState.unlockedAccessories.contains(acc)
                            val isEquipped = petState.equippedAccessory == acc

                            Card(
                                modifier = Modifier
                                    .size(width = 86.dp, height = 90.dp)
                                    .shadow(if (isEquipped) 6.dp else 2.dp, RoundedCornerShape(18.dp))
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable {
                                        if (isUnlocked) {
                                            soundManager.playButtonTap()
                                            petRepo.equipAccessory(acc)
                                            actionMessage = "Pip put on the ${acc.title}! ✨"
                                            triggerJump()
                                        } else {
                                            actionMessage = "Unlock at Level ${acc.unlockLevel}! 🔒"
                                        }
                                    }
                                    .testTag("pet_accessory_${acc.id}"),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isEquipped) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else if (isUnlocked) {
                                        MaterialTheme.colorScheme.surface
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    }
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = if (isUnlocked) acc.emoji else "🔒",
                                        fontSize = 26.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isUnlocked) acc.title else "Lv ${acc.unlockLevel}",
                                        fontSize = 11.sp,
                                        fontWeight = if (isEquipped) FontWeight.ExtraBold else FontWeight.Medium,
                                        color = if (isEquipped) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Level-up celebratory explosion
            if (levelUpExplosionCount > 0) {
                ParticleExplosionEffect(
                    modifier = Modifier.fillMaxSize(),
                    isTriggered = true,
                    triggerKey = levelUpExplosionCount
                )
            }
        }
    }
}

/**
 * Custom toddler-friendly Dino/Bunny Pip drawing on Compose Canvas
 */
private fun DrawScope.drawPetPip(
    mood: PetMood,
    eyeBlink: Float,
    accessory: PetAccessory
) {
    val cx = size.width / 2f
    val cy = size.height / 2f + 15f

    // Soft Shadow Under Pet
    drawOval(
        color = Color(0x22000000),
        topLeft = Offset(cx - 75f, cy + 85f),
        size = Size(150f, 32f)
    )

    // Tail on back
    drawOval(
        color = Color(0xFF66BB6A),
        topLeft = Offset(cx + 60f, cy + 30f),
        size = Size(45f, 35f)
    )

    // Main Body (Round soft green dinosaur/companion)
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF81C784), Color(0xFF4CAF50)),
            startY = cy - 70f,
            endY = cy + 90f
        ),
        topLeft = Offset(cx - 80f, cy - 70f),
        size = Size(160f, 160f),
        cornerRadius = CornerRadius(70f, 70f)
    )

    // Cute belly oval (Pastel yellow)
    drawOval(
        color = Color(0xFFFFF9C4),
        topLeft = Offset(cx - 50f, cy - 10f),
        size = Size(100f, 90f)
    )

    // Back spikes (Dino soft spines)
    val spineColor = Color(0xFFFFB74D)
    drawCircle(color = spineColor, radius = 16f, center = Offset(cx - 65f, cy - 60f))
    drawCircle(color = spineColor, radius = 20f, center = Offset(cx, cy - 80f))
    drawCircle(color = spineColor, radius = 16f, center = Offset(cx + 65f, cy - 60f))

    // Rosy Blushing Cheeks
    drawCircle(color = Color(0x66FF8A80), radius = 14f, center = Offset(cx - 50f, cy + 10f))
    drawCircle(color = Color(0x66FF8A80), radius = 14f, center = Offset(cx + 50f, cy + 10f))

    // Expressive Eyes
    val eyeHeight = (22f * eyeBlink).coerceAtLeast(2f)
    // Left Eye
    drawOval(
        color = Color(0xFF212121),
        topLeft = Offset(cx - 38f, cy - 35f + (22f - eyeHeight) / 2f),
        size = Size(16f, eyeHeight)
    )
    // Right Eye
    drawOval(
        color = Color(0xFF212121),
        topLeft = Offset(cx + 22f, cy - 35f + (22f - eyeHeight) / 2f),
        size = Size(16f, eyeHeight)
    )

    // Eye Twinkle Highlights (if eyes open)
    if (eyeBlink > 0.4f) {
        drawCircle(color = Color.White, radius = 4f, center = Offset(cx - 34f, cy - 30f))
        drawCircle(color = Color.White, radius = 4f, center = Offset(cx + 26f, cy - 30f))
    }

    // Mouth rendering based on mood
    when (mood) {
        PetMood.EATING -> {
            // Open mouth chewing
            drawOval(
                color = Color(0xFFB71C1C),
                topLeft = Offset(cx - 16f, cy - 5f),
                size = Size(32f, 24f)
            )
            drawOval(
                color = Color(0xFFFF8A80),
                topLeft = Offset(cx - 10f, cy + 4f),
                size = Size(20f, 10f)
            )
        }
        PetMood.GIGGLING, PetMood.DANCING -> {
            // Big happy open smile
            drawArc(
                color = Color(0xFF212121),
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(cx - 18f, cy - 6f),
                size = Size(36f, 26f)
            )
        }
        else -> {
            // Gentle warm smile
            drawArc(
                color = Color(0xFF212121),
                startAngle = 15f,
                sweepAngle = 150f,
                useCenter = false,
                topLeft = Offset(cx - 14f, cy - 8f),
                size = Size(28f, 18f),
                style = Stroke(width = 4f)
            )
        }
    }

    // Two little feet
    drawRoundRect(
        color = Color(0xFF388E3C),
        topLeft = Offset(cx - 65f, cy + 76f),
        size = Size(40f, 20f),
        cornerRadius = CornerRadius(10f, 10f)
    )
    drawRoundRect(
        color = Color(0xFF388E3C),
        topLeft = Offset(cx + 25f, cy + 76f),
        size = Size(40f, 20f),
        cornerRadius = CornerRadius(10f, 10f)
    )

    // Accessories Over Head
    when (accessory) {
        PetAccessory.PARTY_HAT -> {
            // Cone party hat
            drawArc(
                color = Color(0xFFFF4081),
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = true,
                topLeft = Offset(cx - 24f, cy - 120f),
                size = Size(48f, 65f)
            )
            drawCircle(color = Color(0xFFFFD54F), radius = 8f, center = Offset(cx, cy - 124f))
        }
        PetAccessory.ROYAL_CROWN -> {
            // Golden Crown
            drawRoundRect(
                color = Color(0xFFFFD700),
                topLeft = Offset(cx - 36f, cy - 108f),
                size = Size(72f, 36f),
                cornerRadius = CornerRadius(6f, 6f)
            )
            // Jewels
            drawCircle(color = Color(0xFFE91E63), radius = 4f, center = Offset(cx - 20f, cy - 96f))
            drawCircle(color = Color(0xFF2196F3), radius = 5f, center = Offset(cx, cy - 96f))
            drawCircle(color = Color(0xFFE91E63), radius = 4f, center = Offset(cx + 20f, cy - 96f))
        }
        PetAccessory.FLOWER_CROWN -> {
            // Ring of flower dots
            val flowerColors = listOf(Color(0xFFFF80AB), Color(0xFFFFEE58), Color(0xFF80D8FF), Color(0xFFB9F6CA))
            for (i in 0..5) {
                val fx = cx - 40f + (i * 16f)
                val fy = cy - 78f + (sin(i.toDouble()) * 4f).toFloat()
                drawCircle(color = flowerColors[i % flowerColors.size], radius = 8f, center = Offset(fx, fy))
            }
        }
        PetAccessory.SUNGLASSES -> {
            // Cool shades
            drawRoundRect(
                color = Color(0xFF212121),
                topLeft = Offset(cx - 45f, cy - 42f),
                size = Size(40f, 24f),
                cornerRadius = CornerRadius(6f, 6f)
            )
            drawRoundRect(
                color = Color(0xFF212121),
                topLeft = Offset(cx + 5f, cy - 42f),
                size = Size(40f, 24f),
                cornerRadius = CornerRadius(6f, 6f)
            )
            drawLine(
                color = Color(0xFF212121),
                start = Offset(cx - 5f, cy - 30f),
                end = Offset(cx + 5f, cy - 30f),
                strokeWidth = 4f
            )
        }
        PetAccessory.SUPER_CAPE -> {
            // Superhero red cape peeking from sides
            drawOval(
                color = Color(0xFFD32F2F),
                topLeft = Offset(cx - 95f, cy + 10f),
                size = Size(35f, 65f)
            )
            drawOval(
                color = Color(0xFFD32F2F),
                topLeft = Offset(cx + 60f, cy + 10f),
                size = Size(35f, 65f)
            )
        }
        PetAccessory.NONE -> {}
    }
}
