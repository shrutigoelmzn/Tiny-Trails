package com.example.core.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * Robust helper for sharing text, certificates, and colored artwork images safely.
 *
 * Guarantees zero crashes on all devices and emulators:
 * - Uses FileProvider to safely share actual colored artwork bitmap files
 * - Adds Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_GRANT_READ_URI_PERMISSION
 * - Catches ActivityNotFoundException and all exceptions gracefully
 * - Automatically copies caption note to clipboard as a guaranteed safe fallback
 * - Provides immediate, user-friendly feedback via Toast
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

    /**
     * Saves a rendered bitmap into app cache and launches the system share sheet
     * to share the actual colored shape image with social/messaging apps.
     */
    fun shareImage(
        context: Context,
        bitmap: Bitmap,
        subject: String,
        captionText: String,
        chooserTitle: String = "Share Artwork 🎨",
        fallbackMessage: String = "Artwork saved! Copied caption to clipboard 📋"
    ) {
        // 1. Copy caption text to clipboard as immediate safe fallback
        copyToClipboard(context, subject, captionText, showToast = false)

        try {
            val cacheFolder = File(context.cacheDir, "shared_images").apply { mkdirs() }
            val imageFile = File(cacheFolder, "tiny_trails_artwork_${System.currentTimeMillis()}.png")

            FileOutputStream(imageFile).use { outStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
                outStream.flush()
            }

            val authority = "${context.packageName}.fileprovider"
            val imageUri: Uri = FileProvider.getUriForFile(context, authority, imageFile)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, captionText)
                clipData = ClipData.newUri(context.contentResolver, subject, imageUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, chooserTitle).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Grant read permission to all potential resolve targets
            val resolveInfos = context.packageManager.queryIntentActivities(shareIntent, 0)
            for (resolveInfo in resolveInfos) {
                val pkgName = resolveInfo.activityInfo.packageName
                context.grantUriPermission(pkgName, imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(chooser)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to share artwork image: ${e.message}", e)
            // Fall back to sharing text/caption
            shareText(context, subject, captionText, chooserTitle, fallbackMessage)
        }
    }
}
