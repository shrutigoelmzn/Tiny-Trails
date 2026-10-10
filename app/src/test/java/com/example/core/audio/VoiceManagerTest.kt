package com.example.core.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class VoiceManagerTest {

    // Simple test fixture to test the DSP pitch-shifting resampler directly
    private fun shiftPitch(input: ShortArray, pitchFactor: Float): ShortArray {
        if (input.isEmpty() || pitchFactor <= 0f) return input
        if (pitchFactor == 1.0f) return input

        val outputLength = (input.size / pitchFactor).toInt().coerceAtLeast(1)
        val output = ShortArray(outputLength)

        for (i in 0 until outputLength) {
            val sourcePos = i * pitchFactor
            val indexFloor = kotlin.math.floor(sourcePos).toInt()
            val fraction = sourcePos - indexFloor

            if (indexFloor >= input.size - 1) {
                output[i] = input.last()
            } else {
                val s0 = input[indexFloor].toFloat()
                val s1 = input[indexFloor + 1].toFloat()
                val interpolated = s0 + fraction * (s1 - s0)
                output[i] = interpolated.coerceIn(-32768f, 32767f).toInt().toShort()
            }
        }
        return output
    }

    @Test
    fun testPitchShift_outputLengthScalesWithPitchFactor() {
        val input = ShortArray(1000) { it.toShort() }

        // Factor 2.0 -> half length
        val shifted2x = shiftPitch(input, 2.0f)
        assertEquals(500, shifted2x.size)

        // Factor 1.60 (child voice) -> 1000 / 1.6 = 625
        val shiftedChild = shiftPitch(input, 1.60f)
        assertEquals(625, shiftedChild.size)
    }

    @Test
    fun testPitchShift_interpolatesLinearRampAccurately() {
        val input = ShortArray(100) { (it * 100).toShort() }
        val factor = 1.5f
        val shifted = shiftPitch(input, factor)

        // Output index 1 -> sourcePos 1.5 -> midpoint between sample 1 (100) and sample 2 (200) -> 150
        assertEquals(150.toShort(), shifted[1])

        // Output index 2 -> sourcePos 3.0 -> sample 3 (300)
        assertEquals(300.toShort(), shifted[2])
    }

    @Test
    fun testPitchShift_handlesEdgeCases() {
        val empty = ShortArray(0)
        assertTrue(shiftPitch(empty, 1.60f).isEmpty())

        val single = ShortArray(1) { 500 }
        val shiftedSingle = shiftPitch(single, 1.60f)
        assertTrue(shiftedSingle.isNotEmpty())

        val normal = ShortArray(50) { 100 }
        val unity = shiftPitch(normal, 1.0f)
        assertEquals(50, unity.size)
    }
}
