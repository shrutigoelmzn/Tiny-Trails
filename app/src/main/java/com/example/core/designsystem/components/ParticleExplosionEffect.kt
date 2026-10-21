package com.example.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Types of particles displayed in the explosion effect.
 */
enum class ExplosionParticleType {
    STAR,
    SPARKLE,
    CONFETTI_RECT,
    CIRCLE,
    GLOW_ORB
}

/**
 * State representing a single particle in the explosion.
 */
data class ExplosionParticle(
    val id: Int,
    val originX: Float,
    val originY: Float,
    val angleRad: Float,
    val maxDistance: Float,
    val gravityBias: Float,
    val speedCurve: Float,
    val size: Float,
    val color: Color,
    val type: ExplosionParticleType,
    val initialRotation: Float,
    val rotationDelta: Float,
    val flipSpeed: Float,
    val delayRatio: Float,
    val decayRatio: Float
)

/**
 * A mini burst triggered by interactive touch taps during celebration.
 */
private data class TapBurst(
    val id: Long,
    val center: Offset,
    val particles: List<ExplosionParticle>,
    val progress: Animatable<Float, *>
)

private val ExplosionPalette = listOf(
    Color(0xFFFFD700), // Sparkling Gold
    Color(0xFFFF4081), // Joyful Fuchsia
    Color(0xFF00E5FF), // Electric Cyan
    Color(0xFF76FF03), // Lime Mint
    Color(0xFFE040FB), // Magic Purple
    Color(0xFFFF9100), // Sunshine Orange
    Color(0xFF2979FF), // Bright Royal Blue
    Color(0xFFFF1744), // Vivid Strawberry Red
    Color(0xFFFFEE58), // Bright Yellow
    Color(0xFF69F0AE)  // Spring Turquoise
)

/**
 * Particle explosion effect built using Jetpack Compose Animation.
 *
 * Triggers an energetic radial explosion of golden stars, sparkling diamonds,
 * spinning confetti ribbons, and shockwave halos when a character tracing session
 * is completed. Also supports re-triggering via [triggerKey] and interactive touch taps.
 */
@Composable
fun ParticleExplosionEffect(
    modifier: Modifier = Modifier,
    isTriggered: Boolean = true,
    triggerKey: Any? = Unit,
    particleCount: Int = 130,
    epicenter: Offset? = null,
    showShockwave: Boolean = true,
    enableTapBurst: Boolean = true,
    onExplosionComplete: (() -> Unit)? = null
) {
    if (!isTriggered) return

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("particle_explosion_container")
    ) {
        val width = constraints.maxWidth.toFloat().coerceAtLeast(300f)
        val height = constraints.maxHeight.toFloat().coerceAtLeast(400f)

        val centerOffset = epicenter ?: Offset(width * 0.5f, height * 0.42f)

        // Main explosion animation progress (0f -> 1f) driven by Compose Animatable
        val explosionProgress = remember { Animatable(0f) }
        val shockwaveProgress = remember { Animatable(0f) }

        // Continuous ambient floating confetti shimmer after the initial explosion
        val ambientTransition = rememberInfiniteTransition(label = "ambient_celebration_shimmer")
        val ambientTime by ambientTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3500, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "ambient_time"
        )

        // Explosion particles list generated when triggerKey changes
        val mainParticles = remember(triggerKey, width, height) {
            generateExplosionParticles(
                count = particleCount,
                origin = centerOffset,
                screenWidth = width,
                screenHeight = height
            )
        }

        val scope = rememberCoroutineScope()

        // Active tap bursts list for interactive screen touches
        val tapBursts = remember { mutableStateListOf<TapBurst>() }

        // Trigger Compose Animation when isTriggered or triggerKey changes
        LaunchedEffect(isTriggered, triggerKey) {
            if (isTriggered) {
                launch {
                    shockwaveProgress.snapTo(0f)
                    shockwaveProgress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing)
                    )
                }

                launch {
                    explosionProgress.snapTo(0f)
                    explosionProgress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 2600, easing = LinearOutSlowInEasing)
                    )
                    onExplosionComplete?.invoke()
                }
            }
        }

        // Tap gesture modifier for interactive pops
        val tapModifier = if (enableTapBurst) {
            Modifier.pointerInput(Unit) {
                detectTapGestures { tapPos ->
                    val miniBurstParticles = generateExplosionParticles(
                        count = 20,
                        origin = tapPos,
                        screenWidth = width,
                        screenHeight = height,
                        maxDistanceMultiplier = 0.42f
                    )
                    val burstProgress = Animatable(0f)
                    val newBurst = TapBurst(
                        id = System.currentTimeMillis() + Random.nextLong(1000),
                        center = tapPos,
                        particles = miniBurstParticles,
                        progress = burstProgress
                    )
                    if (tapBursts.size >= 8) {
                        tapBursts.removeAt(0)
                    }
                    tapBursts.add(newBurst)

                    scope.launch {
                        burstProgress.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 750, easing = LinearOutSlowInEasing)
                        )
                        tapBursts.remove(newBurst)
                    }
                }
            }
        } else {
            Modifier
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .then(tapModifier)
                .testTag("particle_explosion_canvas")
        ) {
            val progressVal = explosionProgress.value

            // 1. Render expanding shockwave ring
            if (showShockwave && shockwaveProgress.value in 0.01f..0.99f) {
                val shockVal = shockwaveProgress.value
                val shockRadius = shockVal * (width * 0.55f)
                val shockAlpha = (1f - shockVal).coerceIn(0f, 1f) * 0.75f

                drawCircle(
                    color = Color(0xFFFFD700).copy(alpha = shockAlpha),
                    radius = shockRadius,
                    center = centerOffset,
                    style = Stroke(width = (14f * (1f - shockVal)).coerceAtLeast(1.5f))
                )

                // Secondary inner cyan shockwave
                drawCircle(
                    color = Color(0xFF00E5FF).copy(alpha = shockAlpha * 0.5f),
                    radius = (shockRadius * 0.85f),
                    center = centerOffset,
                    style = Stroke(width = (8f * (1f - shockVal)).coerceAtLeast(1f))
                )
            }

            // 2. Render primary Compose-animated explosion particles
            if (progressVal in 0.01f..0.99f) {
                for (p in mainParticles) {
                    drawParticleAtProgress(
                        particle = p,
                        progress = progressVal
                    )
                }
            }

            // 3. Render any interactive tap bursts (only while strictly active < 1f)
            for (burst in tapBursts.toList()) {
                val burstVal = burst.progress.value
                if (burstVal in 0.01f..0.99f) {
                    for (p in burst.particles) {
                        drawParticleAtProgress(
                            particle = p,
                            progress = burstVal
                        )
                    }
                }
            }

            // 4. Render gentle ambient floating confetti during the main active explosion window only
            if (progressVal in 0.40f..0.98f) {
                drawAmbientConfetti(
                    screenWidth = width,
                    screenHeight = height,
                    ambientProgress = ambientTime
                )
            }
        }
    }
}

/**
 * Renders a single particle at a given animation progress value [0..1].
 */
private fun DrawScope.drawParticleAtProgress(
    particle: ExplosionParticle,
    progress: Float
) {
    // Account for staggered release delay
    val effectiveProgress = ((progress - particle.delayRatio) / (1f - particle.delayRatio)).coerceIn(0f, 1f)
    if (effectiveProgress <= 0f || effectiveProgress >= 0.999f) return

    // Eased radial expansion from origin
    val radialDistance = particle.maxDistance * (1f - (1f - effectiveProgress) * (1f - effectiveProgress))

    // Gravity displacement pulling downward as time progresses
    val gravityOffset = particle.gravityBias * (effectiveProgress * effectiveProgress)

    // Aerodynamic fluttering sway for tumbling confetti ribbons & stars
    val sway = sin((effectiveProgress * 7f * PI + particle.id).toDouble()).toFloat() * (18f * (1f - effectiveProgress * 0.3f))
    val currentX = particle.originX + cos(particle.angleRad) * radialDistance + sway
    val currentY = particle.originY + sin(particle.angleRad) * radialDistance + gravityOffset

    // Size pop: quick expansion to 1.25x in the first 15%, then gradual fade
    val scale = when {
        effectiveProgress < 0.15f -> (effectiveProgress / 0.15f) * 1.25f
        effectiveProgress < 0.70f -> 1.25f - ((effectiveProgress - 0.15f) / 0.55f) * 0.25f
        else -> (1.0f - ((effectiveProgress - 0.70f) / 0.30f)).coerceAtLeast(0.05f)
    }

    // Alpha fade near end of life
    val alpha = when {
        effectiveProgress < 0.08f -> (effectiveProgress / 0.08f)
        effectiveProgress > particle.decayRatio -> ((1f - effectiveProgress) / (1f - particle.decayRatio)).coerceIn(0f, 1f)
        else -> 1f
    }

    if (alpha <= 0.02f) return

    val particleColor = particle.color.copy(alpha = alpha)
    val currentSize = particle.size * scale

    val currentRotation = particle.initialRotation + particle.rotationDelta * effectiveProgress

    when (particle.type) {
        ExplosionParticleType.STAR -> {
            rotate(degrees = currentRotation, pivot = Offset(currentX, currentY)) {
                drawStar(
                    center = Offset(currentX, currentY),
                    radius = currentSize * 1.1f,
                    color = particleColor
                )
            }
        }

        ExplosionParticleType.SPARKLE -> {
            rotate(degrees = currentRotation, pivot = Offset(currentX, currentY)) {
                drawSparkle(
                    center = Offset(currentX, currentY),
                    radius = currentSize,
                    color = particleColor
                )
            }
        }

        ExplosionParticleType.CONFETTI_RECT -> {
            val flipAngle = effectiveProgress * particle.flipSpeed * 360f
            val flipFactor = cos(flipAngle * PI / 180.0).toFloat()
            val ribbonWidth = currentSize * abs(flipFactor).coerceAtLeast(0.12f)
            val ribbonHeight = currentSize * 1.9f

            rotate(degrees = currentRotation, pivot = Offset(currentX, currentY)) {
                drawRect(
                    color = particleColor,
                    topLeft = Offset(currentX - ribbonWidth / 2f, currentY - ribbonHeight / 2f),
                    size = Size(ribbonWidth, ribbonHeight)
                )
            }
        }

        ExplosionParticleType.CIRCLE -> {
            drawCircle(
                color = particleColor,
                radius = currentSize * 0.65f,
                center = Offset(currentX, currentY)
            )
        }

        ExplosionParticleType.GLOW_ORB -> {
            // Soft glowing orb with translucent outer ring
            drawCircle(
                color = particleColor.copy(alpha = alpha * 0.35f),
                radius = currentSize * 1.1f,
                center = Offset(currentX, currentY)
            )
            drawCircle(
                color = particleColor,
                radius = currentSize * 0.55f,
                center = Offset(currentX, currentY)
            )
            drawCircle(
                color = Color.White.copy(alpha = alpha * 0.85f),
                radius = currentSize * 0.22f,
                center = Offset(currentX, currentY)
            )
        }
    }
}

/**
 * Draws gentle floating celebration confetti across the screen during the completed state.
 */
private fun DrawScope.drawAmbientConfetti(
    screenWidth: Float,
    screenHeight: Float,
    ambientProgress: Float
) {
    val count = 48
    for (i in 0 until count) {
        val seed = i * 53
        val xBase = ((seed * 79) % screenWidth.toInt()).toFloat()
        val speed = 0.5f + ((seed % 10) / 10f) * 0.9f
        val yOffset = ((ambientProgress * speed + (i.toFloat() / count)) % 1f) * (screenHeight + 60f) - 30f

        val sway = (sin((ambientProgress.toDouble() * 3.0 * PI) + i.toDouble()) * 26.0).toFloat()
        val x = (xBase + sway).coerceIn(10f, screenWidth - 10f)

        val color = ExplosionPalette[i % ExplosionPalette.size].copy(alpha = 0.65f)
        val size = 11f + (i % 7)

        when (i % 4) {
            0 -> {
                drawStar(
                    center = Offset(x, yOffset),
                    radius = size * 1.0f,
                    color = color
                )
            }
            1 -> {
                drawSparkle(
                    center = Offset(x, yOffset),
                    radius = size * 0.9f,
                    color = color
                )
            }
            2 -> {
                val rot = (ambientProgress * 720f + i * 45f) % 360f
                rotate(rot, pivot = Offset(x, yOffset)) {
                    drawRect(
                        color = color,
                        topLeft = Offset(x - size * 0.5f, yOffset - size * 1.2f),
                        size = Size(size, size * 2.4f)
                    )
                }
            }
            else -> {
                drawCircle(
                    color = color,
                    radius = size * 0.6f,
                    center = Offset(x, yOffset)
                )
            }
        }
    }
}

private data class ParticleLaunch(
    val origin: Offset,
    val angleRad: Float,
    val maxDist: Float,
    val gravityBias: Float
)

/**
 * Helper to generate random explosion particles with multi-cannon distribution.
 */
private fun generateExplosionParticles(
    count: Int,
    origin: Offset,
    screenWidth: Float,
    screenHeight: Float,
    maxDistanceMultiplier: Float = 1.0f
): List<ExplosionParticle> {
    val list = ArrayList<ExplosionParticle>(count)
    val baseDimension = minOf(screenWidth, screenHeight)

    for (i in 0 until count) {
        val launch = when {
            count <= 30 -> {
                val angle = (Random.nextFloat() * 2f * PI).toFloat()
                val dist = (Random.nextFloat() * (baseDimension * 0.35f) + baseDimension * 0.12f) * maxDistanceMultiplier
                val grav = Random.nextFloat() * (screenHeight * 0.18f) + 80f
                ParticleLaunch(origin, angle, dist, grav)
            }
            i < count * 0.60f -> {
                val angle = (Random.nextFloat() * 2f * PI).toFloat()
                val dist = (Random.nextFloat() * (baseDimension * 0.70f) + baseDimension * 0.22f) * maxDistanceMultiplier
                val grav = Random.nextFloat() * (screenHeight * 0.38f) + screenHeight * 0.15f
                ParticleLaunch(origin, angle, dist, grav)
            }
            i < count * 0.80f -> {
                val cannonOrigin = Offset(screenWidth * 0.06f, screenHeight * 0.95f)
                val angle = (-PI / 2.0 + (Random.nextFloat() - 0.25f) * (PI / 2.5)).toFloat()
                val dist = (Random.nextFloat() * (screenHeight * 0.88f) + screenHeight * 0.45f) * maxDistanceMultiplier
                val grav = Random.nextFloat() * (screenHeight * 0.48f) + screenHeight * 0.30f
                ParticleLaunch(cannonOrigin, angle, dist, grav)
            }
            else -> {
                val cannonOrigin = Offset(screenWidth * 0.94f, screenHeight * 0.95f)
                val angle = (-PI / 2.0 - (Random.nextFloat() - 0.25f) * (PI / 2.5)).toFloat()
                val dist = (Random.nextFloat() * (screenHeight * 0.88f) + screenHeight * 0.45f) * maxDistanceMultiplier
                val grav = Random.nextFloat() * (screenHeight * 0.48f) + screenHeight * 0.30f
                ParticleLaunch(cannonOrigin, angle, dist, grav)
            }
        }

        val type = when {
            i % 6 == 0 -> ExplosionParticleType.STAR
            i % 6 == 1 -> ExplosionParticleType.SPARKLE
            i % 6 in 2..3 -> ExplosionParticleType.CONFETTI_RECT
            i % 6 == 4 -> ExplosionParticleType.GLOW_ORB
            else -> ExplosionParticleType.CIRCLE
        }

        list.add(
            ExplosionParticle(
                id = i,
                originX = launch.origin.x + (Random.nextFloat() - 0.5f) * 20f,
                originY = launch.origin.y + (Random.nextFloat() - 0.5f) * 20f,
                angleRad = launch.angleRad,
                maxDistance = launch.maxDist,
                gravityBias = launch.gravityBias,
                speedCurve = Random.nextFloat() * 0.4f + 0.8f,
                size = Random.nextFloat() * 14f + 14f,
                color = ExplosionPalette.random(),
                type = type,
                initialRotation = Random.nextFloat() * 360f,
                rotationDelta = (Random.nextFloat() - 0.5f) * 900f,
                flipSpeed = Random.nextFloat() * 4f + 2f,
                delayRatio = (Random.nextFloat() * 0.16f),
                decayRatio = Random.nextFloat() * 0.22f + 0.72f
            )
        )
    }

    return list
}

/**
 * Draws a 5-pointed star centered at [center].
 */
private fun DrawScope.drawStar(
    center: Offset,
    radius: Float,
    color: Color
) {
    val innerRadius = radius * 0.42f
    val path = Path()

    for (i in 0 until 10) {
        val r = if (i % 2 == 0) radius else innerRadius
        val angle = -PI / 2 + i * (PI / 5)
        val x = (center.x + r * cos(angle)).toFloat()
        val y = (center.y + r * sin(angle)).toFloat()
        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    path.close()
    drawPath(path = path, color = color)
}

/**
 * Draws a 4-pointed sparkling diamond centered at [center].
 */
private fun DrawScope.drawSparkle(
    center: Offset,
    radius: Float,
    color: Color
) {
    val path = Path().apply {
        moveTo(center.x, center.y - radius)
        lineTo(center.x + radius * 0.26f, center.y - radius * 0.26f)
        lineTo(center.x + radius, center.y)
        lineTo(center.x + radius * 0.26f, center.y + radius * 0.26f)
        lineTo(center.x, center.y + radius)
        lineTo(center.x - radius * 0.26f, center.y + radius * 0.26f)
        lineTo(center.x - radius, center.y)
        lineTo(center.x - radius * 0.26f, center.y - radius * 0.26f)
        close()
    }
    drawPath(path = path, color = color)
    drawCircle(
        color = Color.White.copy(alpha = color.alpha),
        radius = radius * 0.25f,
        center = center
    )
}
