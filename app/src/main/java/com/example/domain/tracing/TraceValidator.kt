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

        // 2. Allow resume from current progress if finger was lifted mid-stroke
        val isAtResumePoint = if (state.highestReachedCheckpointIndex > 0 && state.highestReachedCheckpointIndex < checkpoints.size) {
            val resumePoint = checkpoints[state.highestReachedCheckpointIndex]
            MathUtils.distance(point, resumePoint) <= stroke.tolerance * 1.4f
        } else false

        return if (isAtStart || isAtResumePoint) {
            val startIdx = if (isAtResumePoint) state.highestReachedCheckpointIndex else 0
            val newVisited = if (isAtResumePoint) state.visitedCheckpointIndices else setOf(0)
            val initialProgress = (startIdx.toFloat() / (checkpoints.size - 1).coerceAtLeast(1)).coerceAtLeast(0.02f)

            state = state.copy(
                activeUserPoints = listOf(point),
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

        val currentIdx = state.highestReachedCheckpointIndex
        val totalCheckpoints = checkpoints.size

        // Restrict search strictly to a forward look-ahead window around the current index.
        // This makes it mathematically impossible to skip checkpoints or jump directly to the end.
        val windowLookback = max(0, currentIdx - 3)
        val windowLookahead = min(totalCheckpoints - 1, currentIdx + 4)

        var closestIdx = currentIdx
        var minDistance = Float.MAX_VALUE

        for (i in windowLookback..windowLookahead) {
            val dist = MathUtils.distance(point, checkpoints[i])
            if (dist < minDistance) {
                minDistance = dist
                closestIdx = i
            }
        }

        // Validate that finger stays inside the allowed stroke corridor
        if (minDistance > stroke.tolerance) {
            // Outside the corridor or tried to jump past the look-ahead window
            state = state.copy(
                activeUserPoints = state.activeUserPoints + point,
                feedback = TracingFeedback.OFF_CORRIDOR
            )
            return state
        }

        // Sequential advancement: only advance if user moves forward in sequence
        val newHighest = max(currentIdx, closestIdx)
        val newVisited = state.visitedCheckpointIndices.toMutableSet()
        for (idx in currentIdx..newHighest) {
            newVisited.add(idx)
        }

        val progressFraction = newHighest.toFloat() / (totalCheckpoints - 1).coerceAtLeast(1)

        // Strict completion requirements:
        // 1. Must reach the final checkpoint zone (last 3 checkpoints)
        // 2. Must have visited >= requiredProgressThreshold of ALL intermediate checkpoints sequentially!
        val reachedEndZone = newHighest >= totalCheckpoints - 3
        val coverageRatio = newVisited.size.toFloat() / totalCheckpoints
        val hasRequiredCoverage = coverageRatio >= stroke.requiredProgressThreshold

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
        val reachedEndZone = state.highestReachedCheckpointIndex >= totalCheckpoints - 3
        val coverageRatio = state.visitedCheckpointIndices.size.toFloat() / totalCheckpoints
        val hasRequiredCoverage = coverageRatio >= stroke.requiredProgressThreshold * 0.95f

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
                isCharacterCompleted = isAllDone,
                feedback = if (isAllDone) TracingFeedback.CHARACTER_COMPLETED else TracingFeedback.STROKE_COMPLETED,
                stars = calculateStars(state.totalErrorTouches)
            )
        } else {
            // Keep current progress in place so the child can resume
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
