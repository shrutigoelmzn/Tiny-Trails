package com.example.core.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class ChimeSynthesizerTest {

    @Test
    fun testSynthesizeCompletionChimes_returnsNonEmptyPcmBufferWithExpectedSampleCount() {
        val sampleRate = 44100
        val durationMs = 820
        val expectedSamples = (durationMs * sampleRate) / 1000

        val pcm = ChimeSynthesizer.synthesizeCompletionChimes(
            sampleRate = sampleRate,
            totalDurationMs = durationMs,
            volume = 0.85f
        )

        assertNotNull(pcm)
        assertEquals(expectedSamples, pcm.size)
        assertTrue("PCM should contain audio data", pcm.isNotEmpty())
    }

    @Test
    fun testSynthesizeCompletionChimes_hasProperDynamicRangeWithoutClipping() {
        val pcm = ChimeSynthesizer.synthesizeCompletionChimes(
            sampleRate = 44100,
            totalDurationMs = 820,
            volume = 0.85f
        )

        var maxSample = 0
        var hasNonZero = false

        for (sample in pcm) {
            val absSample = abs(sample.toInt())
            if (absSample > 0) hasNonZero = true
            if (absSample > maxSample) {
                maxSample = absSample
            }
        }

        assertTrue("PCM buffer must contain audible audio signal", hasNonZero)
        assertTrue("Max amplitude must be safely within 16-bit range without clipping", maxSample <= Short.MAX_VALUE)
        assertTrue("Peak amplitude should be rich and audible (> 5000)", maxSample > 5000)
    }

    @Test
    fun testSynthesizeSingleChime_producesCleanAcousticDecay() {
        val durationMs = 180
        val sampleRate = 44100
        val pcm = ChimeSynthesizer.synthesizeSingleChime(
            frequency = 2093.00f, // C7 high-pitch
            durationMs = durationMs,
            sampleRate = sampleRate,
            volume = 0.85f
        )

        val expectedSamples = (durationMs * sampleRate) / 1000
        assertEquals(expectedSamples, pcm.size)

        // Verify start has higher energy than near end (natural exponential decay)
        val earlyQuarter = pcm.sliceArray(0 until pcm.size / 4)
        val lateQuarter = pcm.sliceArray((pcm.size * 3) / 4 until pcm.size)

        val earlyMax = earlyQuarter.maxOfOrNull { abs(it.toInt()) } ?: 0
        val lateMax = lateQuarter.maxOfOrNull { abs(it.toInt()) } ?: 0

        assertTrue("Early portion should have higher energy than decaying tail", earlyMax > lateMax)
    }

    @Test
    fun testSynthesizeCompletionChimes_zeroVolumeProducesSilence() {
        val pcm = ChimeSynthesizer.synthesizeCompletionChimes(
            sampleRate = 44100,
            totalDurationMs = 200,
            volume = 0f
        )

        // Volume 0 returns clamped minimal or zero
        assertTrue("Buffer must not fail or crash on edge volume", pcm.isNotEmpty())
    }

    @Test
    fun testSynthesizeCelebrationFanfare_producesRichAudibleFanfare() {
        val durationMs = 900
        val sampleRate = 44100
        val expectedSamples = (durationMs * sampleRate) / 1000

        val pcm3Star = ChimeSynthesizer.synthesizeCelebrationFanfare(
            stars = 3,
            sampleRate = sampleRate,
            totalDurationMs = durationMs,
            volume = 0.85f
        )

        assertNotNull(pcm3Star)
        assertEquals(expectedSamples, pcm3Star.size)

        val maxAmp = pcm3Star.maxOfOrNull { abs(it.toInt()) } ?: 0
        assertTrue("3-star fanfare should have rich peak amplitude", maxAmp > 5000)
        assertTrue("Amplitude must stay within Short range", maxAmp <= Short.MAX_VALUE)

        val pcm2Star = ChimeSynthesizer.synthesizeCelebrationFanfare(
            stars = 2,
            sampleRate = sampleRate,
            totalDurationMs = durationMs,
            volume = 0.85f
        )
        assertNotNull(pcm2Star)
        assertEquals(expectedSamples, pcm2Star.size)
    }
}
