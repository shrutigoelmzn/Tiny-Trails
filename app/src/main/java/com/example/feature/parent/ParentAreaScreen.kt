package com.example.feature.parent

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.designsystem.components.KidsTopBar
import com.example.data.content.CharacterRepository
import com.example.data.journey.DailyJourneyManager
import com.example.data.repository.ProgressRepository
import com.example.ui.theme.StarGold
import kotlin.random.Random

@Composable
fun ParentGateDialog(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val num1 = remember { Random.nextInt(3, 8) }
    val num2 = remember { Random.nextInt(2, 7) }
    val expectedAnswer = num1 + num2

    var inputAnswer by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Parent Gate",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "Grown-Ups Only 🛡️",
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Please solve this quick problem to enter Parent Area:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "$num1 + $num2 = ?",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                OutlinedTextField(
                    value = inputAnswer,
                    onValueChange = {
                        inputAnswer = it.take(3)
                        showError = false
                    },
                    label = { Text("Answer") },
                    isError = showError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("parent_gate_input")
                )
                if (showError) {
                    Text(
                        text = "Incorrect answer. Please try again.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (inputAnswer.trim() == expectedAnswer.toString()) {
                        onSuccess()
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier.testTag("parent_gate_confirm_button")
            ) {
                Text("Enter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ParentAreaScreen(
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val progressRepo = remember { ProgressRepository.getInstance(context) }
    val progressMap by progressRepo.progressFlow.collectAsState()
    val stickers by progressRepo.stickersFlow.collectAsState()
    val gardenState by progressRepo.gardenFlow.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }

    // Progress metrics
    val totalNumbers = CharacterRepository.numbers.size
    val completedNumbers = CharacterRepository.numbers.count { progressMap[it.id]?.completed == true }
    val numbersPercent = if (totalNumbers > 0) completedNumbers / totalNumbers.toFloat() else 0f

    val totalUpper = CharacterRepository.uppercase.size
    val completedUpper = CharacterRepository.uppercase.count { progressMap[it.id]?.completed == true }
    val upperPercent = if (totalUpper > 0) completedUpper / totalUpper.toFloat() else 0f

    val totalLower = CharacterRepository.lowercase.size
    val completedLower = CharacterRepository.lowercase.count { progressMap[it.id]?.completed == true }
    val lowerPercent = if (totalLower > 0) completedLower / totalLower.toFloat() else 0f

    val needsPractice = remember(progressMap) {
        DailyJourneyManager.getNeedsPracticeList(progressMap)
    }

    val unlockedStickersCount = stickers.count { it.isUnlocked }

    Scaffold(
        topBar = {
            KidsTopBar(
                title = "Parent Dashboard 👨‍👩‍👧",
                onBackClick = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Learning Progress Overview",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            // Progress Category Cards
            ProgressCard(
                title = "Numbers 0–9",
                completed = completedNumbers,
                total = totalNumbers,
                percent = numbersPercent,
                emoji = "🔢"
            )

            ProgressCard(
                title = "Uppercase Letters A–Z",
                completed = completedUpper,
                total = totalUpper,
                percent = upperPercent,
                emoji = "🔤"
            )

            ProgressCard(
                title = "Lowercase Letters a–z",
                completed = completedLower,
                total = totalLower,
                percent = lowerPercent,
                emoji = "🔡"
            )

            // Rewards & Garden Summary
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Achievements & Growth",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "⭐ Stickers Unlocked: $unlockedStickersCount of ${stickers.size}")
                        Text(text = "🌸 Flowers Bloomed: ${gardenState.flowersBloomed}")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "🌱 Plants Grown: ${gardenState.plantsGrown}")
                        Text(text = "🦋 Butterflies: ${gardenState.butterflies}")
                    }
                }
            }

            // Needs Practice / Suggested Practice
            if (needsPractice.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Suggested Practice (Retried / Needs Help)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            needsPractice.forEach { char ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                                ) {
                                    Text(
                                        text = "${char.displayValue} (${char.exampleWord})",
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Reset Progress Section
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { showResetDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp).testTag("parent_reset_progress_button")
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Reset Progress")
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = "Reset All Learning Progress", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(text = "Reset Progress?", fontWeight = FontWeight.Bold) },
            text = {
                Text("This will permanently clear all completed lessons, stars, stickers, and garden flowers. This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        progressRepo.resetAllProgress()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_reset_progress_button")
                ) {
                    Text("Yes, Reset Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProgressCard(
    title: String,
    completed: Int,
    total: Int,
    percent: Float,
    emoji: String
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    Text(text = emoji, fontSize = 24.sp)
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${(percent * 100).toInt()}% ($completed / $total)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            LinearProgressIndicator(
                progress = { percent },
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}
