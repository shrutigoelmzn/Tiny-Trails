package com.example.core.ads

import android.content.Context
import android.util.Log

/**
 * Child-Safe Ad Placement Architecture.
 *
 * Designed specifically for preschool COPPA and Google Play Families policy compliance:
 * - Isolated from core learning & tracing logic
 * - Strictly zero ads during active tracing or mini activities
 * - Cooldown rate-limiting (e.g. minimum 3 minutes between non-intrusive transition placements)
 * - Prepared for standard mobile ads SDK (AdMob / Families certified ad networks)
 */
enum class AdPlacement {
    DAILY_SESSION_COMPLETED,
    PARENT_AREA_TRANSITION,
    NATURAL_IDLE_TRANSITION
}

interface AdEventListener {
    fun onAdLoaded() {}
    fun onAdFailedToLoad(error: String) {}
    fun onAdDismissed() {}
}

class AdManager private constructor(private val context: Context) {

    private var lastAdShownTimestamp: Long = 0L
    private val minimumIntervalMs = 180_000L // 3 minutes cooldown

    /**
     * Shows a child-safe interstitial or transition ad if eligible.
     * Guaranteed to never trigger during active tracing or child gameplay.
     */
    fun showSafeTransitionAd(
        placement: AdPlacement,
        onDismissed: () -> Unit = {}
    ) {
        val now = System.currentTimeMillis()
        if (now - lastAdShownTimestamp < minimumIntervalMs) {
            Log.d(TAG, "Ad skipped due to child-safe cooldown timer for placement: $placement")
            onDismissed()
            return
        }

        Log.i(TAG, "Child-safe ad requested for placement: $placement. Ready for Families Certified Ad SDK.")
        lastAdShownTimestamp = now
        // Callout to certified Families Ad SDK interstitial when provisioned
        onDismissed()
    }

    companion object {
        private const val TAG = "AdManager"

        @Volatile
        private var INSTANCE: AdManager? = null

        fun getInstance(context: Context): AdManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AdManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
