package com.example.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import com.example.data.model.CharacterContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

enum class AudioPromptKey {
    WATCH_DEMO,
    NOW_YOUR_TURN,
    START_TRACING,
    GREAT_JOB,
    SUPERSTAR,
    TRY_AGAIN,
    ACTIVITY_COMPLETE,
    COUNT_OBJECTS,
    POP_BALLOONS,
    FIND_OBJECT,
    MATCH_CARDS,
    FEED_ANIMAL,
    TAP_CORRECT,
    GARDEN_WELCOME,
    STICKER_COLLECTION
}

/**
 * SoundManager handles sound effects, musical celebration chimes, and connects
 * to VoiceManager and ChildVoiceExplainer for authentic, joyful child-voiced narration.
 */
class SoundManager private constructor(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var sfxEnabled = true
    private var masterVolume = 0.85f

    private val voiceManager by lazy { VoiceManager.getInstance(context) }

    fun setSfxEnabled(enabled: Boolean) {
        sfxEnabled = enabled
    }

    fun isSfxEnabled(): Boolean = sfxEnabled

    fun setVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
        voiceManager.setVolume(masterVolume)
    }

    fun getVolume(): Float = masterVolume

    /**
     * Speaks text using the high-fidelity child voice synthesizer.
     */
    fun speak(text: String, isHindi: Boolean = false) {
        if (!sfxEnabled) return
        voiceManager.speakChildVoice(text, isHindi = isHindi)
    }

    /**
     * Explains a character (letter, number, or shape/stroke) in an enthusiastic,
     * warm child-like voice accompanied by a sparkling musical tone.
     */
    fun explainCharacter(character: CharacterContent, isHindi: Boolean = false) {
        if (!sfxEnabled) return
        scope.launch {
            playToneSparkle()
            delay(120)
            val explanation = ChildVoiceExplainer.explainCharacter(character, isHindi = isHindi)
            speak(explanation, isHindi = isHindi)
        }
    }

    /**
     * Explains a milestone achievement badge in friendly toddler terms.
     */
    fun explainMilestoneBadge(title: String, isUnlocked: Boolean) {
        if (!sfxEnabled) return
        val text = ChildVoiceExplainer.getBadgeExplanation(title, isUnlocked)
        if (isUnlocked) {
            playToneSparkle()
        } else {
            playTonePop()
        }
        speak(text)
    }

    /**
     * Explains the user's progress stars count in a cheering child voice.
     */
    fun explainProgress(totalStars: Int) {
        if (!sfxEnabled) return
        val text = ChildVoiceExplainer.getProgressSummaryAudio(totalStars)
        speak(text)
    }

    /**
     * Welcoming explanation for the learning garden.
     */
    fun explainGarden() {
        if (!sfxEnabled) return
        playBabyChirp()
        speak(ChildVoiceExplainer.getGardenWelcomeAudio())
    }

    /**
     * Explains the diploma certificate in an excited child voice.
     */
    fun explainCertificate(childName: String = "superstar") {
        if (!sfxEnabled) return
        playToneSparkle()
        speak(ChildVoiceExplainer.getCertificateAudio(childName))
    }

    /**
     * Explains the creative coloring studio.
     */
    fun explainColoringStudio() {
        if (!sfxEnabled) return
        playToneSparkle()
        speak(ChildVoiceExplainer.getColoringStudioAudio())
    }

    /**
     * Speaks Pip the pet's reaction aloud in a cute child pet voice.
     */
    fun speakPetReaction(action: String) {
        if (!sfxEnabled) return
        val text = ChildVoiceExplainer.getPetReaction(action)
        speak(text)
    }

    /**
     * Speaks interactive prompts using child-voiced dialogue.
     */
    fun playPrompt(key: AudioPromptKey, isHindi: Boolean = false) {
        val text = ChildVoiceExplainer.getPromptText(key, isHindi)

        if (key == AudioPromptKey.GREAT_JOB || key == AudioPromptKey.SUPERSTAR || key == AudioPromptKey.ACTIVITY_COMPLETE) {
            playBabyChirp()
        } else if (key == AudioPromptKey.WATCH_DEMO || key == AudioPromptKey.NOW_YOUR_TURN) {
            playToneSparkle()
        }

        speak(text, isHindi)
    }

    fun playCharacter(displayValue: String, exampleWord: String? = null) {
        val speech = if (exampleWord != null) "$displayValue! $exampleWord!" else "$displayValue!"
        speak(speech)
    }

    fun playSticker(displayName: String) {
        speak("$displayName!")
    }

    @Volatile
    private var cachedCompletionChimes: ShortArray? = null

    /**
     * Triggers friendly, joyful, rewarding audio feedback when a child completes
     * a character trace.
     */
    fun playCharacterTraceCompletion(
        displayValue: String,
        stars: Int = 3,
        exampleWord: String? = null,
        isHindi: Boolean = false
    ) {
        if (!sfxEnabled) return
        scope.launch {
            // Step 1: Sparkling harmonic celebration fanfare (pentatonic chime cascade)
            val pcm = ChimeSynthesizer.synthesizeCelebrationFanfare(
                stars = stars,
                volume = masterVolume
            )
            playPcmAudio(pcm, sampleRate = ChimeSynthesizer.DEFAULT_SAMPLE_RATE)
            playBabyChirp()

            // Step 2: Natural acoustic decay delay so bell chimes ring out
            delay(520)

            // Step 3: Enthusiastic, child-like spoken praise tailored to the character & stars
            val praise = getCelebratoryPraise(
                displayValue = displayValue,
                stars = stars,
                exampleWord = exampleWord,
                isHindi = isHindi
            )
            speak(praise, isHindi = isHindi)
        }
    }

    fun getCelebratoryPraise(
        displayValue: String,
        stars: Int = 3,
        exampleWord: String? = null,
        isHindi: Boolean = false
    ): String {
        return ChildVoiceExplainer.getCelebratoryPraise(displayValue, stars, exampleWord, isHindi)
    }

    /**
     * Plays a high-pitched sparkle chime when a star badge is tapped or awarded.
     */
    fun playStarEarnedSound(starIndex: Int) {
        if (!sfxEnabled) return
        val freq = when (starIndex) {
            0 -> 1318.51f // E6
            1 -> 1760.00f // A6
            else -> 2093.00f // C7
        }
        playChimeSparkle(frequency = freq)
    }

    /**
     * Plays short, high-pitched, child-friendly chime sounds when a tracing session
     * is successfully completed.
     */
    fun playCompletionChimes() {
        if (!sfxEnabled) return
        scope.launch {
            val pcm = cachedCompletionChimes ?: ChimeSynthesizer.synthesizeCompletionChimes(
                volume = masterVolume
            ).also { cachedCompletionChimes = it }
            playPcmAudio(pcm, sampleRate = ChimeSynthesizer.DEFAULT_SAMPLE_RATE)
        }
    }

    /**
     * Plays a single short, sparkling high-pitched chime note.
     */
    fun playChimeSparkle(frequency: Float = 2093.00f) {
        if (!sfxEnabled) return
        scope.launch {
            val pcm = ChimeSynthesizer.synthesizeSingleChime(
                frequency = frequency,
                volume = masterVolume
            )
            playPcmAudio(pcm, sampleRate = ChimeSynthesizer.DEFAULT_SAMPLE_RATE)
        }
    }

    fun playToneSuccess() {
        if (!sfxEnabled) return
        playCompletionChimes()
    }

    fun playToneSparkle() {
        if (!sfxEnabled) return
        scope.launch {
            playSynthesizedTone(frequency = 1318.51f, durationMs = 60) // E6
            playSynthesizedTone(frequency = 1760.00f, durationMs = 80) // A6
        }
    }

    fun playTonePop() {
        if (!sfxEnabled) return
        scope.launch {
            playSynthesizedTone(frequency = 820.0f, durationMs = 50)
        }
    }

    fun playToneGentlePulse() {
        if (!sfxEnabled) return
        scope.launch {
            playSynthesizedTone(frequency = 440.0f, durationMs = 70)
        }
    }

    fun playButtonTap() {
        playTonePop()
    }

    fun playTouchFeedback() {
        playToneGentlePulse()
    }

    fun playStarEarned() {
        playStarEarnedSound(2)
    }

    fun playCorrectChime() {
        playToneSparkle()
    }

    fun playChime() {
        playToneSparkle()
    }

    fun playBabyChirp() {
        if (!sfxEnabled) return
        scope.launch {
            playSynthesizedTone(frequency = 1200.0f, durationMs = 45)
            playSynthesizedTone(frequency = 1500.0f, durationMs = 45)
            playSynthesizedTone(frequency = 1800.0f, durationMs = 60)
        }
    }

    private fun playSynthesizedTone(frequency: Float, durationMs: Int) {
        try {
            val sampleRate = 22050
            val numSamples = (durationMs * sampleRate) / 1000
            val sample = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val time = i.toDouble() / sampleRate
                val envelope = when {
                    i < numSamples * 0.10 -> i / (numSamples * 0.10)
                    i > numSamples * 0.65 -> (numSamples - i) / (numSamples * 0.35)
                    else -> 1.0
                }
                val angle = 2.0 * Math.PI * frequency * time
                sample[i] = (sin(angle) * envelope * Short.MAX_VALUE * 0.30 * masterVolume).toInt().toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(numSamples * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(sample, 0, numSamples)
            audioTrack.play()
            Thread.sleep(durationMs.toLong() + 15)
            audioTrack.release()
        } catch (e: Exception) {
            Log.e("SoundManager", "AudioTrack synthesis error", e)
        }
    }

    private fun playPcmAudio(pcm: ShortArray, sampleRate: Int = 44100) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(pcm.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(pcm, 0, pcm.size)
            audioTrack.play()
            val durationMs = (pcm.size * 1000L) / sampleRate
            Thread.sleep(durationMs + 20)
            audioTrack.release()
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing PCM audio", e)
        }
    }

    fun shutdown() {
        voiceManager.shutdown()
    }

    companion object {
        @Volatile
        private var INSTANCE: SoundManager? = null

        fun getInstance(context: Context): SoundManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SoundManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
