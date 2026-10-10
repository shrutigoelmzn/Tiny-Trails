package com.example.feature.coloring

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.core.util.ShareHelper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.audio.SoundManager
import com.example.core.designsystem.components.KidsButton
import com.example.core.designsystem.components.KidsTopBar
import kotlin.math.sin
import kotlin.random.Random

enum class ColoringBrushType(val title: String, val icon: String) {
    GLITTER("Glitter", "✨"),
    RAINBOW("Rainbow", "🌈"),
    STARS("Stars", "⭐"),
    CRAYON("Crayon", "🖍️"),
    ERASER("Eraser", "🧽")
}

enum class ColoringTemplate(val title: String, val emoji: String) {
    BLANK("Blank", "🎨"),
    BUTTERFLY("Butterfly", "🦋"),
    DINOSAUR("Dino", "🦕"),
    ROCKET("Rocket", "🚀"),
    TEDDY("Teddy", "🧸")
}

data class ColoringStroke(
    val points: List<Offset>,
    val color: Color,
    val brushType: ColoringBrushType,
    val strokeWidth: Float
)

@Composable
fun ColoringStudioScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val soundManager = remember { SoundManager.getInstance(context) }

    val strokes = remember { mutableStateListOf<ColoringStroke>() }
    var currentPoints = remember { mutableStateListOf<Offset>() }

    var selectedBrush by remember { mutableStateOf(ColoringBrushType.GLITTER) }
    var selectedColor by remember { mutableStateOf(Color(0xFFFF5252)) }
    var selectedTemplate by remember { mutableStateOf(ColoringTemplate.BUTTERFLY) }
    var showShareDialog by remember { mutableStateOf(false) }

    val palette = remember {
        listOf(
            Color(0xFFFF5252), // Strawberry Red
            Color(0xFFFF7043), // Sunset Orange
            Color(0xFFFFD54F), // Sunny Yellow
            Color(0xFF4CAF50), // Mint Green
            Color(0xFF29B6F6), // Sky Blue
            Color(0xFFAB47BC), // Royal Purple
            Color(0xFFFF4081), // Bubblegum Pink
            Color(0xFF37474F)  // Charcoal Crayon
        )
    }

    // Sparkle shimmer continuous animation
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_phase"
    )

    Scaffold(
        topBar = {
            KidsTopBar(
                title = "Glitter Coloring Studio 🎨",
                onBackClick = onBack,
                onAudioClick = {
                    soundManager.explainColoringStudio()
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Template Selector Row (Butterfly, Dino, Rocket, Teddy, Blank)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Outlines:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ColoringTemplate.entries) { template ->
                        val isSelected = selectedTemplate == template
                        Card(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    soundManager.playButtonTap()
                                    selectedTemplate = template
                                }
                                .testTag("template_${template.name.lowercase()}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(template.emoji, fontSize = 16.sp)
                                Text(
                                    text = template.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Main Interactive Toddler Drawing Canvas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(8.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .border(3.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(selectedBrush, selectedColor) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        currentPoints.clear()
                                        currentPoints.add(offset)
                                        soundManager.playTouchFeedback()
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        currentPoints.add(change.position)
                                    },
                                    onDragEnd = {
                                        if (currentPoints.isNotEmpty()) {
                                            val strokeWidth = when (selectedBrush) {
                                                ColoringBrushType.ERASER -> 48f
                                                ColoringBrushType.GLITTER -> 32f
                                                ColoringBrushType.RAINBOW -> 28f
                                                ColoringBrushType.STARS -> 26f
                                                ColoringBrushType.CRAYON -> 22f
                                            }
                                            strokes.add(
                                                ColoringStroke(
                                                    points = currentPoints.toList(),
                                                    color = if (selectedBrush == ColoringBrushType.ERASER) Color.White else selectedColor,
                                                    brushType = selectedBrush,
                                                    strokeWidth = strokeWidth
                                                )
                                            )
                                            currentPoints.clear()
                                        }
                                    }
                                )
                            }
                            .testTag("coloring_canvas")
                    ) {
                        // 1. Draw Template Outlines (Light Gray guides)
                        drawTemplateOutline(selectedTemplate)

                        // 2. Draw Committed Strokes
                        for (stroke in strokes) {
                            renderColoringStroke(stroke, shimmerPhase)
                        }

                        // 3. Draw Active In-Progress Stroke
                        if (currentPoints.size > 1) {
                            val activeWidth = when (selectedBrush) {
                                ColoringBrushType.ERASER -> 48f
                                ColoringBrushType.GLITTER -> 32f
                                ColoringBrushType.RAINBOW -> 28f
                                ColoringBrushType.STARS -> 26f
                                ColoringBrushType.CRAYON -> 22f
                            }
                            renderColoringStroke(
                                ColoringStroke(
                                    points = currentPoints.toList(),
                                    color = if (selectedBrush == ColoringBrushType.ERASER) Color.White else selectedColor,
                                    brushType = selectedBrush,
                                    strokeWidth = activeWidth
                                ),
                                shimmerPhase
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Brush Selection Toolbar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ColoringBrushType.entries.forEach { brush ->
                    val isSelected = selectedBrush == brush
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(if (isSelected) 4.dp else 1.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                soundManager.playButtonTap()
                                selectedBrush = brush
                            }
                            .testTag("brush_${brush.name.lowercase()}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(brush.icon, fontSize = 20.sp)
                            Text(
                                text = brush.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Oversized Toddler Color Palette Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(palette) { color ->
                    val isSelected = selectedColor == color && selectedBrush != ColoringBrushType.ERASER
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .shadow(if (isSelected) 6.dp else 2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 4.dp else 2.dp,
                                color = if (isSelected) Color(0xFFFFB300) else Color.White,
                                shape = CircleShape
                            )
                            .clickable {
                                soundManager.playButtonTap()
                                selectedColor = color
                                if (selectedBrush == ColoringBrushType.ERASER) {
                                    selectedBrush = ColoringBrushType.GLITTER
                                }
                            }
                            .testTag("color_pill_${color.value}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Text("✨", fontSize = 16.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Canvas Action Buttons (Undo, Clear, Share)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Undo Button
                KidsButton(
                    text = "Undo ↩️",
                    onClick = {
                        if (strokes.isNotEmpty()) {
                            soundManager.playButtonTap()
                            strokes.removeAt(strokes.lastIndex)
                        }
                    },
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("coloring_action_undo")
                )

                // Clear Button
                KidsButton(
                    text = "Clear 🧹",
                    onClick = {
                        soundManager.playChime()
                        strokes.clear()
                    },
                    backgroundColor = Color(0xFFFFCDD2),
                    contentColor = Color(0xFFC62828),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("coloring_action_clear")
                )

                // Share / Save Button
                KidsButton(
                    text = "Share 🎨",
                    onClick = {
                        soundManager.playStarEarned()
                        showShareDialog = true
                    },
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("coloring_action_share")
                )
            }

            // Share Options Dialog
            if (showShareDialog) {
                AlertDialog(
                    onDismissRequest = { showShareDialog = false },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🎨", fontSize = 26.sp)
                            Text(
                                text = "Share Artwork",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Show off your colorful ${selectedTemplate.title} artwork! 🌟",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Option 1: Share to installed apps
                            KidsButton(
                                text = "🚀 Share with Apps",
                                onClick = {
                                    soundManager.playButtonTap()
                                    shareArtwork(context, selectedTemplate.title)
                                    showShareDialog = false
                                },
                                backgroundColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("coloring_share_option_apps")
                            )

                            // Option 2: Copy to Clipboard
                            KidsButton(
                                text = "📋 Copy Artwork Note",
                                onClick = {
                                    soundManager.playButtonTap()
                                    val text = "Look at my beautiful ${selectedTemplate.title} coloring artwork created on Tiny Trails! 🎨✨ Proud toddler artist creation!"
                                    ShareHelper.copyToClipboard(
                                        context = context,
                                        label = "Tiny Trails Art",
                                        text = text,
                                        showToast = true
                                    )
                                    showShareDialog = false
                                },
                                backgroundColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("coloring_share_option_copy")
                            )

                            // Option 3: Cheer artwork aloud
                            KidsButton(
                                text = "🔊 Cheer for Masterpiece!",
                                onClick = {
                                    soundManager.playCorrectChime()
                                    soundManager.speak(
                                        "Look at this super colorful ${selectedTemplate.title}! You are such a fantastic artist! High five!"
                                    )
                                    showShareDialog = false
                                },
                                backgroundColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = MaterialTheme.colorScheme.onTertiary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("coloring_share_option_cheer")
                            )
                        }
                    },
                    confirmButton = {},
                    dismissButton = {
                        TextButton(
                            onClick = { showShareDialog = false },
                            modifier = Modifier.testTag("coloring_share_dialog_close")
                        ) {
                            Text("Close", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        }
    }
}

/**
 * Renders a toddler stroke with selected brush behavior
 */
private fun DrawScope.renderColoringStroke(stroke: ColoringStroke, shimmerPhase: Float) {
    if (stroke.points.size < 2) return

    val path = Path()
    path.moveTo(stroke.points[0].x, stroke.points[0].y)
    for (i in 1 until stroke.points.size) {
        val prev = stroke.points[i - 1]
        val curr = stroke.points[i]
        val midX = (prev.x + curr.x) / 2f
        val midY = (prev.y + curr.y) / 2f
        path.quadraticTo(prev.x, prev.y, midX, midY)
    }

    when (stroke.brushType) {
        ColoringBrushType.ERASER -> {
            drawPath(
                path = path,
                color = Color.White,
                style = Stroke(width = stroke.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
        ColoringBrushType.CRAYON -> {
            drawPath(
                path = path,
                color = stroke.color.copy(alpha = 0.88f),
                style = Stroke(width = stroke.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
        ColoringBrushType.RAINBOW -> {
            val rainbowColors = listOf(
                Color(0xFFFF5252), Color(0xFFFFB74D), Color(0xFFFFF176),
                Color(0xFF81C784), Color(0xFF4FC3F7), Color(0xFFBA68C8)
            )
            drawPath(
                path = path,
                brush = Brush.linearGradient(
                    colors = rainbowColors,
                    start = stroke.points.first(),
                    end = stroke.points.last()
                ),
                style = Stroke(width = stroke.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
        ColoringBrushType.GLITTER -> {
            // Thick base stroke
            drawPath(
                path = path,
                color = stroke.color,
                style = Stroke(width = stroke.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            // Glistening sparkle dots along stroke
            val step = 4
            for (i in 0 until stroke.points.size step step) {
                val pt = stroke.points[i]
                val sparkleOffset = (sin((i + shimmerPhase * 10f).toDouble()) * 8f).toFloat()
                drawCircle(
                    color = Color.White.copy(alpha = 0.85f),
                    radius = 3.5f,
                    center = Offset(pt.x + sparkleOffset, pt.y - sparkleOffset)
                )
                drawCircle(
                    color = Color(0xFFFFEB3B).copy(alpha = 0.9f),
                    radius = 2.5f,
                    center = Offset(pt.x - sparkleOffset, pt.y + sparkleOffset)
                )
            }
        }
        ColoringBrushType.STARS -> {
            drawPath(
                path = path,
                color = stroke.color.copy(alpha = 0.6f),
                style = Stroke(width = stroke.strokeWidth * 0.7f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            // Tiny star dots
            val step = 6
            for (i in 0 until stroke.points.size step step) {
                val pt = stroke.points[i]
                drawCircle(
                    color = Color(0xFFFFD700),
                    radius = 5f,
                    center = pt
                )
                drawCircle(
                    color = Color.White,
                    radius = 2f,
                    center = pt
                )
            }
        }
    }
}

/**
 * Draws cute toddler black-line outlines for coloring templates
 */
private fun DrawScope.drawTemplateOutline(template: ColoringTemplate) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val outlineColor = Color(0xFF90A4AE)
    val stroke = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)

    when (template) {
        ColoringTemplate.BUTTERFLY -> {
            // Butterfly wings
            drawOval(color = outlineColor, topLeft = Offset(cx - 160f, cy - 140f), size = Size(150f, 130f), style = stroke)
            drawOval(color = outlineColor, topLeft = Offset(cx + 10f, cy - 140f), size = Size(150f, 130f), style = stroke)
            drawOval(color = outlineColor, topLeft = Offset(cx - 130f, cy - 10f), size = Size(120f, 100f), style = stroke)
            drawOval(color = outlineColor, topLeft = Offset(cx + 10f, cy - 10f), size = Size(120f, 100f), style = stroke)
            // Body
            drawRoundRect(color = outlineColor, topLeft = Offset(cx - 15f, cy - 110f), size = Size(30f, 180f), cornerRadius = CornerRadius(15f, 15f), style = stroke)
            // Antennae
            drawLine(color = outlineColor, start = Offset(cx - 10f, cy - 110f), end = Offset(cx - 40f, cy - 150f), strokeWidth = 5f)
            drawLine(color = outlineColor, start = Offset(cx + 10f, cy - 110f), end = Offset(cx + 40f, cy - 150f), strokeWidth = 5f)
            drawCircle(color = outlineColor, radius = 6f, center = Offset(cx - 40f, cy - 150f))
            drawCircle(color = outlineColor, radius = 6f, center = Offset(cx + 40f, cy - 150f))
        }
        ColoringTemplate.DINOSAUR -> {
            // Dino body & head
            drawRoundRect(color = outlineColor, topLeft = Offset(cx - 100f, cy - 40f), size = Size(180f, 120f), cornerRadius = CornerRadius(50f, 50f), style = stroke)
            // Neck & head
            drawLine(color = outlineColor, start = Offset(cx - 70f, cy - 30f), end = Offset(cx - 70f, cy - 130f), strokeWidth = 14f)
            drawOval(color = outlineColor, topLeft = Offset(cx - 110f, cy - 160f), size = Size(70f, 45f), style = stroke)
            // Legs
            drawRoundRect(color = outlineColor, topLeft = Offset(cx - 70f, cy + 70f), size = Size(30f, 60f), cornerRadius = CornerRadius(10f, 10f), style = stroke)
            drawRoundRect(color = outlineColor, topLeft = Offset(cx + 30f, cy + 70f), size = Size(30f, 60f), cornerRadius = CornerRadius(10f, 10f), style = stroke)
            // Tail
            drawLine(color = outlineColor, start = Offset(cx + 80f, cy + 20f), end = Offset(cx + 150f, cy - 20f), strokeWidth = 10f)
        }
        ColoringTemplate.ROCKET -> {
            // Rocket main body cone
            val rocketPath = Path().apply {
                moveTo(cx, cy - 160f)
                lineTo(cx + 65f, cy + 40f)
                lineTo(cx - 65f, cy + 40f)
                close()
            }
            drawPath(path = rocketPath, color = outlineColor, style = stroke)
            // Porthole window
            drawCircle(color = outlineColor, radius = 28f, center = Offset(cx, cy - 30f), style = stroke)
            // Fins
            val leftFin = Path().apply {
                moveTo(cx - 65f, cy)
                lineTo(cx - 115f, cy + 70f)
                lineTo(cx - 65f, cy + 40f)
                close()
            }
            drawPath(path = leftFin, color = outlineColor, style = stroke)
            val rightFin = Path().apply {
                moveTo(cx + 65f, cy)
                lineTo(cx + 115f, cy + 70f)
                lineTo(cx + 65f, cy + 40f)
                close()
            }
            drawPath(path = rightFin, color = outlineColor, style = stroke)
        }
        ColoringTemplate.TEDDY -> {
            // Teddy Bear Head
            drawCircle(color = outlineColor, radius = 75f, center = Offset(cx, cy - 60f), style = stroke)
            // Ears
            drawCircle(color = outlineColor, radius = 25f, center = Offset(cx - 65f, cy - 125f), style = stroke)
            drawCircle(color = outlineColor, radius = 25f, center = Offset(cx + 65f, cy - 125f), style = stroke)
            // Body
            drawOval(color = outlineColor, topLeft = Offset(cx - 70f, cy + 10f), size = Size(140f, 130f), style = stroke)
            // Snout
            drawOval(color = outlineColor, topLeft = Offset(cx - 30f, cy - 50f), size = Size(60f, 40f), style = stroke)
        }
        ColoringTemplate.BLANK -> {
            // Pure blank canvas
        }
    }
}

private fun shareArtwork(context: Context, templateTitle: String) {
    val shareBody = "Look at my beautiful $templateTitle coloring artwork created on Tiny Trails! 🎨✨ Proud toddler artist creation!"
    ShareHelper.shareText(
        context = context,
        subject = "Tiny Trails Masterpiece: $templateTitle 🎨",
        body = shareBody,
        chooserTitle = "Share Tiny Trails Artwork"
    )
}
