package com.example.feature.tracing

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.audio.AudioPromptKey
import com.example.core.audio.SoundManager
import com.example.data.content.PrototypeCharacters
import com.example.data.model.CharacterContent
import com.example.domain.tracing.NormalizedPoint
import com.example.domain.tracing.TraceCharacterDef
import com.example.domain.tracing.TraceStroke
import com.example.domain.tracing.TraceValidator
import com.example.domain.tracing.TracingFeedback
import com.example.domain.tracing.TracingState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DemoState(
    val isPlaying: Boolean = false,
    val currentStrokeIndex: Int = 0,
    val progress: Float = 0f,
    val pointerPos: NormalizedPoint? = null
)

class TracingViewModel(application: Application) : AndroidViewModel(application) {

    private val soundManager = SoundManager.getInstance(application)
    private val progressRepo = com.example.data.repository.ProgressRepository.getInstance(application)
    private var validator: TraceValidator? = null
    private var demoJob: Job? = null
    private var currentCharacterContent: CharacterContent? = null

    private val _uiState = MutableStateFlow<TracingState?>(null)
    val uiState: StateFlow<TracingState?> = _uiState.asStateFlow()

    private val _demoState = MutableStateFlow(DemoState())
    val demoState: StateFlow<DemoState> = _demoState.asStateFlow()

    private val _traceUiState = MutableStateFlow<TraceUiState>(TraceUiState.Loading)
    val traceUiState: StateFlow<TraceUiState> = _traceUiState.asStateFlow()

    private val preferencesRepository = com.example.data.preferences.UserPreferencesRepository(application)
    private var isHindiLanguage = false

    private val _celebrationTriggerCount = MutableStateFlow(0)
    val celebrationTriggerCount: StateFlow<Int> = _celebrationTriggerCount.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.languageFlow.collect { lang ->
                isHindiLanguage = lang.equals("hi", ignoreCase = true)
            }
        }
    }

    fun loadCharacter(characterId: String) {
        _traceUiState.value = TraceUiState.Loading
        val content = PrototypeCharacters.findById(characterId) ?: PrototypeCharacters.NUMBER_1
        currentCharacterContent = content
        val def = content.traceDefinition
        val newValidator = TraceValidator(TracingState(characterDef = def))
        validator = newValidator
        _uiState.value = newValidator.getCurrentState()

        // Announce character and start automatic tracing demo
        soundManager.playCharacter(content.displayValue, content.exampleWord)
        startTracingDemo(def)
    }

    fun startTracingDemo(def: TraceCharacterDef? = null) {
        val characterDef = def ?: _uiState.value?.characterDef ?: return
        demoJob?.cancel()

        demoJob = viewModelScope.launch {
            _traceUiState.value = TraceUiState.Demo
            _demoState.value = DemoState(
                isPlaying = true,
                currentStrokeIndex = 0,
                progress = 0f,
                pointerPos = characterDef.strokes.firstOrNull()?.startPoint
            )

            soundManager.playPrompt(AudioPromptKey.WATCH_DEMO)
            delay(400)

            for (strokeIdx in characterDef.strokes.indices) {
                val stroke = characterDef.strokes[strokeIdx]
                val durationMs = 1300L
                val steps = 40
                val stepDelay = durationMs / steps

                for (step in 0..steps) {
                    val progress = step / steps.toFloat()
                    val point = getPointAtFraction(stroke, progress)
                    _demoState.value = DemoState(
                        isPlaying = true,
                        currentStrokeIndex = strokeIdx,
                        progress = progress,
                        pointerPos = point
                    )
                    delay(stepDelay)
                }
                delay(300)
            }

            // Demo Finished: Reset character to clean user tracing state (user progress starts from 0)
            val newValidator = TraceValidator(TracingState(characterDef = characterDef))
            validator = newValidator
            _uiState.value = newValidator.getCurrentState()
            _demoState.value = DemoState(isPlaying = false)

            // Enable user input
            _traceUiState.value = TraceUiState.ReadyToTrace
            soundManager.playPrompt(AudioPromptKey.NOW_YOUR_TURN)
        }
    }

    fun skipDemo() {
        demoJob?.cancel()
        val characterDef = _uiState.value?.characterDef
        if (characterDef != null) {
            val newValidator = TraceValidator(TracingState(characterDef = characterDef))
            validator = newValidator
            _uiState.value = newValidator.getCurrentState()
        }
        _demoState.value = DemoState(isPlaying = false)
        _traceUiState.value = TraceUiState.ReadyToTrace
        soundManager.playPrompt(AudioPromptKey.NOW_YOUR_TURN)
    }

    fun replayDemo() {
        val current = _uiState.value ?: return
        // Cancel/reset any incomplete user trace
        val newValidator = TraceValidator(TracingState(characterDef = current.characterDef))
        validator = newValidator
        _uiState.value = newValidator.getCurrentState()
        // Temporarily disable tracing input and replay complete demonstration
        startTracingDemo(current.characterDef)
    }

    fun onTouchDown(point: NormalizedPoint) {
        val currentState = _traceUiState.value
        // Touch input is strictly accepted ONLY in ReadyToTrace or Tracing
        if (!currentState.isInputAllowed) return

        val v = validator ?: return
        val previousState = v.getCurrentState()
        val newState = v.onTouchDown(point)
        _uiState.value = newState

        if (newState.feedback == TracingFeedback.START_LOCKED) {
            _traceUiState.value = TraceUiState.Tracing
            if (previousState.feedback != TracingFeedback.START_LOCKED) {
                soundManager.playToneSparkle()
            }
        } else if (newState.feedback == TracingFeedback.WAITING_FOR_START) {
            soundManager.playToneGentlePulse()
        }
    }

    fun onTouchMove(point: NormalizedPoint) {
        val currentState = _traceUiState.value
        if (!currentState.isInputAllowed) return

        val v = validator ?: return
        val previousState = v.getCurrentState()
        val newState = v.onTouchMove(point)
        _uiState.value = newState

        if (newState.isCharacterCompleted) {
            _traceUiState.value = TraceUiState.Completed
            _celebrationTriggerCount.value += 1
            currentCharacterContent?.id?.let { id ->
                progressRepo.recordCompletion(id, newState.stars)
            }
            val displayVal = currentCharacterContent?.displayValue ?: ""
            val exampleWord = currentCharacterContent?.exampleWord
            soundManager.playCharacterTraceCompletion(
                displayValue = displayVal,
                stars = newState.stars,
                exampleWord = exampleWord,
                isHindi = isHindiLanguage
            )
        } else if (newState.completedStrokes.size > previousState.completedStrokes.size) {
            _traceUiState.value = TraceUiState.ReadyToTrace
            soundManager.playToneSparkle()
        } else if (newState.feedback == TracingFeedback.OFF_CORRIDOR && previousState.feedback != TracingFeedback.OFF_CORRIDOR) {
            soundManager.playToneGentlePulse()
        }
    }

    fun onTouchUp() {
        val currentState = _traceUiState.value
        if (!currentState.isInputAllowed) return

        val v = validator ?: return
        val previousState = v.getCurrentState()
        val newState = v.onTouchUp()
        _uiState.value = newState

        if (newState.isCharacterCompleted) {
            _traceUiState.value = TraceUiState.Completed
            _celebrationTriggerCount.value += 1
            currentCharacterContent?.id?.let { id ->
                progressRepo.recordCompletion(id, newState.stars)
            }
            val displayVal = currentCharacterContent?.displayValue ?: ""
            val exampleWord = currentCharacterContent?.exampleWord
            soundManager.playCharacterTraceCompletion(
                displayValue = displayVal,
                stars = newState.stars,
                exampleWord = exampleWord,
                isHindi = isHindiLanguage
            )
        } else {
            _traceUiState.value = TraceUiState.ReadyToTrace
            if (newState.completedStrokes.size > previousState.completedStrokes.size) {
                soundManager.playToneSparkle()
            }
        }
    }

    fun replayCelebration() {
        _celebrationTriggerCount.value += 1
        val displayVal = currentCharacterContent?.displayValue ?: ""
        val exampleWord = currentCharacterContent?.exampleWord
        val stars = _uiState.value?.stars ?: 3
        soundManager.playCharacterTraceCompletion(
            displayValue = displayVal,
            stars = stars,
            exampleWord = exampleWord,
            isHindi = isHindiLanguage
        )
    }

    fun playStarSound(starIndex: Int) {
        soundManager.playStarEarnedSound(starIndex)
    }

    fun retry() {
        val current = _uiState.value ?: return
        demoJob?.cancel()
        _demoState.value = DemoState(isPlaying = false)
        val newValidator = TraceValidator(TracingState(characterDef = current.characterDef))
        validator = newValidator
        _uiState.value = newValidator.getCurrentState()
        _traceUiState.value = TraceUiState.ReadyToTrace
        soundManager.playTonePop()
    }

    fun speakCharacter() {
        val current = _uiState.value ?: return
        val content = PrototypeCharacters.findById(current.characterDef.id)
            ?: PrototypeCharacters.ALL_ITEMS.firstOrNull { it.traceDefinition.id == current.characterDef.id }
        if (content != null) {
            soundManager.playCharacter(content.displayValue, content.exampleWord)
        }
    }

    private fun getPointAtFraction(stroke: TraceStroke, fraction: Float): NormalizedPoint {
        val checkpoints = stroke.checkpoints
        if (checkpoints.isEmpty()) return stroke.startPoint
        val targetIdx = (fraction.coerceIn(0f, 1f) * (checkpoints.size - 1)).toInt()
        return checkpoints[targetIdx.coerceIn(0, checkpoints.size - 1)]
    }

    override fun onCleared() {
        super.onCleared()
        demoJob?.cancel()
    }
}
