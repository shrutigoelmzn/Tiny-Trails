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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.cos
import kotlin.math.hypot
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
    TEDDY("Teddy", "🧸"),
    STAR("Star", "⭐"),
    HEART("Heart", "💖"),
    FLOWER("Flower", "🌸"),
    FISH("Fish", "🐠")
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
    val undoStack = remember { mutableStateListOf<List<ColoringStroke>>() }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var eraserPos by remember { mutableStateOf<Offset?>(null) }
    var lastEraserPos by remember { mutableStateOf<Offset?>(null) }

    fun pushUndo() {
        if (undoStack.size >= 20) {
            undoStack.removeAt(0)
        }
        undoStack.add(strokes.toList())
    }

    fun eraseColorNear(center: Offset, radius: Float = 48f) {
        if (strokes.isEmpty()) return
        val radiusSq = radius * radius
        val updatedStrokes = mutableListOf<ColoringStroke>()
        var didModify = false

        for (stroke in strokes) {
            val points = stroke.points
            if (points.isEmpty()) continue

            var hitAny = false
            val newSegments = mutableListOf<MutableList<Offset>>()
            var curSegment = mutableListOf<Offset>()

            for (pt in points) {
                val dx = pt.x - center.x
                val dy = pt.y - center.y
                val d2 = dx * dx + dy * dy
                if (d2 <= radiusSq) {
                    hitAny = true
                    didModify = true
                    if (curSegment.size >= 2) {
                        newSegments.add(curSegment)
                        curSegment = mutableListOf()
                    } else {
                        curSegment.clear()
                    }
                } else {
                    curSegment.add(pt)
                }
            }
            if (curSegment.size >= 2) {
                newSegments.add(curSegment)
            }

            if (hitAny) {
                for (seg in newSegments) {
                    updatedStrokes.add(stroke.copy(points = seg))
                }
            } else {
                updatedStrokes.add(stroke)
            }
        }

        if (didModify) {
            strokes.clear()
            strokes.addAll(updatedStrokes)
        }
    }

    fun interpolateAndErase(from: Offset, to: Offset, radius: Float = 48f) {
        val dx = to.x - from.x
        val dy = to.y - from.y
        val dist = hypot(dx.toDouble(), dy.toDouble()).toFloat()
        val steps = (dist / 14f).toInt().coerceAtLeast(1)
        for (i in 0..steps) {
            val t = i / steps.toFloat()
            val interpolated = Offset(from.x + dx * t, from.y + dy * t)
            eraseColorNear(interpolated, radius)
        }
    }

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
                            .onSizeChanged { canvasSize = it }
                            .pointerInput(selectedBrush, selectedColor) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        soundManager.playTouchFeedback()
                                        if (selectedBrush == ColoringBrushType.ERASER) {
                                            pushUndo()
                                            eraserPos = offset
                                            lastEraserPos = offset
                                            eraseColorNear(offset)
                                        } else {
                                            pushUndo()
                                            currentPoints.clear()
                                            currentPoints.add(offset)
                                        }
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        if (selectedBrush == ColoringBrushType.ERASER) {
                                            eraserPos = change.position
                                            val prev = lastEraserPos ?: change.position
                                            interpolateAndErase(prev, change.position)
                                            lastEraserPos = change.position
                                        } else {
                                            currentPoints.add(change.position)
                                        }
                                    },
                                    onDragEnd = {
                                        if (selectedBrush == ColoringBrushType.ERASER) {
                                            eraserPos = null
                                            lastEraserPos = null
                                        } else {
                                            if (currentPoints.isNotEmpty()) {
                                                val strokeWidth = when (selectedBrush) {
                                                    ColoringBrushType.GLITTER -> 36f
                                                    ColoringBrushType.RAINBOW -> 32f
                                                    ColoringBrushType.STARS -> 30f
                                                    ColoringBrushType.CRAYON -> 28f
                                                    ColoringBrushType.ERASER -> 0f
                                                }
                                                strokes.add(
                                                    ColoringStroke(
                                                        points = currentPoints.toList(),
                                                        color = selectedColor,
                                                        brushType = selectedBrush,
                                                        strokeWidth = strokeWidth
                                                    )
                                                )
                                                currentPoints.clear()
                                            }
                                        }
                                    },
                                    onDragCancel = {
                                        eraserPos = null
                                        lastEraserPos = null
                                        currentPoints.clear()
                                    }
                                )
                            }
                            .testTag("coloring_canvas")
                    ) {
                        // 1. Draw Template Outlines initially as background guide
                        drawTemplateOutline(selectedTemplate)

                        // 2. Draw Committed Coloring Strokes (only colored strokes)
                        for (stroke in strokes) {
                            renderColoringStroke(stroke, shimmerPhase)
                        }

                        // 3. Draw Active In-Progress Color Stroke
                        if (selectedBrush != ColoringBrushType.ERASER && currentPoints.size > 1) {
                            val activeWidth = when (selectedBrush) {
                                ColoringBrushType.GLITTER -> 36f
                                ColoringBrushType.RAINBOW -> 32f
                                ColoringBrushType.STARS -> 30f
                                ColoringBrushType.CRAYON -> 28f
                                ColoringBrushType.ERASER -> 0f
                            }
                            renderColoringStroke(
                                ColoringStroke(
                                    points = currentPoints.toList(),
                                    color = selectedColor,
                                    brushType = selectedBrush,
                                    strokeWidth = activeWidth
                                ),
                                shimmerPhase
                            )
                        }

                        // 4. Always draw crisp bold template outlines on top so outlines are never obscured
                        if (selectedTemplate != ColoringTemplate.BLANK) {
                            drawTemplateOutline(selectedTemplate)
                        }

                        // 5. Visual Toddler Eraser ring cursor when actively erasing
                        if (selectedBrush == ColoringBrushType.ERASER && eraserPos != null) {
                            drawCircle(
                                color = Color(0xFF64B5F6).copy(alpha = 0.35f),
                                radius = 48f,
                                center = eraserPos!!
                            )
                            drawCircle(
                                color = Color(0xFF1E88E5),
                                radius = 48f,
                                center = eraserPos!!,
                                style = Stroke(width = 3.5f)
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
                        if (undoStack.isNotEmpty()) {
                            soundManager.playButtonTap()
                            strokes.clear()
                            strokes.addAll(undoStack.removeAt(undoStack.lastIndex))
                        } else if (strokes.isNotEmpty()) {
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
                        if (strokes.isNotEmpty()) {
                            pushUndo()
                            soundManager.playChime()
                            strokes.clear()
                        }
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
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Show off your colorful ${selectedTemplate.title} artwork as a picture! 🌟",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Option 1: Share Colored Shape Image (Professional high-quality picture)
                            KidsButton(
                                text = "🖼️ Share Colored Shape (Picture)",
                                onClick = {
                                    soundManager.playStarEarned()
                                    shareArtwork(
                                        context = context,
                                        template = selectedTemplate,
                                        strokes = strokes.toList(),
                                        canvasWidth = canvasSize.width,
                                        canvasHeight = canvasSize.height
                                    )
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
            // Eraser acts by removing and carving color stroke points directly, preserving shapes and canvas
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
 * Draws bold, cute toddler outlines for coloring templates.
 * Shapes are scaled 2x with bold 14f borders (2-3x thickness) for easy, beautiful toddler coloring.
 */
private fun DrawScope.drawTemplateOutline(template: ColoringTemplate) {
    if (template == ColoringTemplate.BLANK) return

    val cx = size.width / 2f
    val cy = size.height / 2f
    // High-contrast, friendly coloring-book charcoal outline
    val outlineColor = Color(0xFF2C3E50)
    // 14f border stroke: 2.8x thicker than original 5f for bold, clear boundaries
    val stroke = Stroke(width = 14f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val detailStroke = Stroke(width = 9f, cap = StrokeCap.Round, join = StrokeJoin.Round)

    when (template) {
        ColoringTemplate.BUTTERFLY -> {
            // 2x Butterfly Wings (Upper & Lower)
            drawOval(color = outlineColor, topLeft = Offset(cx - 310f, cy - 250f), size = Size(290f, 240f), style = stroke)
            drawOval(color = outlineColor, topLeft = Offset(cx + 20f, cy - 250f), size = Size(290f, 240f), style = stroke)
            drawOval(color = outlineColor, topLeft = Offset(cx - 240f, cy - 10f), size = Size(230f, 190f), style = stroke)
            drawOval(color = outlineColor, topLeft = Offset(cx + 10f, cy - 10f), size = Size(230f, 190f), style = stroke)

            // Wing decorative circles for toddler coloring
            drawCircle(color = outlineColor, radius = 32f, center = Offset(cx - 165f, cy - 130f), style = detailStroke)
            drawCircle(color = outlineColor, radius = 32f, center = Offset(cx + 165f, cy - 130f), style = detailStroke)
            drawCircle(color = outlineColor, radius = 24f, center = Offset(cx - 125f, cy + 85f), style = detailStroke)
            drawCircle(color = outlineColor, radius = 24f, center = Offset(cx + 125f, cy + 85f), style = detailStroke)

            // 2x Butterfly Body
            drawRoundRect(color = outlineColor, topLeft = Offset(cx - 28f, cy - 210f), size = Size(56f, 350f), cornerRadius = CornerRadius(28f, 28f), style = stroke)

            // Cute facial features
            drawCircle(color = outlineColor, radius = 7f, center = Offset(cx - 12f, cy - 160f))
            drawCircle(color = outlineColor, radius = 7f, center = Offset(cx + 12f, cy - 160f))
            drawArc(color = outlineColor, startAngle = 10f, sweepAngle = 160f, useCenter = false, topLeft = Offset(cx - 12f, cy - 148f), size = Size(24f, 16f), style = detailStroke)

            // 2x Antennae
            drawLine(color = outlineColor, start = Offset(cx - 14f, cy - 210f), end = Offset(cx - 75f, cy - 295f), strokeWidth = 12f, cap = StrokeCap.Round)
            drawLine(color = outlineColor, start = Offset(cx + 14f, cy - 210f), end = Offset(cx + 75f, cy - 295f), strokeWidth = 12f, cap = StrokeCap.Round)
            drawCircle(color = outlineColor, radius = 14f, center = Offset(cx - 75f, cy - 295f))
            drawCircle(color = outlineColor, radius = 14f, center = Offset(cx + 75f, cy - 295f))
        }

        ColoringTemplate.DINOSAUR -> {
            // 2x Dino Body
            drawRoundRect(color = outlineColor, topLeft = Offset(cx - 200f, cy - 70f), size = Size(350f, 230f), cornerRadius = CornerRadius(95f, 95f), style = stroke)

            // 2x Dino Neck
            drawLine(color = outlineColor, start = Offset(cx - 140f, cy - 60f), end = Offset(cx - 140f, cy - 240f), strokeWidth = 24f, cap = StrokeCap.Round)
            drawLine(color = outlineColor, start = Offset(cx - 85f, cy - 60f), end = Offset(cx - 85f, cy - 240f), strokeWidth = 24f, cap = StrokeCap.Round)

            // 2x Dino Head
            drawOval(color = outlineColor, topLeft = Offset(cx - 220f, cy - 300f), size = Size(140f, 90f), style = stroke)
            drawCircle(color = outlineColor, radius = 10f, center = Offset(cx - 170f, cy - 265f))
            drawArc(color = outlineColor, startAngle = 10f, sweepAngle = 150f, useCenter = false, topLeft = Offset(cx - 205f, cy - 250f), size = Size(36f, 22f), style = detailStroke)

            // 2x Legs
            drawRoundRect(color = outlineColor, topLeft = Offset(cx - 140f, cy + 145f), size = Size(58f, 110f), cornerRadius = CornerRadius(18f, 18f), style = stroke)
            drawRoundRect(color = outlineColor, topLeft = Offset(cx + 60f, cy + 145f), size = Size(58f, 110f), cornerRadius = CornerRadius(18f, 18f), style = stroke)

            // 2x Tail
            val tailPath = Path().apply {
                moveTo(cx + 140f, cy + 20f)
                quadraticTo(cx + 250f, cy - 20f, cx + 290f, cy - 40f)
                quadraticTo(cx + 230f, cy + 80f, cx + 130f, cy + 110f)
            }
            drawPath(path = tailPath, color = outlineColor, style = stroke)

            // Cute Spikes along back
            val spikes = Path().apply {
                moveTo(cx - 50f, cy - 70f); lineTo(cx - 30f, cy - 125f); lineTo(cx - 10f, cy - 70f)
                moveTo(cx, cy - 70f); lineTo(cx + 20f, cy - 125f); lineTo(cx + 40f, cy - 70f)
                moveTo(cx + 50f, cy - 70f); lineTo(cx + 70f, cy - 125f); lineTo(cx + 90f, cy - 70f)
            }
            drawPath(path = spikes, color = outlineColor, style = stroke)
        }

        ColoringTemplate.ROCKET -> {
            // 2x Rocket Main Body Cone
            val rocketPath = Path().apply {
                moveTo(cx, cy - 320f)
                lineTo(cx + 130f, cy + 80f)
                lineTo(cx - 130f, cy + 80f)
                close()
            }
            drawPath(path = rocketPath, color = outlineColor, style = stroke)

            // Nose cone stripe
            drawLine(color = outlineColor, start = Offset(cx - 65f, cy - 120f), end = Offset(cx + 65f, cy - 120f), strokeWidth = 14f, cap = StrokeCap.Round)

            // 2x Porthole Window
            drawCircle(color = outlineColor, radius = 56f, center = Offset(cx, cy - 35f), style = stroke)
            drawCircle(color = outlineColor, radius = 38f, center = Offset(cx, cy - 35f), style = detailStroke)

            // 2x Rocket Fins
            val leftFin = Path().apply {
                moveTo(cx - 130f, cy)
                lineTo(cx - 230f, cy + 140f)
                lineTo(cx - 130f, cy + 80f)
                close()
            }
            drawPath(path = leftFin, color = outlineColor, style = stroke)

            val rightFin = Path().apply {
                moveTo(cx + 130f, cy)
                lineTo(cx + 230f, cy + 140f)
                lineTo(cx + 130f, cy + 80f)
                close()
            }
            drawPath(path = rightFin, color = outlineColor, style = stroke)

            // 2x Thruster Flames
            val flames = Path().apply {
                moveTo(cx - 70f, cy + 80f)
                lineTo(cx - 40f, cy + 175f)
                lineTo(cx, cy + 115f)
                lineTo(cx + 40f, cy + 175f)
                lineTo(cx + 70f, cy + 80f)
                close()
            }
            drawPath(path = flames, color = outlineColor, style = stroke)
        }

        ColoringTemplate.TEDDY -> {
            // 2x Teddy Bear Head
            drawCircle(color = outlineColor, radius = 140f, center = Offset(cx, cy - 100f), style = stroke)

            // 2x Ears
            drawCircle(color = outlineColor, radius = 50f, center = Offset(cx - 120f, cy - 220f), style = stroke)
            drawCircle(color = outlineColor, radius = 26f, center = Offset(cx - 120f, cy - 220f), style = detailStroke)
            drawCircle(color = outlineColor, radius = 50f, center = Offset(cx + 120f, cy - 220f), style = stroke)
            drawCircle(color = outlineColor, radius = 26f, center = Offset(cx + 120f, cy - 220f), style = detailStroke)

            // 2x Body
            drawOval(color = outlineColor, topLeft = Offset(cx - 135f, cy + 25f), size = Size(270f, 250f), style = stroke)

            // Belly Patch
            drawOval(color = outlineColor, topLeft = Offset(cx - 85f, cy + 75f), size = Size(170f, 150f), style = detailStroke)

            // 2x Snout & Cute Face
            drawOval(color = outlineColor, topLeft = Offset(cx - 55f, cy - 80f), size = Size(110f, 75f), style = stroke)
            drawOval(color = outlineColor, topLeft = Offset(cx - 24f, cy - 85f), size = Size(48f, 32f))
            drawArc(color = outlineColor, startAngle = 10f, sweepAngle = 160f, useCenter = false, topLeft = Offset(cx - 30f, cy - 55f), size = Size(60f, 35f), style = detailStroke)
            drawCircle(color = outlineColor, radius = 12f, center = Offset(cx - 55f, cy - 135f))
            drawCircle(color = outlineColor, radius = 12f, center = Offset(cx + 55f, cy - 135f))
        }

        ColoringTemplate.STAR -> {
            // 2x Toddler Friendly Star
            val starPath = Path()
            for (i in 0 until 10) {
                val angle = (i * 36 - 90) * (Math.PI / 180.0)
                val r = if (i % 2 == 0) 240f else 115f
                val px = cx + (r * cos(angle)).toFloat()
                val py = cy + (r * sin(angle)).toFloat()
                if (i == 0) starPath.moveTo(px, py) else starPath.lineTo(px, py)
            }
            starPath.close()
            drawPath(path = starPath, color = outlineColor, style = stroke)

            // Smiling Face in Star Center
            drawCircle(color = outlineColor, radius = 12f, center = Offset(cx - 45f, cy - 20f))
            drawCircle(color = outlineColor, radius = 12f, center = Offset(cx + 45f, cy - 20f))
            drawArc(color = outlineColor, startAngle = 10f, sweepAngle = 160f, useCenter = false, topLeft = Offset(cx - 35f, cy - 10f), size = Size(70f, 45f), style = detailStroke)
            drawCircle(color = outlineColor, radius = 14f, center = Offset(cx - 70f, cy + 10f), style = detailStroke)
            drawCircle(color = outlineColor, radius = 14f, center = Offset(cx + 70f, cy + 10f), style = detailStroke)
        }

        ColoringTemplate.HEART -> {
            // 2x Giant Heart
            val heartPath = Path().apply {
                moveTo(cx, cy + 220f)
                cubicTo(cx - 260f, cy + 80f, cx - 280f, cy - 140f, cx - 130f, cy - 190f)
                cubicTo(cx - 40f, cy - 220f, cx, cy - 110f, cx, cy - 90f)
                cubicTo(cx, cy - 110f, cx + 40f, cy - 220f, cx + 130f, cy - 190f)
                cubicTo(cx + 280f, cy - 140f, cx + 260f, cy + 80f, cx, cy + 220f)
                close()
            }
            drawPath(path = heartPath, color = outlineColor, style = stroke)

            // Cute Happy Eyes & Smile
            drawCircle(color = outlineColor, radius = 12f, center = Offset(cx - 50f, cy - 30f))
            drawCircle(color = outlineColor, radius = 12f, center = Offset(cx + 50f, cy - 30f))
            drawArc(color = outlineColor, startAngle = 10f, sweepAngle = 160f, useCenter = false, topLeft = Offset(cx - 35f, cy - 15f), size = Size(70f, 45f), style = detailStroke)
        }

        ColoringTemplate.FLOWER -> {
            // 2x Giant Flower Center
            drawCircle(color = outlineColor, radius = 80f, center = Offset(cx, cy - 40f), style = stroke)

            // 6 Big Rounded Petals
            for (i in 0 until 6) {
                val angle = (i * 60) * (Math.PI / 180.0)
                val petalX = cx + (145f * cos(angle)).toFloat()
                val petalY = (cy - 40f) + (145f * sin(angle)).toFloat()
                drawCircle(color = outlineColor, radius = 75f, center = Offset(petalX, petalY), style = stroke)
            }

            // Smiling Face in Flower Center
            drawCircle(color = outlineColor, radius = 10f, center = Offset(cx - 30f, cy - 55f))
            drawCircle(color = outlineColor, radius = 10f, center = Offset(cx + 30f, cy - 55f))
            drawArc(color = outlineColor, startAngle = 15f, sweepAngle = 150f, useCenter = false, topLeft = Offset(cx - 25f, cy - 45f), size = Size(50f, 35f), style = detailStroke)

            // Stem & Big Leaves
            drawLine(color = outlineColor, start = Offset(cx, cy + 185f), end = Offset(cx, cy + 285f), strokeWidth = 18f, cap = StrokeCap.Round)
            val leftLeaf = Path().apply {
                moveTo(cx, cy + 235f)
                quadraticTo(cx - 90f, cy + 205f, cx - 110f, cy + 245f)
                quadraticTo(cx - 60f, cy + 275f, cx, cy + 265f)
                close()
            }
            drawPath(path = leftLeaf, color = outlineColor, style = stroke)
        }

        ColoringTemplate.FISH -> {
            // 2x Giant Ocean Fish Body
            drawOval(color = outlineColor, topLeft = Offset(cx - 210f, cy - 120f), size = Size(340f, 240f), style = stroke)

            // Tail Fin
            val tail = Path().apply {
                moveTo(cx + 120f, cy)
                lineTo(cx + 250f, cy - 130f)
                lineTo(cx + 200f, cy)
                lineTo(cx + 250f, cy + 130f)
                close()
            }
            drawPath(path = tail, color = outlineColor, style = stroke)

            // Dorsal Fin
            val topFin = Path().apply {
                moveTo(cx - 70f, cy - 110f)
                quadraticTo(cx, cy - 210f, cx + 70f, cy - 100f)
                close()
            }
            drawPath(path = topFin, color = outlineColor, style = stroke)

            // Eye & Smile
            drawCircle(color = outlineColor, radius = 24f, center = Offset(cx - 120f, cy - 35f), style = stroke)
            drawCircle(color = outlineColor, radius = 12f, center = Offset(cx - 125f, cy - 35f))
            drawArc(color = outlineColor, startAngle = 20f, sweepAngle = 140f, useCenter = false, topLeft = Offset(cx - 180f, cy + 10f), size = Size(40f, 30f), style = detailStroke)

            // Gill Arc
            drawArc(color = outlineColor, startAngle = 270f, sweepAngle = 180f, useCenter = false, topLeft = Offset(cx - 70f, cy - 100f), size = Size(50f, 200f), style = detailStroke)
        }

        ColoringTemplate.BLANK -> {}
    }
}

private fun createArtworkBitmap(
    template: ColoringTemplate,
    strokes: List<ColoringStroke>,
    canvasWidth: Int,
    canvasHeight: Int
): Bitmap {
    val width = if (canvasWidth > 0) canvasWidth else 1080
    val height = if (canvasHeight > 0) canvasHeight else 1080
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val composeCanvas = androidx.compose.ui.graphics.Canvas(bitmap.asImageBitmap())
    val drawScope = CanvasDrawScope()

    drawScope.draw(
        density = Density(2f),
        layoutDirection = LayoutDirection.Ltr,
        canvas = composeCanvas,
        size = Size(width.toFloat(), height.toFloat())
    ) {
        // 1. Draw solid clean white paper background
        drawRect(Color.White)

        // 2. Draw template outline background
        drawTemplateOutline(template)

        // 3. Draw committed coloring strokes
        for (stroke in strokes) {
            renderColoringStroke(stroke, shimmerPhase = 0f)
        }

        // 4. Draw bold crisp template outline on top so borders and shape stay sharp
        if (template != ColoringTemplate.BLANK) {
            drawTemplateOutline(template)
        }
    }
    return bitmap
}

private fun shareArtwork(
    context: Context,
    template: ColoringTemplate,
    strokes: List<ColoringStroke>,
    canvasWidth: Int,
    canvasHeight: Int
) {
    try {
        val bitmap = createArtworkBitmap(template, strokes, canvasWidth, canvasHeight)
        val caption = "Look at my beautiful ${template.title} coloring artwork created on Tiny Trails! 🎨✨ Proud toddler artist creation!"
        ShareHelper.shareImage(
            context = context,
            bitmap = bitmap,
            subject = "Tiny Trails Masterpiece: ${template.title} 🎨",
            captionText = caption,
            chooserTitle = "Share Tiny Trails Artwork"
        )
    } catch (e: Exception) {
        val fallbackText = "Look at my beautiful ${template.title} coloring artwork created on Tiny Trails! 🎨✨ Proud toddler artist creation!"
        ShareHelper.shareText(
            context = context,
            subject = "Tiny Trails Masterpiece: ${template.title} 🎨",
            body = fallbackText,
            chooserTitle = "Share Tiny Trails Artwork"
        )
    }
}
