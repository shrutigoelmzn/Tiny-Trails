package com.example.domain.tracing

enum class TracingFeedback {
    IDLE,
    WAITING_FOR_START,
    START_LOCKED,
    TRACING_CORRIDOR,
    OFF_CORRIDOR,
    STROKE_COMPLETED,
    CHARACTER_COMPLETED
}

data class TracingState(
    val characterDef: TraceCharacterDef,
    val currentStrokeIndex: Int = 0,
    val currentStrokeProgress: Float = 0f,
    val highestReachedCheckpointIndex: Int = 0,
    val visitedCheckpointIndices: Set<Int> = emptySet(),
    val completedStrokes: List<Int> = emptyList(),
    val activeUserPoints: List<NormalizedPoint> = emptyList(),
    val smoothHeadPosition: NormalizedPoint? = null,
    val isCharacterCompleted: Boolean = false,
    val feedback: TracingFeedback = TracingFeedback.IDLE,
    val totalErrorTouches: Int = 0,
    val stars: Int = 3
) {
    val activeStroke: TraceStroke?
        get() = characterDef.strokes.getOrNull(currentStrokeIndex)

    val isAllStrokesDone: Boolean
        get() = completedStrokes.size >= characterDef.strokes.size
}
