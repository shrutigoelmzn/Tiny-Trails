package com.example.feature.activity

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.example.core.audio.AudioPromptKey
import com.example.core.audio.SoundManager
import com.example.core.designsystem.components.KidsButton
import com.example.core.designsystem.components.KidsTopBar
import com.example.data.content.PrototypeCharacters
import com.example.data.model.MiniActivityType
import com.example.ui.theme.BalloonBlue
import com.example.ui.theme.BalloonGreen
import com.example.ui.theme.BalloonPurple
import com.example.ui.theme.BalloonRed
import com.example.ui.theme.BalloonYellow

@Composable
fun MiniActivityScreen(
    characterId: String,
    starsEarned: Int,
    onBack: () -> Unit,
    onActivityCompleted: (characterId: String, stars: Int) -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val soundManager = SoundManager.getInstance(context)
    val item = PrototypeCharacters.findById(characterId) ?: PrototypeCharacters.NUMBER_1

    var isCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        when (item.miniActivityType) {
            MiniActivityType.COUNT_OBJECTS -> soundManager.playPrompt(AudioPromptKey.COUNT_OBJECTS)
            MiniActivityType.BALLOON_POP -> soundManager.playPrompt(AudioPromptKey.POP_BALLOONS)
            MiniActivityType.FIND_OBJECT -> soundManager.playPrompt(AudioPromptKey.FIND_OBJECT)
            MiniActivityType.SIMPLE_MATCHING -> soundManager.playPrompt(AudioPromptKey.MATCH_CARDS)
            MiniActivityType.FEED_ANIMAL -> soundManager.playPrompt(AudioPromptKey.FEED_ANIMAL)
            MiniActivityType.TAP_CORRECT -> soundManager.playPrompt(AudioPromptKey.TAP_CORRECT)
        }
    }

    Scaffold(
        topBar = {
            KidsTopBar(
                title = stringResource(R.string.mini_activity_title),
                onBackClick = onBack,
                onAudioClick = {
                    if (isCompleted) {
                        soundManager.playPrompt(AudioPromptKey.ACTIVITY_COMPLETE)
                    } else {
                        soundManager.playPrompt(AudioPromptKey.GREAT_JOB)
                    }
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
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header instruction card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = if (isCompleted) {
                            "Activity Complete! ⭐ Tap Collect Reward!"
                        } else {
                            when (item.miniActivityType) {
                                MiniActivityType.COUNT_OBJECTS -> "Tap to count: ${item.exampleCount} ${item.exampleWord}!"
                                MiniActivityType.BALLOON_POP -> "Pop all the floating balloons! 🎈"
                                MiniActivityType.FIND_OBJECT -> "Can you find the ${item.exampleWord}? 🔍"
                                MiniActivityType.SIMPLE_MATCHING -> "Match '${item.displayValue}' with ${item.exampleWord}! 🧩"
                                MiniActivityType.FEED_ANIMAL -> "Tap food to feed the hungry friend! 😋"
                                MiniActivityType.TAP_CORRECT -> "Which one is '${item.displayValue}'? 🎯"
                            }
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }

                // Interactive Mini Game Area - strictly disabled once isCompleted is true
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (item.miniActivityType) {
                        MiniActivityType.COUNT_OBJECTS -> {
                            CountObjectsGame(
                                count = item.exampleCount,
                                emoji = item.exampleEmoji,
                                isCompleted = isCompleted,
                                soundManager = soundManager,
                                onCompleted = {
                                    if (!isCompleted) {
                                        isCompleted = true
                                        soundManager.playCompletionChimes()
                                        soundManager.playPrompt(AudioPromptKey.SUPERSTAR)
                                    }
                                }
                            )
                        }
                        MiniActivityType.BALLOON_POP -> {
                            BalloonPopGame(
                                isCompleted = isCompleted,
                                soundManager = soundManager,
                                onCompleted = {
                                    if (!isCompleted) {
                                        isCompleted = true
                                        soundManager.playCompletionChimes()
                                        soundManager.playPrompt(AudioPromptKey.SUPERSTAR)
                                    }
                                }
                            )
                        }
                        MiniActivityType.FIND_OBJECT -> {
                            FindObjectGame(
                                targetEmoji = item.exampleEmoji,
                                targetName = item.exampleWord,
                                isCompleted = isCompleted,
                                soundManager = soundManager,
                                onCompleted = {
                                    if (!isCompleted) {
                                        isCompleted = true
                                        soundManager.playCompletionChimes()
                                        soundManager.playPrompt(AudioPromptKey.GREAT_JOB)
                                    }
                                }
                            )
                        }
                        MiniActivityType.SIMPLE_MATCHING -> {
                            SimpleMatchingGame(
                                character = item.displayValue,
                                emoji = item.exampleEmoji,
                                isCompleted = isCompleted,
                                soundManager = soundManager,
                                onCompleted = {
                                    if (!isCompleted) {
                                        isCompleted = true
                                        soundManager.playCompletionChimes()
                                        soundManager.playPrompt(AudioPromptKey.SUPERSTAR)
                                    }
                                }
                            )
                        }
                        MiniActivityType.FEED_ANIMAL -> {
                            FeedAnimalGame(
                                foodEmoji = item.exampleEmoji,
                                isCompleted = isCompleted,
                                soundManager = soundManager,
                                onCompleted = {
                                    if (!isCompleted) {
                                        isCompleted = true
                                        soundManager.playCompletionChimes()
                                        soundManager.playPrompt(AudioPromptKey.SUPERSTAR)
                                    }
                                }
                            )
                        }
                        MiniActivityType.TAP_CORRECT -> {
                            TapCorrectGame(
                                targetChar = item.displayValue,
                                isCompleted = isCompleted,
                                soundManager = soundManager,
                                onCompleted = {
                                    if (!isCompleted) {
                                        isCompleted = true
                                        soundManager.playCompletionChimes()
                                        soundManager.playPrompt(AudioPromptKey.GREAT_JOB)
                                    }
                                }
                            )
                        }
                    }
                }

                // Bottom Continue button
                KidsButton(
                    text = if (isCompleted) "Collect Reward! 🎁" else "Skip to Reward ▶",
                    onClick = {
                        onActivityCompleted(characterId, starsEarned)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    backgroundColor = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    testTag = "activity_continue_button"
                )
            }
        }
    }
}

@Composable
private fun CountObjectsGame(
    count: Int,
    emoji: String,
    isCompleted: Boolean,
    soundManager: SoundManager,
    onCompleted: () -> Unit
) {
    val tappedIndices = remember { mutableStateListOf<Int>() }

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..count) {
            val isTapped = tappedIndices.contains(i)
            val scale by animateFloatAsState(
                targetValue = if (isTapped) 1.25f else 1.0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "scale_$i"
            )

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .scale(scale)
                    .shadow(if (isTapped) 8.dp else 4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(if (isTapped) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
                    .clickable(enabled = !isCompleted && !isTapped) {
                        if (isCompleted) return@clickable
                        tappedIndices.add(i)
                        soundManager.playToneSparkle()
                        soundManager.speak("$i")
                        if (tappedIndices.size == count) {
                            onCompleted()
                        }
                    }
                    .testTag("count_object_$i"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 44.sp
                )
            }
        }
    }
}

@Composable
private fun BalloonPopGame(
    isCompleted: Boolean,
    soundManager: SoundManager,
    onCompleted: () -> Unit
) {
    val poppedBalloons = remember { mutableStateListOf<Int>() }
    val balloons = listOf(
        Pair(BalloonRed, "🎈"),
        Pair(BalloonBlue, "🎈"),
        Pair(BalloonGreen, "🎈"),
        Pair(BalloonYellow, "🎈")
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        balloons.forEachIndexed { index, pair ->
            val isPopped = poppedBalloons.contains(index)
            if (!isPopped) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(6.dp, CircleShape)
                        .clip(CircleShape)
                        .background(pair.first)
                        .clickable(enabled = !isCompleted) {
                            if (isCompleted) return@clickable
                            poppedBalloons.add(index)
                            soundManager.playTonePop()
                            if (poppedBalloons.size == balloons.size) {
                                onCompleted()
                            }
                        }
                        .testTag("balloon_$index"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎈", fontSize = 42.sp)
                }
            } else {
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✨", fontSize = 32.sp)
                }
            }
        }
    }
}

@Composable
private fun FindObjectGame(
    targetEmoji: String,
    targetName: String,
    isCompleted: Boolean,
    soundManager: SoundManager,
    onCompleted: () -> Unit
) {
    val options = remember {
        listOf(
            Pair(targetEmoji, true),
            Pair("⚽", false),
            Pair("🚗", false)
        ).shuffled()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            options.forEachIndexed { idx, pair ->
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .shadow(6.dp, CircleShape)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable(enabled = !isCompleted) {
                            if (isCompleted) return@clickable
                            if (pair.second) {
                                onCompleted()
                            } else {
                                soundManager.playToneGentlePulse()
                                soundManager.playPrompt(AudioPromptKey.TRY_AGAIN)
                            }
                        }
                        .testTag("find_option_$idx"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = pair.first, fontSize = 48.sp)
                }
            }
        }

        if (isCompleted) {
            Text(
                text = "Superstar! 🌟 Found $targetName!",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SimpleMatchingGame(
    character: String,
    emoji: String,
    isCompleted: Boolean,
    soundManager: SoundManager,
    onCompleted: () -> Unit
) {
    var matchedCharacter by remember { mutableStateOf(false) }
    var matchedEmoji by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Tap both matching cards! 🧩",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            // Card 1: Character Glyph
            Card(
                modifier = Modifier
                    .size(110.dp)
                    .clickable(enabled = !isCompleted && !matchedCharacter) {
                        if (isCompleted) return@clickable
                        matchedCharacter = true
                        soundManager.playToneSparkle()
                        if (matchedEmoji) {
                            onCompleted()
                        }
                    }
                    .testTag("match_card_char"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (matchedCharacter) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = character,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Card 2: Object Emoji
            Card(
                modifier = Modifier
                    .size(110.dp)
                    .clickable(enabled = !isCompleted && !matchedEmoji) {
                        if (isCompleted) return@clickable
                        matchedEmoji = true
                        soundManager.playToneSparkle()
                        if (matchedCharacter) {
                            onCompleted()
                        }
                    }
                    .testTag("match_card_emoji"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (matchedEmoji) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = emoji, fontSize = 48.sp)
                }
            }
        }

        if (isCompleted) {
            Text(
                text = "Matched! 🌟 Great job!",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FeedAnimalGame(
    foodEmoji: String,
    isCompleted: Boolean,
    soundManager: SoundManager,
    onCompleted: () -> Unit
) {
    val foodsFed = remember { mutableStateListOf<Int>() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cute Animal Character
        Text(
            text = if (isCompleted) "🐻✨" else "🐻",
            fontSize = 72.sp
        )

        Text(
            text = if (isCompleted) "Nom nom nom! So full and happy! 😋" else "Feed me 3 treats!",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(3) { idx ->
                val isFed = foodsFed.contains(idx)
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .shadow(if (isFed) 0.dp else 4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(if (isFed) Color.LightGray.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface)
                        .clickable(enabled = !isCompleted && !isFed) {
                            if (isCompleted || isFed) return@clickable
                            foodsFed.add(idx)
                            soundManager.playTonePop()
                            if (foodsFed.size >= 3) {
                                onCompleted()
                            }
                        }
                        .testTag("feed_food_$idx"),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isFed) {
                        Text(text = foodEmoji, fontSize = 38.sp)
                    } else {
                        Text(text = "❤️", fontSize = 24.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun TapCorrectGame(
    targetChar: String,
    isCompleted: Boolean,
    soundManager: SoundManager,
    onCompleted: () -> Unit
) {
    val options = remember(targetChar) {
        val distractors = listOf("1", "A", "O", "5", "B", "9").filter { it != targetChar }
        val d1 = distractors.getOrNull(0) ?: "X"
        val d2 = distractors.getOrNull(1) ?: "Y"
        listOf(
            Pair(targetChar, true),
            Pair(d1, false),
            Pair(d2, false)
        ).shuffled()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Tap: '$targetChar'!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            options.forEachIndexed { idx, pair ->
                Card(
                    modifier = Modifier
                        .size(88.dp)
                        .clickable(enabled = !isCompleted) {
                            if (isCompleted) return@clickable
                            if (pair.second) {
                                onCompleted()
                            } else {
                                soundManager.playToneGentlePulse()
                                soundManager.playPrompt(AudioPromptKey.TRY_AGAIN)
                            }
                        }
                        .testTag("tap_option_$idx"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCompleted && pair.second) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = pair.first,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        if (isCompleted) {
            Text(
                text = "Bingo! 🎯 That's '$targetChar'!",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
