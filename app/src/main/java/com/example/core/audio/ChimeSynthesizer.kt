package com.example.core.audio

import kotlin.math.exp
import kotlin.math.sin

/**
 * Acoustic synthesizer that generates short, high-pitched, child-friendly chime sounds
 * for tracing completions and milestone celebrations.
 *
 * Uses harmonic bell synthesis (Euler-Bernoulli chime overtones with fast attack and
 * natural exponential acoustic decay) to produce authentic, music-box-like chimes
 * distinct from instructional voice feedback.
 */
object ChimeSynthesizer {

    const val DEFAULT_SAMPLE_RATE = 44100

    // High-pitched celebratory arpeggio notes (C6, E6, G6, C7, G7)
    val COMPLETION_NOTES = listOf(
        ChimeNote(freq = 1046.50f, offsetMs = 0, decayTau = 0.32f, amplitude = 0.55f),   // C6
        ChimeNote(freq = 1318.51f, offsetMs = 85, decayTau = 0.34f, amplitude = 0.65f),  // E6
        ChimeNote(freq = 1567.98f, offsetMs = 170, decayTau = 0.38f, amplitude = 0.75f), // G6
        ChimeNote(freq = 2093.00f, offsetMs = 260, decayTau = 0.44f, amplitude = 0.85f), // C7
        ChimeNote(freq = 3135.96f, offsetMs = 360, decayTau = 0.50f, amplitude = 0.90f)  // G7 (sparkling chime tail)
    )

    // Grand 3-star celebratory arpeggio notes (C6, E6, G6, C7, E7, G7)
    val THREE_STAR_FANFARE_NOTES = listOf(
        ChimeNote(freq = 1046.50f, offsetMs = 0, decayTau = 0.35f, amplitude = 0.55f),   // C6
        ChimeNote(freq = 1318.51f, offsetMs = 70, decayTau = 0.36f, amplitude = 0.65f),  // E6
        ChimeNote(freq = 1567.98f, offsetMs = 140, decayTau = 0.40f, amplitude = 0.75f), // G6
        ChimeNote(freq = 2093.00f, offsetMs = 210, decayTau = 0.44f, amplitude = 0.85f), // C7
        ChimeNote(freq = 2637.02f, offsetMs = 280, decayTau = 0.48f, amplitude = 0.90f), // E7
        ChimeNote(freq = 3135.96f, offsetMs = 360, decayTau = 0.55f, amplitude = 0.95f)  // G7
    )

    data class ChimeNote(
        val freq: Float,
        val offsetMs: Int,
        val decayTau: Float,
        val amplitude: Float
    )

    /**
     * Synthesizes an extra-rewarding celebratory fanfare with shimmering higher harmonics
     * tailored for milestone tracing completions and star achievements.
     */
    fun synthesizeCelebrationFanfare(
        stars: Int = 3,
        sampleRate: Int = DEFAULT_SAMPLE_RATE,
        totalDurationMs: Int = 900,
        volume: Float = 0.85f
    ): ShortArray {
        val notes = if (stars >= 3) THREE_STAR_FANFARE_NOTES else COMPLETION_NOTES
        val totalSamples = (totalDurationMs * sampleRate) / 1000
        val buffer = FloatArray(totalSamples)

        for (note in notes) {
            val startSample = (note.offsetMs * sampleRate) / 1000
            val noteDurationMs = totalDurationMs - note.offsetMs
            if (noteDurationMs <= 0) continue
            val noteSamples = (noteDurationMs * sampleRate) / 1000

            for (i in 0 until noteSamples) {
                val bufferIdx = startSample + i
                if (bufferIdx >= totalSamples) break

                val t = i.toDouble() / sampleRate
                val attack = if (t < 0.003) (t / 0.003) else 1.0
                val decay = exp(-t / note.decayTau)
                val envelope = attack * decay

                val fundamental = sin(2.0 * Math.PI * note.freq * t)
                val overtone1 = 0.32 * sin(2.0 * Math.PI * (note.freq * 2.76) * t)
                val overtone2 = 0.15 * sin(2.0 * Math.PI * (note.freq * 5.40) * t)

                val wave = (fundamental + overtone1 + overtone2) * envelope * note.amplitude
                buffer[bufferIdx] += wave.toFloat()
            }
        }

        var maxPeak = 0f
        for (sample in buffer) {
            val absVal = kotlin.math.abs(sample)
            if (absVal > maxPeak) {
                maxPeak = absVal
            }
        }

        val normFactor = if (maxPeak > 0f) {
            (Short.MAX_VALUE * volume.coerceIn(0.1f, 1.0f) * 0.90f) / maxPeak
        } else {
            0f
        }

        val pcm = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val scaled = (buffer[i] * normFactor).toInt()
            pcm[i] = scaled.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        return pcm
    }

    /**
     * Synthesizes a cascading polyphonic chime fanfare for tracing session completion.
     * Returns a 16-bit PCM ShortArray ready for static AudioTrack playback.
     */
    fun synthesizeCompletionChimes(
        sampleRate: Int = DEFAULT_SAMPLE_RATE,
        totalDurationMs: Int = 820,
        volume: Float = 0.85f
    ): ShortArray {
        val totalSamples = (totalDurationMs * sampleRate) / 1000
        val buffer = FloatArray(totalSamples)

        for (note in COMPLETION_NOTES) {
            val startSample = (note.offsetMs * sampleRate) / 1000
            val noteDurationMs = totalDurationMs - note.offsetMs
            if (noteDurationMs <= 0) continue
            val noteSamples = (noteDurationMs * sampleRate) / 1000

            for (i in 0 until noteSamples) {
                val bufferIdx = startSample + i
                if (bufferIdx >= totalSamples) break

                val t = i.toDouble() / sampleRate

                // Instant attack (3ms) to avoid clicking, followed by exponential metallic decay
                val attack = if (t < 0.003) (t / 0.003) else 1.0
                val decay = exp(-t / note.decayTau)
                val envelope = attack * decay

                // Harmonic bell overtones: fundamental + 2.76x + 5.40x overtone
                val fundamental = sin(2.0 * Math.PI * note.freq * t)
                val overtone1 = 0.30 * sin(2.0 * Math.PI * (note.freq * 2.76) * t)
                val overtone2 = 0.12 * sin(2.0 * Math.PI * (note.freq * 5.40) * t)

                val wave = (fundamental + overtone1 + overtone2) * envelope * note.amplitude
                buffer[bufferIdx] += wave.toFloat()
            }
        }

        // Normalize & scale to 16-bit PCM shorts without clipping
        var maxPeak = 0f
        for (sample in buffer) {
            val absVal = kotlin.math.abs(sample)
            if (absVal > maxPeak) {
                maxPeak = absVal
            }
        }

        val normFactor = if (maxPeak > 0f) {
            (Short.MAX_VALUE * volume.coerceIn(0.1f, 1.0f) * 0.90f) / maxPeak
        } else {
            0f
        }

        val pcm = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val scaled = (buffer[i] * normFactor).toInt()
            pcm[i] = scaled.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        return pcm
    }

    /**
     * Synthesizes a single high-pitched sparkle chime (e.g. for checkpoint or star hit).
     */
    fun synthesizeSingleChime(
        frequency: Float = 2093.00f, // C7
        durationMs: Int = 180,
        sampleRate: Int = DEFAULT_SAMPLE_RATE,
        volume: Float = 0.85f
    ): ShortArray {
        val totalSamples = (durationMs * sampleRate) / 1000
        val pcm = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val attack = if (t < 0.002) (t / 0.002) else 1.0
            val decay = exp(-t / 0.14)
            val envelope = attack * decay

            val fundamental = sin(2.0 * Math.PI * frequency * t)
            val overtone = 0.28 * sin(2.0 * Math.PI * (frequency * 2.76) * t)
            val wave = (fundamental + overtone) * envelope

            val sampleVal = (wave * Short.MAX_VALUE * 0.65f * volume.coerceIn(0f, 1f)).toInt()
            pcm[i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        return pcm
    }
}
