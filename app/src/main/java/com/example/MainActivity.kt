package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.core.audio.BackgroundMusicManager
import com.example.core.audio.SoundManager
import com.example.core.audio.VoiceManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TinyTrailsApp()
        }
    }

    override fun onResume() {
        super.onResume()
        BackgroundMusicManager.getInstance(applicationContext).start()
    }

    override fun onPause() {
        super.onPause()
        BackgroundMusicManager.getInstance(applicationContext).stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        SoundManager.getInstance(applicationContext).shutdown()
        VoiceManager.getInstance(applicationContext).shutdown()
        BackgroundMusicManager.getInstance(applicationContext).stop()
    }
}
