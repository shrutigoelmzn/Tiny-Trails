package com.example.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "tiny_trails_settings")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val AUDIO_FEEDBACK_ENABLED = booleanPreferencesKey("audio_feedback_enabled")
        val BACKGROUND_MUSIC_ENABLED = booleanPreferencesKey("background_music_enabled")
        val APP_VOLUME = floatPreferencesKey("app_volume")
        val LANGUAGE = stringPreferencesKey("language")
    }

    val themeModeFlow: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        val name = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.HAPPY_GARDEN.name
        try {
            ThemeMode.valueOf(name)
        } catch (e: Exception) {
            ThemeMode.HAPPY_GARDEN
        }
    }

    val audioFeedbackEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.AUDIO_FEEDBACK_ENABLED] ?: true
    }

    val backgroundMusicEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.BACKGROUND_MUSIC_ENABLED] ?: true
    }

    val appVolumeFlow: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.APP_VOLUME] ?: 0.80f
    }

    val soundEnabledFlow: Flow<Boolean> = audioFeedbackEnabledFlow

    val languageFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LANGUAGE] ?: "en"
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    suspend fun setAudioFeedbackEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUDIO_FEEDBACK_ENABLED] = enabled
        }
    }

    suspend fun setBackgroundMusicEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BACKGROUND_MUSIC_ENABLED] = enabled
        }
    }

    suspend fun setAppVolume(volume: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_VOLUME] = volume.coerceIn(0.0f, 1.0f)
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        setAudioFeedbackEnabled(enabled)
    }

    suspend fun setLanguage(langCode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = langCode
        }
    }
}
