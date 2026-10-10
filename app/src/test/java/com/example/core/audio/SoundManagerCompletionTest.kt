package com.example.core.audio

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SoundManagerCompletionTest {

    @Test
    fun testGetCelebratoryPraise_for3StarsEnglish_includesCharacterAndEncouragement() {
        val testDisplayValue = "A"
        val mockContext = ApplicationProvider.getApplicationContext<Context>()
        val soundManager = SoundManager.getInstance(mockContext)

        val praise = soundManager.getCelebratoryPraise(
            displayValue = testDisplayValue,
            stars = 3,
            exampleWord = "Apple",
            isHindi = false
        )

        assertNotNull(praise)
        assertTrue("Praise should be non-empty", praise.isNotBlank())
        assertTrue("3-star praise should contain character or star reference", praise.contains("A") || praise.contains("star", ignoreCase = true))
    }

    @Test
    fun testGetCelebratoryPraise_forHindi_returnsHindiPraise() {
        val mockContext = ApplicationProvider.getApplicationContext<Context>()
        val soundManager = SoundManager.getInstance(mockContext)

        val praise = soundManager.getCelebratoryPraise(
            displayValue = "3",
            stars = 3,
            isHindi = true
        )

        assertNotNull(praise)
        assertTrue("Hindi praise should contain Devanagari script", praise.any { it in '\u0900'..'\u097F' })
    }

    @Test
    fun testPlayCharacterTraceCompletion_whenSfxDisabled_doesNotCrash() {
        val mockContext = ApplicationProvider.getApplicationContext<Context>()
        val soundManager = SoundManager.getInstance(mockContext)
        soundManager.setSfxEnabled(false)

        assertFalse(soundManager.isSfxEnabled())
        // Should return early safely without throwing any exceptions
        soundManager.playCharacterTraceCompletion("B", 3)
        soundManager.setSfxEnabled(true)
        assertTrue(soundManager.isSfxEnabled())
    }

    @Test
    fun testPlayStarEarnedSound_runsSafely() {
        val mockContext = ApplicationProvider.getApplicationContext<Context>()
        val soundManager = SoundManager.getInstance(mockContext)

        soundManager.playStarEarnedSound(0)
        soundManager.playStarEarnedSound(1)
        soundManager.playStarEarnedSound(2)
    }
}
