package com.example.core.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

/**
 * Robust helper for sharing text, certificates, and artwork safely.
 *
 * Guarantees zero crashes on all devices and emulators:
 * - Adds Intent.FLAG_ACTIVITY_NEW_TASK to chooser intents
 * - Catches ActivityNotFoundException and other exceptions gracefully
 * - Automatically copies content to clipboard as an immediate safe fallback
 * - Provides user-friendly feedback via Toast
 */
object ShareHelper {

    private const val TAG = "ShareHelper"

    /**
     * Copies text to the system clipboard and optionally shows a confirmation Toast.
     */
    fun copyToClipboard(
        context: Context,
        label: String,
        text: String,
        showToast: Boolean = true
    ): Boolean {
        return try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            if (clipboard != null) {
                val clip = ClipData.newPlainText(label, text)
                clipboard.setPrimaryClip(clip)
                if (showToast) {
                    Toast.makeText(context, "Copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                Log.w(TAG, "ClipboardManager service not available")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to copy to clipboard: ${e.message}", e)
            false
        }
    }

    /**
     * Safely launches the system share chooser for text content.
     * Always copies to clipboard first so user can paste immediately anywhere,
     * and wraps startActivity in a try-catch to prevent crashes when no sharing app exists.
     */
    fun shareText(
        context: Context,
        subject: String,
        body: String,
        chooserTitle: String = "Share",
        fallbackMessage: String = "Copied to clipboard! 📋 You can paste and share it in any app."
    ) {
        // 1. Copy to clipboard first as guaranteed fallback
        copyToClipboard(context, subject, body, showToast = false)

        // 2. Launch system share sheet safely
        try {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }

            val chooserIntent = Intent.createChooser(sendIntent, chooserTitle).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(chooserIntent)
        } catch (e: Exception) {
            Log.w(TAG, "Share chooser failed or no app installed: ${e.message}", e)
            Toast.makeText(context, fallbackMessage, Toast.LENGTH_LONG).show()
        }
    }
}
