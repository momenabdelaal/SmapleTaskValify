package com.valify.presentation.selfie

import android.content.Context
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.valify.domain.use_case.CaptureSelfie
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SelfieViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val captureSelfie: CaptureSelfie
) : ViewModel() {

    private val _state = mutableStateOf(SelfieState())
    val state: State<SelfieState> = _state

    private val faceDetector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
    )

    fun startSmileDetection(imageCapture: ImageCapture) {
        viewModelScope.launch {
            while (!state.value.registrationComplete) {
                try {
                    // Capture image for analysis
                    val file = File(context.cacheDir, "analysis.jpg")
                    imageCapture.takePicture(
                        ImageCapture.OutputFileOptions.Builder(file).build(),
                        context.mainExecutor,
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                val image = InputImage.fromFilePath(context, Uri.fromFile(file))
                                faceDetector.process(image)
                                    .addOnSuccessListener { faces ->
                                        faces.firstOrNull()?.let { face ->
                                            if (face.smilingProbability != null && face.smilingProbability!! > 0.8) {
                                                _state.value = state.value.copy(isSmileDetected = true)
                                                captureAndSaveSelfie(imageCapture)
                                            }
                                        }
                                    }
                            }

                            override fun onError(exception: ImageCaptureException) {
                                _state.value = state.value.copy(
                                    error = "Failed to analyze image: ${exception.message}"
                                )
                            }
                        }
                    )
                    delay(1000) // Check every second
                } catch (e: Exception) {
                    _state.value = state.value.copy(
                        error = "Error during smile detection: ${e.message}"
                    )
                }
            }
        }
    }

    private fun captureAndSaveSelfie(imageCapture: ImageCapture) {
        _state.value = state.value.copy(isLoading = true)
        val photoFile = File(context.filesDir, "selfie_${System.currentTimeMillis()}.jpg")

        imageCapture.takePicture(
            ImageCapture.OutputFileOptions.Builder(photoFile).build(),
            context.mainExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    viewModelScope.launch {
                        try {
                            captureSelfie(state.value.registrationId, photoFile.absolutePath)
                            _state.value = state.value.copy(
                                isLoading = false,
                                registrationComplete = true
                            )
                        } catch (e: Exception) {
                            _state.value = state.value.copy(
                                isLoading = false,
                                error = "Failed to save selfie: ${e.message}"
                            )
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    _state.value = state.value.copy(
                        isLoading = false,
                        error = "Failed to capture selfie: ${exception.message}"
                    )
                }
            }
        )
    }
}
