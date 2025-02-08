package com.valify.presentation.selfie

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.valify.domain.use_case.CaptureSelfie
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

data class SelfieState(
    val registrationId: Long = 0L,
    val isLoading: Boolean = false,
    val error: String? = null,
    val registrationComplete: Boolean = false,
    val capturedImageUri: Uri? = null
)

@HiltViewModel
class SelfieViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val captureSelfie: CaptureSelfie,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val registrationId: Long = checkNotNull(savedStateHandle.get<Long>("registrationId"))
    private val _state = mutableStateOf(SelfieState(registrationId = registrationId))
    val state: State<SelfieState> = _state

    private val faceDetector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.15f)
            .build()
    )

    @RequiresApi(Build.VERSION_CODES.P)
    fun capturePhoto(imageCapture: ImageCapture) {
        _state.value = state.value.copy(isLoading = true, error = null)
        
        viewModelScope.launch(Dispatchers.Main + CoroutineExceptionHandler { _, throwable ->
            _state.value = state.value.copy(
                isLoading = false,
                error = "Failed to process photo: ${throwable.message}"
            )
        }) {
            try {
                val photoFile = File(context.filesDir, "selfie_${System.currentTimeMillis()}.jpg")
                takePhoto(imageCapture, photoFile)
                
                val image = InputImage.fromFilePath(context, Uri.fromFile(photoFile))
                val faces = detectFaces(image)

                if (faces.isEmpty()) {
                    _state.value = state.value.copy(
                        isLoading = false,
                        error = "No face detected. Please look at the camera and try again."
                    )
                    photoFile.delete()
                    return@launch
                }

                val face = faces.first()
                val smileProb = face.smilingProbability ?: 0f

                if (smileProb < 0.5f) {
                    _state.value = state.value.copy(
                        isLoading = false,
                        error = "Please smile and try again!"
                    )
                    photoFile.delete()
                    return@launch
                }

                withContext(Dispatchers.IO) {
                    captureSelfie(registrationId, photoFile.absolutePath)
                }
                
                _state.value = state.value.copy(
                    isLoading = false,
                    registrationComplete = true,
                    error = null,
                    capturedImageUri = Uri.fromFile(photoFile)
                )
            } catch (e: Exception) {
                _state.value = state.value.copy(
                    isLoading = false,
                    error = when (e) {
                        is ImageCaptureException -> "Failed to capture photo: Camera may be in use"
                        is SecurityException -> "Permission denied to access camera or storage"
                        else -> "Failed to process photo: ${e.message}"
                    }
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private suspend fun takePhoto(imageCapture: ImageCapture, file: File) = suspendCoroutine { continuation ->
        try {
            imageCapture.takePicture(
                ImageCapture.OutputFileOptions.Builder(file).build(),
                context.mainExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        continuation.resume(Unit)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        continuation.resumeWithException(exception)
                    }
                }
            )
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }

    private suspend fun detectFaces(image: InputImage) = suspendCoroutine<List<com.google.mlkit.vision.face.Face>> { continuation ->
        try {
            faceDetector.process(image)
                .addOnSuccessListener { faces ->
                    continuation.resume(faces)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }

    override fun onCleared() {
        super.onCleared()
        faceDetector.close()
    }
}
