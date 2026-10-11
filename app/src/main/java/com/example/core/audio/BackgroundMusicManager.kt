package com.example.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * BackgroundMusicManager generates and streams a soft, calming preschool music-box melody
 * in real-time using AudioTrack.
 *
 * It gives parents complete control to enable/disable background music and adjust volume.
 */
class BackgroundMusicManager private constructor(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var playbackJob: Job? = null
    private var audioTrack: AudioTrack? = null

    private var isMusicEnabled = true
    private val _isMusicEnabledFlow = kotlinx.coroutines.flow.MutableStateFlow(true)
    val isMusicEnabledFlow: kotlinx.coroutines.flow.StateFlow<Boolean> = _isMusicEnabledFlow

    private var musicVolume = 0.50f // Soft background level

    // Gentle, calming lullaby/nursery melody notes in Hertz (C major / Pentatonic)
    // Notes: C4, G4, A4, G4, F4, E4, D4, C4
    private val melodyNotes = floatArrayOf(
        261.63f, 392.00f, 440.00f, 392.00f,
        349.23f, 329.63f, 293.66f, 261.63f,
        392.00f, 349.23f, 329.63f, 293.66f,
        392.00f, 349.23f, 329.63f, 293.66f,
        261.63f, 392.00f, 440.00f, 392.00f,
        349.23f, 329.63f, 293.66f, 261.63f
    )
    private val noteDurationsMs = intArrayOf(
        500, 500, 500, 500,
        500, 500, 500, 1000,
        500, 500, 500, 500,
        500, 500, 500, 1000,
        500, 500, 500, 500,
        500, 500, 500, 1200
    )

    fun setMusicEnabled(enabled: Boolean) {
        if (isMusicEnabled == enabled) return
        isMusicEnabled = enabled
        _isMusicEnabledFlow.value = enabled
        if (enabled) {
            start()
        } else {
            stop()
        }
    }

    fun toggleMusic(): Boolean {
        val next = !isMusicEnabled
        setMusicEnabled(next)
        return next
    }

    fun isEnabled(): Boolean = isMusicEnabled

    fun setVolume(volume: Float) {
        musicVolume = volume.coerceIn(0.0f, 1.0f)
        try {
            audioTrack?.setVolume(musicVolume * 0.40f) // Keep background music subtle
        } catch (e: Exception) {
            Log.w(TAG, "Error setting music volume: ${e.message}")
        }
    }

    fun getVolume(): Float = musicVolume

    fun start() {
        if (!isMusicEnabled) return
        if (playbackJob?.isActive == true) return

        playbackJob = scope.launch {
            val sampleRate = 22050
            val channelConfig = AudioFormat.CHANNEL_OUT_MONO
            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
            val minBufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat)

            try {
                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(audioFormat)
                            .setSampleRate(sampleRate)
                            .setChannelMask(channelConfig)
                            .build()
                    )
                    .setBufferSizeInBytes(minBufferSize * 4)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack?.setVolume(musicVolume * 0.40f)
                audioTrack?.play()

                var noteIndex = 0

                while (isActive && isMusicEnabled) {
                    val freq = melodyNotes[noteIndex % melodyNotes.size]
                    val durationMs = noteDurationsMs[noteIndex % noteDurationsMs.size]
                    noteIndex++

                    val numSamples = (durationMs * sampleRate) / 1000
                    val buffer = ShortArray(numSamples)

                    for (i in 0 until numSamples) {
                        val t = i.toDouble() / sampleRate
                        // Soft music-box bell envelope (gentle attack, soft exponential decay)
                        val attack = (i.toDouble() / (sampleRate * 0.02)).coerceAtMost(1.0)
                        val decay = exp(-t * 3.5)
                        val envelope = attack * decay

                        // Fundamental tone + subtle 2nd harmonic for warm marimba/music box warmth
                        val s1 = sin(2.0 * PI * freq * t)
                        val s2 = 0.25 * sin(4.0 * PI * freq * t)
                        val sampleVal = (s1 + s2) * envelope * Short.MAX_VALUE * 0.25

                        buffer[i] = sampleVal.toInt().coerceIn(-32768, 32767).toShort()
                    }

                    audioTrack?.write(buffer, 0, buffer.size)

                    // Brief rest between notes
                    delay(40)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Music playback loop exception", e)
            } finally {
                cleanUpTrack()
            }
        }
    }

    fun stop() {
        playbackJob?.cancel()
        playbackJob = null
        cleanUpTrack()
    }

    private fun cleanUpTrack() {
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.release()
            audioTrack = null
        } catch (e: Exception) {
            Log.w(TAG, "Error cleaning up music track: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "BackgroundMusicManager"

        @Volatile
        private var INSTANCE: BackgroundMusicManager? = null

        fun getInstance(context: Context): BackgroundMusicManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BackgroundMusicManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
