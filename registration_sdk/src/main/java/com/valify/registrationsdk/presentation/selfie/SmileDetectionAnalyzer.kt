package com.valify.registrationsdk.presentation.selfie

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.ByteArrayOutputStream

class SmileDetectionAnalyzer(
    private val onSmileDetected: () -> Unit,
    private val onFaceQualityError: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setMinFaceSize(0.15f)
        .enableTracking()
        .build()

    private val detector = FaceDetection.getClient(options)
    private var isSmileDetected = false
    private var lastTrackingId: Int? = null
    private var consecutiveSmileFrames = 0
    private val requiredSmileFrames = 3

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null && !isSmileDetected) {
            try {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                detector.process(image)
                    .addOnSuccessListener { faces ->
                        if (faces.isEmpty()) {
                            onFaceQualityError("No face detected")
                            consecutiveSmileFrames = 0
                            return@addOnSuccessListener
                        }

                        if (faces.size > 1) {
                            onFaceQualityError("Multiple faces detected")
                            consecutiveSmileFrames = 0
                            return@addOnSuccessListener
                        }

                        val face = faces[0]
                        
                      
                        val smileProbability = face.smilingProbability ?: 0f
                        val isSmiling = smileProbability > 0.8f

                        if (isSmiling) {
                            consecutiveSmileFrames++
                            if (consecutiveSmileFrames >= requiredSmileFrames) {
                                isSmileDetected = true
                                onSmileDetected()
                            }
                        } else {
                            consecutiveSmileFrames = 0
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("SmileDetectionAnalyzer", "Face detection failed", e)
                        onFaceQualityError("Face detection failed: ${e.message}")
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } catch (e: Exception) {
                Log.e("SmileDetectionAnalyzer", "Error analyzing image", e)
                onFaceQualityError("Error analyzing image: ${e.message}")
                imageProxy.close()
            }
        } else {
            imageProxy.close()
        }
    }

    fun reset() {
        isSmileDetected = false
        consecutiveSmileFrames = 0
        lastTrackingId = null
    }
}
