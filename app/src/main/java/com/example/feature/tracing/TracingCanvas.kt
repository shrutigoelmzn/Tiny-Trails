package com.example.feature.tracing

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.util.MathUtils
import com.example.domain.tracing.NormalizedPoint
import com.example.domain.tracing.TraceStroke
import com.example.domain.tracing.TracingFeedback
import com.example.domain.tracing.TracingState
import com.example.ui.theme.StarGold
import com.example.ui.theme.TraceActiveGlow
import com.example.ui.theme.TraceGuidelineColor
import com.example.ui.theme.TracedCompletedColor
import com.example.ui.theme.TracingStrokeColor
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// Worksheet styling colors
private val WorksheetBorderColor = Color(0xFF37474F)      // Dark slate outline border
private val WorksheetTrackColor = Color(0xFFFFFFFF)       // Empty hollow space
private val WorksheetRuleLineColor = Color(0xFFECEFF1)    // Worksheet horizontal rule lines
private val WorksheetCenterDashColor = Color(0xFF90A4AE)  // Center dashed guideline
private val WorksheetArrowColor = Color(0xFF607D8B)       // Direction arrow color
private val ActiveUserFillColor = Color(0xFF0288D1)       // Child marker blue
private val ErrorTrailColor = Color(0xFFFF7043)           // Off-corridor warning tint

@Composable
fun TracingCanvas(
    state: TracingState,
    onTouchDown: (NormalizedPoint) -> Unit,
    onTouchMove: (NormalizedPoint) -> Unit,
    onTouchUp: () -> Unit,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 48f
) {
    TracingCanvas(
        state = state,
        demoState = DemoState(),
        onTouchDown = onTouchDown,
        onTouchMove = onTouchMove,
        onTouchUp = onTouchUp,
        modifier = modifier,
        strokeWidth = strokeWidth
    )
}

@Composable
fun TracingCanvas(
    state: TracingState,
    demoState: DemoState,
    onTouchDown: (NormalizedPoint) -> Unit,
    onTouchMove: (NormalizedPoint) -> Unit,
    onTouchUp: () -> Unit,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 48f,
    traceUiState: TraceUiState = if (demoState.isPlaying) TraceUiState.Demo else if (state.isCharacterCompleted) TraceUiState.Completed else TraceUiState.ReadyToTrace
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_start")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val baseWidth = min(maxWidth.value, maxHeight.value)
        // strokeWidth increased from default 24f to 48f for preschool accessibility
        val trackWidthPx = remember(baseWidth, strokeWidth) {
            maxOf(strokeWidth * 1.55f, baseWidth * 0.23f)
        }
        val borderThicknessPx = remember(baseWidth, strokeWidth) {
            (strokeWidth * 0.16f).coerceAtLeast(7.5f)
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("tracing_canvas")
                .pointerInput(state.characterDef.id, traceUiState) {
                    // Tracing input is strictly accepted ONLY in ReadyToTrace and Tracing
                    // Input is strictly blocked in Loading, Demo, and Completed
                    if (!traceUiState.isInputAllowed) return@pointerInput

                    val canvasWidth = size.width.toFloat()
                    val canvasHeight = size.height.toFloat()
                    val boxSize = min(canvasWidth, canvasHeight) * 0.82f
                    val left = (canvasWidth - boxSize) / 2f
                    val top = (canvasHeight - boxSize) / 2f

                    detectDragGestures(
                        onDragStart = { offset ->
                            val normX = (offset.x - left) / boxSize
                            val normY = (offset.y - top) / boxSize
                            onTouchDown(NormalizedPoint(normX, normY))
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val normX = (change.position.x - left) / boxSize
                            val normY = (change.position.y - top) / boxSize
                            onTouchMove(NormalizedPoint(normX, normY))
                        },
                        onDragEnd = { onTouchUp() },
                        onDragCancel = { onTouchUp() }
                    )
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height
            val boxSize = min(canvasW, canvasH) * 0.82f
            val bounds = Rect(
                left = (canvasW - boxSize) / 2f,
                top = (canvasH - boxSize) / 2f,
                right = (canvasW + boxSize) / 2f,
                bottom = (canvasH + boxSize) / 2f
            )

            // 1. Draw preschool worksheet rule lines
            drawWorksheetRuleLines(bounds)

            // 2. Draw worksheet hollow two-border outlined character
            drawWorksheetHollowCharacter(
                state = state,
                bounds = bounds,
                trackWidthPx = trackWidthPx,
                borderThicknessPx = borderThicknessPx
            )

            // 3. Draw completed strokes filled in vibrant green
            drawCompletedStrokes(state, bounds, trackWidthPx)

            // 4. Draw user active tracing progress inside hollow track
            if (!demoState.isPlaying) {
                drawActiveUserProgress(state, bounds, trackWidthPx)
                drawUserFingerTrail(state, bounds, trackWidthPx)
            } else {
                drawDemoProgress(demoState, state.characterDef.strokes, bounds, trackWidthPx)
            }

            // 5. Draw numbered stroke badges & directional arrows
            drawStrokeBadgesAndArrows(
                state = state,
                bounds = bounds,
                trackWidthPx = trackWidthPx,
                pulseScale = pulseScale,
                demoState = demoState
            )

            // 6. Draw demo pointer hand if demo is playing
            if (demoState.isPlaying && demoState.pointerPos != null) {
                drawDemoPointer(demoState.pointerPos, bounds, trackWidthPx, pulseScale)
            }
        }

        // Calculate the percentage of the path completed by the user
        val totalStrokes = state.characterDef.strokes.size.coerceAtLeast(1)
        val completedStrokesCount = state.completedStrokes.size
        val activeStrokeProgress = if (demoState.isPlaying) {
            demoState.progress
        } else {
            state.currentStrokeProgress
        }.coerceIn(0f, 1f)

        val totalProgress = if (state.isCharacterCompleted) {
            1.0f
        } else {
            ((completedStrokesCount.toFloat() + activeStrokeProgress) / totalStrokes).coerceIn(0f, 1f)
        }

        val animatedOverallProgress by animateFloatAsState(
            targetValue = totalProgress,
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            label = "circular_tracing_progress"
        )

        val percentValue = (animatedOverallProgress * 100).toInt()

        // Child-friendly Circular Progress Bar Overlay
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.95f))
                .padding(4.dp)
                .size(58.dp)
                .testTag("circular_progress_overlay"),
            contentAlignment = Alignment.Center
        ) {
            // Background ring track
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFECEFF1),
                strokeWidth = 5.dp
            )

            // Animated circular progress indicator
            CircularProgressIndicator(
                progress = { animatedOverallProgress },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("circular_progress_indicator"),
                color = if (percentValue >= 98) Color(0xFF4CAF50) else Color(0xFF0288D1),
                strokeWidth = 5.dp,
                strokeCap = StrokeCap.Round
            )

            // Center Content: Percentage or celebratory star when complete
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (percentValue >= 98) {
                    Text(
                        text = "⭐",
                        fontSize = 18.sp
                    )
                } else {
                    Text(
                        text = "$percentValue%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0288D1)
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawWorksheetRuleLines(bounds: Rect) {
    val topY = bounds.top + bounds.height * 0.18f
    val midY = bounds.top + bounds.height * 0.50f
    val baseY = bounds.top + bounds.height * 0.82f

    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

    // Top rule line
    drawLine(
        color = WorksheetRuleLineColor,
        start = Offset(bounds.left - 16f, topY),
        end = Offset(bounds.right + 16f, topY),
        strokeWidth = 2f
    )
    // Middle dashed rule line
    drawLine(
        color = WorksheetRuleLineColor.copy(alpha = 0.85f),
        start = Offset(bounds.left - 16f, midY),
        end = Offset(bounds.right + 16f, midY),
        strokeWidth = 2f,
        pathEffect = dashEffect
    )
    // Baseline
    drawLine(
        color = WorksheetRuleLineColor,
        start = Offset(bounds.left - 16f, baseY),
        end = Offset(bounds.right + 16f, baseY),
        strokeWidth = 2f
    )
}

private fun DrawScope.drawWorksheetHollowCharacter(
    state: TracingState,
    bounds: Rect,
    trackWidthPx: Float,
    borderThicknessPx: Float
) {
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 14f), 0f)

    // For every stroke of the character:
    for (stroke in state.characterDef.strokes) {
        val path = createPathFromPoints(stroke.pathPoints, bounds)

        // 1. Outer Dark Border (outer boundary)
        drawPath(
            path = path,
            color = WorksheetBorderColor,
            style = Stroke(
                width = trackWidthPx + borderThicknessPx * 2,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // 2. Hollow Inner Space (empty white corridor between both borders)
        drawPath(
            path = path,
            color = WorksheetTrackColor,
            style = Stroke(
                width = trackWidthPx,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // 3. Center Dotted Guideline
        drawPath(
            path = path,
            color = WorksheetCenterDashColor,
            style = Stroke(
                width = 5.5f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
                pathEffect = dashEffect
            )
        )
    }
}

private fun DrawScope.drawCompletedStrokes(
    state: TracingState,
    bounds: Rect,
    trackWidthPx: Float
) {
    for (strokeId in state.completedStrokes) {
        val stroke = state.characterDef.strokes.firstOrNull { it.id == strokeId } ?: continue
        val path = createPathFromPoints(stroke.pathPoints, bounds)

        // Draw solid vibrant celebratory green fill inside the hollow track
        drawPath(
            path = path,
            color = TracedCompletedColor,
            style = Stroke(
                width = trackWidthPx * 0.78f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

private fun DrawScope.drawActiveUserProgress(
    state: TracingState,
    bounds: Rect,
    trackWidthPx: Float
) {
    val activeStroke = state.activeStroke ?: return
    if (state.highestReachedCheckpointIndex <= 0) return

    val checkpoints = activeStroke.checkpoints
    val subPoints = checkpoints.take(state.highestReachedCheckpointIndex + 1)
    if (subPoints.size < 2) return

    val subPath = createPathFromPoints(subPoints, bounds)
    drawPath(
        path = subPath,
        color = ActiveUserFillColor,
        style = Stroke(
            width = trackWidthPx * 0.74f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // Visual progress tip at the current frontier along the path
    val headPoint = toScreenOffset(checkpoints[state.highestReachedCheckpointIndex], bounds)
    drawCircle(
        color = Color.White,
        radius = trackWidthPx * 0.44f,
        center = headPoint
    )
    drawCircle(
        color = ActiveUserFillColor,
        radius = trackWidthPx * 0.34f,
        center = headPoint
    )
    drawCircle(
        color = Color(0xFFFFD54F),
        radius = trackWidthPx * 0.20f,
        center = headPoint
    )
}

private fun DrawScope.drawDemoProgress(
    demoState: DemoState,
    strokes: List<TraceStroke>,
    bounds: Rect,
    trackWidthPx: Float
) {
    // Fill strokes completed during demo
    for (i in 0 until demoState.currentStrokeIndex) {
        if (i < strokes.size) {
            val path = createPathFromPoints(strokes[i].pathPoints, bounds)
            drawPath(
                path = path,
                color = TracedCompletedColor.copy(alpha = 0.85f),
                style = Stroke(
                    width = trackWidthPx * 0.74f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }

    // Active demo stroke partial path
    val currentStroke = strokes.getOrNull(demoState.currentStrokeIndex) ?: return
    val checkpoints = currentStroke.checkpoints
    val count = (demoState.progress * checkpoints.size).toInt().coerceIn(0, checkpoints.size)
    if (count >= 2) {
        val subPath = createPathFromPoints(checkpoints.take(count), bounds)
        drawPath(
            path = subPath,
            color = ActiveUserFillColor.copy(alpha = 0.80f),
            style = Stroke(
                width = trackWidthPx * 0.74f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

private fun DrawScope.drawUserFingerTrail(
    state: TracingState,
    bounds: Rect,
    trackWidthPx: Float
) {
    if (state.activeUserPoints.size < 2) return

    val trailPath = Path()
    val first = toScreenOffset(state.activeUserPoints.first(), bounds)
    trailPath.moveTo(first.x, first.y)

    for (i in 1 until state.activeUserPoints.size) {
        val pt = toScreenOffset(state.activeUserPoints[i], bounds)
        trailPath.lineTo(pt.x, pt.y)
    }

    val trailColor = if (state.feedback == TracingFeedback.OFF_CORRIDOR) {
        ErrorTrailColor.copy(alpha = 0.65f)
    } else {
        ActiveUserFillColor.copy(alpha = 0.75f)
    }

    drawPath(
        path = trailPath,
        color = trailColor,
        style = Stroke(
            width = trackWidthPx * 0.60f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

private fun DrawScope.drawStrokeBadgesAndArrows(
    state: TracingState,
    bounds: Rect,
    trackWidthPx: Float,
    pulseScale: Float,
    demoState: DemoState
) {
    val strokes = state.characterDef.strokes

    val textPaint = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = trackWidthPx * 0.44f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }

    for (strokeIdx in strokes.indices) {
        val stroke = strokes[strokeIdx]
        val isCompleted = state.completedStrokes.contains(stroke.id)
        val isActive = strokeIdx == state.currentStrokeIndex && !isCompleted

        val checkpoints = stroke.checkpoints
        if (checkpoints.size < 2) continue

        // 1. Draw Directional Chevron Arrows along the stroke track
        drawDirectionArrows(checkpoints, bounds, trackWidthPx)

        // 2. Draw Numbered Badge at start point
        val startOffset = toScreenOffset(checkpoints.first(), bounds)
        val badgeRadius = trackWidthPx * 0.38f * (if (isActive && !demoState.isPlaying) pulseScale else 1.0f)

        // Halo for active stroke
        if (isActive && !demoState.isPlaying) {
            drawCircle(
                color = TraceActiveGlow.copy(alpha = 0.40f),
                radius = badgeRadius * 1.55f,
                center = startOffset
            )
        }

        // Outer badge border
        drawCircle(
            color = WorksheetBorderColor,
            radius = badgeRadius + 2f,
            center = startOffset
        )

        // Badge background
        val badgeBg = when {
            isCompleted -> TracedCompletedColor
            isActive -> StarGold
            else -> Color(0xFFCFD8DC)
        }
        drawCircle(
            color = badgeBg,
            radius = badgeRadius,
            center = startOffset
        )

        // Badge Text (Stroke number or checkmark)
        val badgeText = if (isCompleted) "✓" else "${strokeIdx + 1}"
        val textY = startOffset.y - ((textPaint.descent() + textPaint.ascent()) / 2f)
        drawContext.canvas.nativeCanvas.drawText(
            badgeText,
            startOffset.x,
            textY,
            textPaint
        )
    }
}

private fun DrawScope.drawDirectionArrows(
    checkpoints: List<NormalizedPoint>,
    bounds: Rect,
    trackWidthPx: Float
) {
    if (checkpoints.size < 6) return

    val arrowIndices = listOf(
        (checkpoints.size * 0.35f).toInt(),
        (checkpoints.size * 0.70f).toInt()
    )

    for (idx in arrowIndices) {
        if (idx !in 1 until checkpoints.size) continue
        val p1 = checkpoints[idx - 1]
        val p2 = checkpoints[idx]

        val screenP = toScreenOffset(p2, bounds)
        val angle = MathUtils.tangentAngle(p1, p2)
        val arrowLen = trackWidthPx * 0.28f

        // Draw chevron arrowhead pointing forward
        val finAngle1 = angle + Math.PI.toFloat() * 0.80f
        val finAngle2 = angle - Math.PI.toFloat() * 0.80f

        val fin1 = Offset(
            screenP.x + arrowLen * cos(finAngle1),
            screenP.y + arrowLen * sin(finAngle1)
        )
        val fin2 = Offset(
            screenP.x + arrowLen * cos(finAngle2),
            screenP.y + arrowLen * sin(finAngle2)
        )

        val arrowPath = Path().apply {
            moveTo(fin1.x, fin1.y)
            lineTo(screenP.x, screenP.y)
            lineTo(fin2.x, fin2.y)
        }

        drawPath(
            path = arrowPath,
            color = WorksheetArrowColor,
            style = Stroke(
                width = 6.0f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

private fun DrawScope.drawDemoPointer(
    pointerPos: NormalizedPoint,
    bounds: Rect,
    trackWidthPx: Float,
    pulseScale: Float
) {
    val offset = toScreenOffset(pointerPos, bounds)
    val pointerRadius = trackWidthPx * 0.40f * pulseScale

    // Glowing outer aura
    drawCircle(
        color = StarGold.copy(alpha = 0.50f),
        radius = pointerRadius * 1.6f,
        center = offset
    )

    // Inner bright center
    drawCircle(
        color = StarGold,
        radius = pointerRadius,
        center = offset
    )

    drawCircle(
        color = Color.White,
        radius = pointerRadius * 0.45f,
        center = offset
    )
}

private fun createPathFromPoints(points: List<NormalizedPoint>, bounds: Rect): Path {
    val path = Path()
    if (points.isEmpty()) return path

    val first = toScreenOffset(points[0], bounds)
    path.moveTo(first.x, first.y)

    for (i in 1 until points.size) {
        val pt = toScreenOffset(points[i], bounds)
        path.lineTo(pt.x, pt.y)
    }
    return path
}

private fun toScreenOffset(norm: NormalizedPoint, bounds: Rect): Offset {
    return Offset(
        x = bounds.left + norm.x * bounds.width,
        y = bounds.top + norm.y * bounds.height
    )
}
