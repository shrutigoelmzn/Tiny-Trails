package com.example.feature.garden

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.audio.SoundManager
import com.example.core.designsystem.components.KidsTopBar
import com.example.data.repository.ProgressRepository

@Composable
fun LearningGardenScreen(
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val progressRepo = remember { ProgressRepository.getInstance(context) }
    val soundManager = SoundManager.getInstance(context)
    val gardenState by progressRepo.gardenFlow.collectAsState()

    LaunchedEffect(Unit) {
        soundManager.explainGarden()
    }

    Scaffold(
        topBar = {
            KidsTopBar(
                title = stringResource(R.string.garden_title),
                onBackClick = onBack,
                onAudioClick = {
                    soundManager.explainGarden()
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Garden Scene Card
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
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Sky / Sun
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "☀️", fontSize = 48.sp)
                        Text(text = "☁️", fontSize = 40.sp)
                    }

                    // Garden Living Elements
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Butterflies
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            repeat(maxOf(1, gardenState.butterflies)) {
                                Text(text = "🦋", fontSize = 36.sp)
                            }
                        }

                        // Flowers and plants
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(text = "🌱", fontSize = 44.sp)
                            if (gardenState.plantsGrown > 0) {
                                Text(text = "🌿", fontSize = 52.sp)
                            }
                            if (gardenState.flowersBloomed > 0) {
                                Text(text = "🌸", fontSize = 60.sp)
                                Text(text = "🌻", fontSize = 60.sp)
                            }
                        }
                    }

                    // Soil bed
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🌷 🌼 🌿 🌻 🌸 🌺",
                            fontSize = 24.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Garden Stats
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatPill("Seeds 🌱", "${gardenState.seedsCount}")
                    StatPill("Plants 🌿", "${gardenState.plantsGrown}")
                    StatPill("Flowers 🌸", "${gardenState.flowersBloomed}")
                }
            }
        }
    }
}

@Composable
private fun StatPill(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
