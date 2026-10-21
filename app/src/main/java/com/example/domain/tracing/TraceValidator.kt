package com.example.domain.tracing

import com.example.core.util.MathUtils
import kotlin.math.max
import kotlin.math.min

class TraceValidator(
    private var state: TracingState
) {

    fun getCurrentState(): TracingState = state

    fun reset(characterDef: TraceCharacterDef) {
        state = TracingState(characterDef = characterDef)
    }

    fun onTouchDown(point: NormalizedPoint): TracingState {
        if (state.isCharacterCompleted) return state
        val stroke = state.activeStroke ?: return state
        val checkpoints = stroke.checkpoints
        if (checkpoints.isEmpty()) return state

        // 1. Check start point validation
        val distToStart = MathUtils.distance(point, checkpoints.first())
        val isAtStart = distToStart <= stroke.startRadius

        // 2. Allow seamless resume from current progress if finger was lifted mid-stroke
        val resumePoint = state.smoothHeadPosition ?: if (state.highestReachedCheckpointIndex in checkpoints.indices) {
            checkpoints[state.highestReachedCheckpointIndex]
        } else null

        val isAtResumePoint = if (resumePoint != null && state.highestReachedCheckpointIndex > 0) {
            MathUtils.distance(point, resumePoint) <= stroke.tolerance * 1.5f
        } else false

        return if (isAtStart || isAtResumePoint) {
            val startIdx = if (isAtResumePoint) state.highestReachedCheckpointIndex else 0
            val newVisited = if (isAtResumePoint) state.visitedCheckpointIndices else setOf(0)
            val initialProgress = (startIdx.toFloat() / (checkpoints.size - 1).coerceAtLeast(1)).coerceAtLeast(0.02f)
            val head = if (isAtResumePoint) resumePoint else checkpoints.first()

            state = state.copy(
                activeUserPoints = listOf(point),
                smoothHeadPosition = head,
                highestReachedCheckpointIndex = startIdx,
                visitedCheckpointIndices = newVisited,
                currentStrokeProgress = initialProgress,
                feedback = TracingFeedback.START_LOCKED
            )
            state
        } else {
            // Touch began away from designated start point or resume point
            state = state.copy(
                activeUserPoints = emptyList(),
                smoothHeadPosition = null,
                feedback = TracingFeedback.WAITING_FOR_START,
                totalErrorTouches = state.totalErrorTouches + 1
            )
            state
        }
    }

    fun onTouchMove(point: NormalizedPoint): TracingState {
        if (state.isCharacterCompleted) return state
        val stroke = state.activeStroke ?: return state
        val checkpoints = stroke.checkpoints
        if (checkpoints.isEmpty()) return state

        if (state.activeUserPoints.isEmpty() && state.feedback != TracingFeedback.START_LOCKED) {
            return state
        }

        // Micro-jitter deduplication: skip redundant tiny moves (< 1-2px in normalized space)
        val lastPoint = state.activeUserPoints.lastOrNull()
        if (lastPoint != null && MathUtils.distance(point, lastPoint) < 0.0018f) {
            return state
        }

        val currentIdx = state.highestReachedCheckpointIndex
        val totalCheckpoints = checkpoints.size

        // Dynamic forward window: look slightly behind (up to 4 checkpoints for jitter recovery)
        // and look ahead generously (up to 16 checkpoints) so natural toddler swipes never stall
        val windowLookback = max(0, currentIdx - 4)
        val windowLookahead = min(totalCheckpoints - 1, currentIdx + 16)

        var closestIdx = currentIdx
        var minDistance = Float.MAX_VALUE
        var bestProjectedPoint = checkpoints[currentIdx]

        for (i in windowLookback until windowLookahead) {
            val p1 = checkpoints[i]
            val p2 = checkpoints[i + 1]

            // Calculate perpendicular projection onto segment p1-p2
            val segLenSq = (p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y)
            val t = if (segLenSq == 0f) 0f else {
                max(0f, min(1f, ((point.x - p1.x) * (p2.x - p1.x) + (point.y - p1.y) * (p2.y - p1.y)) / segLenSq))
            }
            val projX = p1.x + t * (p2.x - p1.x)
            val projY = p1.y + t * (p2.y - p1.y)
            val dist = MathUtils.distance(point, NormalizedPoint(projX, projY))

            if (dist < minDistance) {
                minDistance = dist
                closestIdx = if (t > 0.5f) i + 1 else i
                bestProjectedPoint = NormalizedPoint(projX, projY)
            }
        }

        // Elastic tolerance corridor with gentle leeway for preschool fingers
        val effectiveTolerance = stroke.tolerance * 1.30f

        if (minDistance > effectiveTolerance) {
            // Outside corridor
            state = state.copy(
                activeUserPoints = state.activeUserPoints + point,
                feedback = TracingFeedback.OFF_CORRIDOR
            )
            return state
        }

        // Sequential advancement forward
        val newHighest = max(currentIdx, closestIdx)
        val newVisited = state.visitedCheckpointIndices.toMutableSet()
        for (idx in currentIdx..newHighest) {
            newVisited.add(idx)
        }

        val progressFraction = (newHighest.toFloat() / (totalCheckpoints - 1).coerceAtLeast(1)).coerceIn(0f, 1f)

        // Completion criteria: reached end zone and covered required checkpoints
        val reachedEndZone = newHighest >= totalCheckpoints - 4
        val coverageRatio = newVisited.size.toFloat() / totalCheckpoints
        val hasRequiredCoverage = coverageRatio >= stroke.requiredProgressThreshold * 0.88f

        if (reachedEndZone && hasRequiredCoverage) {
            val updatedCompletedStrokes = state.completedStrokes + stroke.id
            val nextStrokeIdx = state.currentStrokeIndex + 1
            val isAllDone = updatedCompletedStrokes.size >= state.characterDef.strokes.size

            state = state.copy(
                currentStrokeIndex = nextStrokeIdx,
                currentStrokeProgress = 0f,
                highestReachedCheckpointIndex = 0,
                visitedCheckpointIndices = emptySet(),
                completedStrokes = updatedCompletedStrokes,
                activeUserPoints = emptyList(),
                smoothHeadPosition = null,
                isCharacterCompleted = isAllDone,
                feedback = if (isAllDone) TracingFeedback.CHARACTER_COMPLETED else TracingFeedback.STROKE_COMPLETED,
                stars = calculateStars(state.totalErrorTouches)
            )
            return state
        }

        state = state.copy(
            currentStrokeProgress = progressFraction,
            highestReachedCheckpointIndex = newHighest,
            visitedCheckpointIndices = newVisited,
            activeUserPoints = state.activeUserPoints + point,
            smoothHeadPosition = bestProjectedPoint,
            feedback = TracingFeedback.TRACING_CORRIDOR
        )
        return state
    }

    fun onTouchUp(): TracingState {
        if (state.isCharacterCompleted) return state
        val stroke = state.activeStroke ?: return state
        val checkpoints = stroke.checkpoints
        if (checkpoints.isEmpty()) return state

        val totalCheckpoints = checkpoints.size
        val reachedEndZone = state.highestReachedCheckpointIndex >= totalCheckpoints - 4
        val coverageRatio = state.visitedCheckpointIndices.size.toFloat() / totalCheckpoints
        val hasRequiredCoverage = coverageRatio >= stroke.requiredProgressThreshold * 0.88f

        if (reachedEndZone && hasRequiredCoverage) {
            val updatedCompletedStrokes = state.completedStrokes + stroke.id
            val nextStrokeIdx = state.currentStrokeIndex + 1
            val isAllDone = updatedCompletedStrokes.size >= state.characterDef.strokes.size

            state = state.copy(
                currentStrokeIndex = nextStrokeIdx,
                currentStrokeProgress = 0f,
                highestReachedCheckpointIndex = 0,
                visitedCheckpointIndices = emptySet(),
                completedStrokes = updatedCompletedStrokes,
                activeUserPoints = emptyList(),
                smoothHeadPosition = null,
                isCharacterCompleted = isAllDone,
                feedback = if (isAllDone) TracingFeedback.CHARACTER_COMPLETED else TracingFeedback.STROKE_COMPLETED,
                stars = calculateStars(state.totalErrorTouches)
            )
        } else {
            // Keep current progress and smoothHeadPosition in place so the child can seamlessly resume
            state = state.copy(
                activeUserPoints = emptyList(),
                feedback = TracingFeedback.IDLE
            )
        }
        return state
    }

    private fun calculateStars(errorCount: Int): Int {
        return when {
            errorCount <= 2 -> 3
            errorCount <= 5 -> 2
            else -> 1
        }
    }
}
