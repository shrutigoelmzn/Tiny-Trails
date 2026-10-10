package com.example.core.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset

/**
 * Celebratory particle effect that displays a colorful particle explosion
 * and celebratory cascade using Jetpack Compose Animation.
 */
@Composable
fun CelebratoryParticleEffect(
    modifier: Modifier = Modifier,
    isTriggered: Boolean = true,
    particleCount: Int = 120,
    epicenter: Offset? = null,
    triggerKey: Any? = Unit
) {
    ParticleExplosionEffect(
        modifier = modifier,
        isTriggered = isTriggered,
        triggerKey = triggerKey,
        particleCount = particleCount,
        epicenter = epicenter,
        showShockwave = true,
        enableTapBurst = true
    )
}
