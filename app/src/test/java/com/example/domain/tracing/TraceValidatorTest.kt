package com.example.domain.tracing

import com.example.data.content.PrototypeCharacters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TraceValidatorTest {

    @Test
    fun testStartPointValidation_outsideStartRadius_rejected() {
        val def = PrototypeCharacters.NUMBER_1.traceDefinition
        val validator = TraceValidator(TracingState(characterDef = def))

        // First point of 1 is (0.38, 0.32). Touching at (0.9, 0.9) must be rejected
        val state = validator.onTouchDown(NormalizedPoint(0.9f, 0.9f))
        assertEquals(TracingFeedback.WAITING_FOR_START, state.feedback)
        assertTrue(state.activeUserPoints.isEmpty())
        assertFalse(state.isCharacterCompleted)
    }

    @Test
    fun testStartPointValidation_nearStartPoint_accepted() {
        val def = PrototypeCharacters.NUMBER_1.traceDefinition
        val validator = TraceValidator(TracingState(characterDef = def))

        // First point is (0.38, 0.32). Touching at (0.39, 0.32) should lock start
        val state = validator.onTouchDown(NormalizedPoint(0.39f, 0.32f))
        assertEquals(TracingFeedback.START_LOCKED, state.feedback)
        assertFalse(state.activeUserPoints.isEmpty())
    }

    @Test
    fun testCorridorValidation_touchOutsideCorridor_markedOffCorridor() {
        val def = PrototypeCharacters.NUMBER_1.traceDefinition
        val validator = TraceValidator(TracingState(characterDef = def))

        // Start correctly
        validator.onTouchDown(NormalizedPoint(0.38f, 0.32f))

        // Move far to the right (x = 0.9f)
        val state = validator.onTouchMove(NormalizedPoint(0.90f, 0.32f))
        assertEquals(TracingFeedback.OFF_CORRIDOR, state.feedback)
    }

    @Test
    fun testAntiTeleportation_jumpingToEnd_rejected() {
        val def = PrototypeCharacters.NUMBER_1.traceDefinition
        val validator = TraceValidator(TracingState(characterDef = def))

        // Start correctly
        validator.onTouchDown(NormalizedPoint(0.38f, 0.32f))

        // Immediately try to touch the bottom end (0.50, 0.82)
        val state = validator.onTouchMove(NormalizedPoint(0.50f, 0.82f))
        // Progress should not teleport to 100%
        assertTrue("Progress must not jump directly to completion", state.currentStrokeProgress < 0.25f)
        assertFalse("Character must not be completed from teleportation", state.isCharacterCompleted)
    }

    @Test
    fun testAntiCutting_lowercaseA_jumpingToStem_rejected() {
        // Lowercase 'a' loop starts at (0.66, 0.48), loops around the left counter, ends at (0.66, 0.52)
        val def = PrototypeCharacters.LETTER_LOWER_A.traceDefinition
        val validator = TraceValidator(TracingState(characterDef = def))

        val stroke1 = def.strokes[0]
        validator.onTouchDown(stroke1.checkpoints.first())

        // Try to cut directly to the end of stroke 1 without traversing the left loop
        val stateAfterCutting = validator.onTouchMove(stroke1.checkpoints.last())
        assertEquals("Stroke 1 must not complete by cutting across", 0, stateAfterCutting.completedStrokes.size)
        assertFalse(stateAfterCutting.isCharacterCompleted)
        assertTrue("Progress must not advance by cutting across loop", stateAfterCutting.currentStrokeProgress < 0.25f)

        // Try to jump directly down to the bottom stem (0.66, 0.84)
        val stateAfterStemJump = validator.onTouchMove(NormalizedPoint(0.66f, 0.84f))
        assertEquals(TracingFeedback.OFF_CORRIDOR, stateAfterStemJump.feedback)
        assertEquals("Stroke 1 must not complete by jumping to bottom stem", 0, stateAfterStemJump.completedStrokes.size)
        assertFalse(stateAfterStemJump.isCharacterCompleted)
    }

    @Test
    fun testValidStrokeProgression_completesCharacter1() {
        val def = PrototypeCharacters.NUMBER_1.traceDefinition
        val validator = TraceValidator(TracingState(characterDef = def))

        val stroke = def.strokes[0]
        validator.onTouchDown(stroke.checkpoints.first())

        var finalState = validator.getCurrentState()
        for (pt in stroke.checkpoints) {
            finalState = validator.onTouchMove(pt)
        }

        assertTrue("Number 1 should complete when checkpoints are sequentially traced", finalState.isCharacterCompleted)
        assertEquals(1, finalState.completedStrokes.size)
    }

    @Test
    fun testMultiStrokeProgression_letterA() {
        val def = PrototypeCharacters.LETTER_A.traceDefinition
        val validator = TraceValidator(TracingState(characterDef = def))

        assertEquals(3, def.strokes.size)

        // 1. Trace Stroke 1 (Left slant)
        validator.onTouchDown(def.strokes[0].checkpoints.first())
        for (pt in def.strokes[0].checkpoints) {
            validator.onTouchMove(pt)
        }
        var state = validator.onTouchUp()
        assertEquals("Stroke 1 should be completed", 1, state.completedStrokes.size)
        assertEquals(1, state.currentStrokeIndex)
        assertFalse(state.isCharacterCompleted)

        // 2. Trace Stroke 2 (Right slant)
        validator.onTouchDown(def.strokes[1].checkpoints.first())
        for (pt in def.strokes[1].checkpoints) {
            validator.onTouchMove(pt)
        }
        state = validator.onTouchUp()
        assertEquals("Stroke 2 should be completed", 2, state.completedStrokes.size)
        assertEquals(2, state.currentStrokeIndex)
        assertFalse(state.isCharacterCompleted)

        // 3. Trace Stroke 3 (Crossbar)
        validator.onTouchDown(def.strokes[2].checkpoints.first())
        for (pt in def.strokes[2].checkpoints) {
            validator.onTouchMove(pt)
        }
        state = validator.onTouchUp()
        assertEquals("Stroke 3 should be completed", 3, state.completedStrokes.size)
        assertTrue("Letter A should be completed after all 3 strokes", state.isCharacterCompleted)
    }
}
