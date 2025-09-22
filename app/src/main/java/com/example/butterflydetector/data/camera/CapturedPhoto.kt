package com.example.butterflydetector.data.camera

import android.graphics.Bitmap

/**
 * Model class representing a captured photo with metadata.
 */
data class CapturedPhoto(
    val id: String,
    val bitmap: Bitmap,
    val timestamp: Long
)
