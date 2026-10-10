package com.example.domain.tracing

import com.example.core.util.MathUtils

/**
 * Normalized 2D coordinate in unit space [0.0..1.0].
 * (0,0) is top-left of the bounding box, (1,1) is bottom-right.
 */
data class NormalizedPoint(
    val x: Float,
    val y: Float
)

/**
 * Defines a single stroke of a character with its expected corridor, checkpoints, and tolerance.
 */
data class TraceStroke(
    val id: Int,
    val pathPoints: List<NormalizedPoint>,
    val tolerance: Float = 0.18f,               // Doubled half-width corridor matching extra-thick track
    val startRadius: Float = 0.20f,             // Generous toddler start radius
    val requiredProgressThreshold: Float = 0.88f // 88% checkpoints needed to snap to completed
) {
    val checkpoints: List<NormalizedPoint> by lazy {
        MathUtils.generateCheckpoints(pathPoints, stepSize = 0.022f)
    }

    val startPoint: NormalizedPoint
        get() = pathPoints.firstOrNull() ?: NormalizedPoint(0.5f, 0.5f)

    val endPoint: NormalizedPoint
        get() = pathPoints.lastOrNull() ?: NormalizedPoint(0.5f, 0.5f)
}

/**
 * Defines an entire multi-stroke character.
 */
data class TraceCharacterDef(
    val id: String,
    val displayChar: String,
    val strokes: List<TraceStroke>
)
