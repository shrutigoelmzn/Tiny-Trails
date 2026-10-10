package com.example.feature.tracing

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.core.audio.ChimeSynthesizer
import com.example.core.audio.SoundManager
import com.example.data.content.PrototypeCharacters
import com.example.domain.tracing.NormalizedPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TracingCelebrationTest {

    @Test
    fun testReplayCelebration_incrementsCelebrationTriggerCount() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = TracingViewModel(app)

        viewModel.loadCharacter(PrototypeCharacters.NUMBER_1.id)
        val initialCount = viewModel.celebrationTriggerCount.value
        assertEquals(0, initialCount)

        viewModel.replayCelebration()
        assertEquals(1, viewModel.celebrationTriggerCount.value)

        viewModel.replayCelebration()
        assertEquals(2, viewModel.celebrationTriggerCount.value)
    }

    @Test
    fun testSoundManager_providesJoyfulCelebratoryFeedbackOnTraceCompletion() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val soundManager = SoundManager.getInstance(app)

        // 3-star praise for Letter A
        val englishPraise = soundManager.getCelebratoryPraise(
            displayValue = "A",
            stars = 3,
            exampleWord = "Apple",
            isHindi = false
        )
        assertNotNull(englishPraise)
        assertTrue("English praise must celebrate the character", englishPraise.contains("A"))

        // Hindi praise for Number 1
        val hindiPraise = soundManager.getCelebratoryPraise(
            displayValue = "१",
            stars = 3,
            isHindi = true
        )
        assertNotNull(hindiPraise)
        assertTrue("Hindi praise must contain cheerful congratulation", hindiPraise.isNotBlank())
    }

    @Test
    fun testChimeSynthesizer_synthesizesCelebrationFanfareBuffer() {
        val fanfarePcm = ChimeSynthesizer.synthesizeCelebrationFanfare(
            stars = 3,
            sampleRate = 44100,
            totalDurationMs = 900,
            volume = 0.85f
        )
        assertNotNull(fanfarePcm)
        assertTrue(fanfarePcm.isNotEmpty())
        assertEquals((900 * 44100) / 1000, fanfarePcm.size)
    }
}
