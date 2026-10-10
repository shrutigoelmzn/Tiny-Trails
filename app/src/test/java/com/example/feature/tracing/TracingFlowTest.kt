package com.example.feature.tracing

import com.example.data.content.PrototypeCharacters
import com.example.domain.tracing.NormalizedPoint
import com.example.domain.tracing.TraceValidator
import com.example.domain.tracing.TracingFeedback
import com.example.domain.tracing.TracingState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TracingFlowTest {

    @Test
    fun testTraceUiState_inputBlockingRules() {
        // Input MUST be strictly blocked in Loading, Demo, and Completed
        assertFalse("Input must be blocked in Loading state", TraceUiState.Loading.isInputAllowed)
        assertFalse("Input must be blocked in Demo state", TraceUiState.Demo.isInputAllowed)
        assertFalse("Input must be blocked in Completed state", TraceUiState.Completed.isInputAllowed)

        // Input MUST be accepted in ReadyToTrace and Tracing
        assertTrue("Input must be accepted in ReadyToTrace state", TraceUiState.ReadyToTrace.isInputAllowed)
        assertTrue("Input must be accepted in Tracing state", TraceUiState.Tracing.isInputAllowed)
    }

    @Test
    fun testDemoAndUserTracing_useSameStrokeDefinitions() {
        val character = PrototypeCharacters.LETTER_A
        val def = character.traceDefinition

        // Verify multi-stroke order is preserved: Stroke 1 -> Stroke 2 -> Stroke 3
        assertEquals(3, def.strokes.size)
        assertEquals(1, def.strokes[0].id)
        assertEquals(2, def.strokes[1].id)
        assertEquals(3, def.strokes[2].id)

        // Verify checkpoints exist for all strokes (used for both demo and user validation)
        for (stroke in def.strokes) {
            assertTrue("Stroke ${stroke.id} must have checkpoints", stroke.checkpoints.size >= 4)
            assertEquals(stroke.startPoint, stroke.checkpoints.first())
            assertEquals(stroke.endPoint, stroke.checkpoints.last())
        }
    }

    @Test
    fun testPristineUserStateAfterDemo_progressBeginsAtZero() {
        val def = PrototypeCharacters.NUMBER_1.traceDefinition
        val validator = TraceValidator(TracingState(characterDef = def))
        val state = validator.getCurrentState()

        assertEquals(0, state.completedStrokes.size)
        assertEquals(0f, state.currentStrokeProgress, 0.001f)
        assertTrue(state.activeUserPoints.isEmpty())
        assertFalse(state.isCharacterCompleted)
        assertEquals(TracingFeedback.IDLE, state.feedback)
    }

    @Test
    fun testSequentialMultiStrokeTracing_requiresCompletingStroke1BeforeStroke2() {
        val def = PrototypeCharacters.NUMBER_3.traceDefinition
        val validator = TraceValidator(TracingState(characterDef = def))

        // Stroke 1 starts at (0.34, 0.24). Stroke 2 starts at (0.52, 0.48).
        // Try to touch stroke 2 start point while stroke 1 is active
        val stroke2Start = def.strokes[1].startPoint
        val touchState = validator.onTouchDown(stroke2Start)

        // Stroke 2 must not activate before stroke 1 is completed
        assertEquals(0, touchState.currentStrokeIndex)
        assertEquals(TracingFeedback.WAITING_FOR_START, touchState.feedback)
        assertTrue(touchState.completedStrokes.isEmpty())
    }
}
