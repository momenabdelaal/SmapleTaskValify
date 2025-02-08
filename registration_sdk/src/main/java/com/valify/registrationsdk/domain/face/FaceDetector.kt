package com.valify.registrationsdk.domain.face

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

interface FaceDetector {
    fun startDetection()
    fun stopDetection()
    fun detectSmile(image: Bitmap): Flow<SmileDetectionResult>
}

sealed class SmileDetectionResult {
    data class Success(
        val isSmiling: Boolean,
        val smileProbability: Float,
        val faceQuality: FaceQuality
    ) : SmileDetectionResult()
    data class Error(val message: String) : SmileDetectionResult()
}

data class FaceQuality(
    val isGoodQuality: Boolean,
    val brightness: Float,
    val sharpness: Float,
    val orientation: Float
)
