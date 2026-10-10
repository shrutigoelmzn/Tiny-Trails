package com.example.feature.certificate

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.SoundManager
import com.example.core.designsystem.components.KidsButton
import com.example.core.designsystem.components.KidsTopBar
import com.example.core.designsystem.components.ParticleExplosionEffect
import com.example.data.content.CharacterRepository
import com.example.data.repository.ProgressRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CertificateScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val progressRepo = remember { ProgressRepository.getInstance(context) }
    val soundManager = remember { SoundManager.getInstance(context) }
    val progressMap by progressRepo.progressFlow.collectAsState()

    var selectedCertId by remember { mutableStateOf(CertificateDefinitions.ALL_CERTIFICATES.first().id) }
    var childName by remember { mutableStateOf("Little Explorer") }
    var showEditNameDialog by remember { mutableStateOf(false) }
    var celebrationTriggerCount by remember { mutableIntStateOf(0) }

    val currentDate = remember {
        SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date())
    }

    // Progress stats
    val allChars = remember { CharacterRepository.allCharacters }
    val numbersList = remember { CharacterRepository.numbers }
    val lettersList = remember { CharacterRepository.uppercase + CharacterRepository.lowercase }
    val shapesList = remember { CharacterRepository.shapes }

    val totalCompleted = remember(progressMap) {
        allChars.count { progressMap[it.id]?.completed == true }
    }
    val numbersCompleted = remember(progressMap) {
        numbersList.count { progressMap[it.id]?.completed == true }
    }
    val lettersCompleted = remember(progressMap) {
        lettersList.count { progressMap[it.id]?.completed == true }
    }
    val shapesCompleted = remember(progressMap) {
        shapesList.count { progressMap[it.id]?.completed == true }
    }
    val totalStars = remember(progressMap) {
        allChars.sumOf { progressMap[it.id]?.stars ?: 0 }
    }

    val selectedCert = remember(selectedCertId) {
        CertificateDefinitions.ALL_CERTIFICATES.first { it.id == selectedCertId }
    }

    val isUnlocked = remember(selectedCert, totalCompleted, numbersCompleted, lettersCompleted, shapesCompleted, totalStars) {
        selectedCert.checkUnlocked(totalCompleted, numbersCompleted, lettersCompleted, shapesCompleted, totalStars)
    }

    Scaffold(
        topBar = {
            KidsTopBar(
                title = "Milestone Certificates 🏆",
                onBackClick = onBack,
                onAudioClick = {
                    soundManager.explainCertificate(childName)
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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Certificate Carousel / Selector Tabs
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(CertificateDefinitions.ALL_CERTIFICATES) { cert ->
                        val unlocked = cert.checkUnlocked(totalCompleted, numbersCompleted, lettersCompleted, shapesCompleted, totalStars)
                        val isSelected = cert.id == selectedCertId

                        Card(
                            modifier = Modifier
                                .width(135.dp)
                                .shadow(if (isSelected) 6.dp else 2.dp, RoundedCornerShape(18.dp))
                                .clip(RoundedCornerShape(18.dp))
                                .clickable {
                                    soundManager.playButtonTap()
                                    selectedCertId = cert.id
                                }
                                .testTag("cert_tab_${cert.id}"),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (unlocked) cert.badgeEmoji else "🔒",
                                    fontSize = 28.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = cert.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Editable Recipient Name Field
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Learner's Name:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = childName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        KidsButton(
                            text = "Edit ✏️",
                            onClick = { showEditNameDialog = !showEditNameDialog },
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.testTag("cert_edit_name_button")
                        )
                    }
                }

                if (showEditNameDialog) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = childName,
                        onValueChange = { childName = it },
                        label = { Text("Enter Child's Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cert_name_input")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // The Diploma Certificate Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(10.dp, RoundedCornerShape(26.dp))
                        .clip(RoundedCornerShape(26.dp))
                        .border(4.dp, Color(0xFFFFD700), RoundedCornerShape(26.dp))
                        .testTag("diploma_certificate_card"),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF5))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .border(2.dp, Color(0xFFE0C068).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Decorative Header Stars
                        Text(
                            text = "★ ★ ★ ★ ★",
                            fontSize = 14.sp,
                            color = Color(0xFFFFB300),
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "CERTIFICATE OF ACHIEVEMENT",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF795548),
                            letterSpacing = 1.5.sp
                        )

                        Text(
                            text = selectedCert.subtitle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF8D6E63)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "THIS IS PROUDLY PRESENTED TO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9E9E9E),
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Recipient Name in Elegant Display Font
                        Text(
                            text = childName,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1B5E20),
                            textAlign = TextAlign.Center
                        )

                        Box(
                            modifier = Modifier
                                .width(180.dp)
                                .height(2.dp)
                                .background(Color(0xFFFFD54F))
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Honor Title
                        Text(
                            text = selectedCert.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0D47A1),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = selectedCert.description,
                            fontSize = 12.sp,
                            color = Color(0xFF424242),
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Gold Seal Ribbon & Date Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Date Column
                            Column(horizontalAlignment = Alignment.Start) {
                                Text(
                                    text = "Date Issued:",
                                    fontSize = 10.sp,
                                    color = Color(0xFF757575)
                                )
                                Text(
                                    text = currentDate,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF212121)
                                )
                            }

                            // 3D Official Gold Ribbon Seal
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .shadow(4.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(Color(0xFFFFE082), Color(0xFFFFB300))
                                        )
                                    )
                                    .border(2.dp, Color(0xFFFF8F00), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(selectedCert.badgeEmoji, fontSize = 22.sp)
                                    Text(
                                        text = "AWARD",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF4E342E)
                                    )
                                }
                            }

                            // Academy Signature Column
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Certified By:",
                                    fontSize = 10.sp,
                                    color = Color(0xFF757575)
                                )
                                Text(
                                    text = "Tiny Trails Academy ✨",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1565C0)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Locked Status Notice or Action Buttons
                if (!isUnlocked) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("🔒", fontSize = 24.sp)
                            Column {
                                Text(
                                    text = "Milestone In Progress!",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    text = selectedCert.requirementDescription,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Celebrate Fanfare
                        KidsButton(
                            text = "Celebrate! 🎉",
                            onClick = {
                                celebrationTriggerCount++
                                soundManager.playCharacterTraceCompletion("🌟", stars = 3, isHindi = false)
                            },
                            backgroundColor = Color(0xFFFF7043),
                            contentColor = Color.White,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("cert_action_celebrate")
                        )

                        // Share & Print Certificate
                        KidsButton(
                            text = "Share / Print 🖨️",
                            onClick = {
                                soundManager.playStarEarned()
                                shareCertificate(context, childName, selectedCert, currentDate)
                            },
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("cert_action_share")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Celebratory Particle Explosion
            if (celebrationTriggerCount > 0) {
                ParticleExplosionEffect(
                    modifier = Modifier.fillMaxSize(),
                    isTriggered = true,
                    triggerKey = celebrationTriggerCount
                )
            }
        }
    }
}

private fun shareCertificate(
    context: Context,
    recipient: String,
    cert: MilestoneCertificate,
    date: String
) {
    val shareBody = """
        🎓 CERTIFICATE OF ACHIEVEMENT 🎓
        Presented to: $recipient
        Award: ${cert.title} ${cert.badgeEmoji}
        $date
        
        "${cert.description}"
        
        Issued by Tiny Trails Early Learning Academy ✨
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Milestone Certificate: ${cert.title}")
        putExtra(Intent.EXTRA_TEXT, shareBody)
    }
    context.startActivity(Intent.createChooser(intent, "Share / Print Certificate"))
}
