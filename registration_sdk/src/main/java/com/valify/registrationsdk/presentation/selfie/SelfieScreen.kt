package com.valify.registrationsdk.presentation.selfie

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executor

private const val TAG = "SelfieScreen"
private const val SMILE_THRESHOLD = 0.8f

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun SelfieScreen(
    registrationId: Long,
    onNavigateToRegister: (Long) -> Unit,
    viewModel: SelfieViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = viewModel.state.value
    val scope = rememberCoroutineScope()

    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val mainExecutor = remember { ContextCompat.getMainExecutor(context) }
    
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    // Initialize face detector with optimal settings
    val faceDetector = remember {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.2f) // Adjust based on your needs
            .build()
        FaceDetection.getClient(options)
    }

    LaunchedEffect(registrationId) {
        viewModel.setRegistrationId(registrationId)
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    // Show error in UI
                }
                is UiEvent.NavigateToRegister -> {
                    onNavigateToRegister(event.registrationId)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main) {
            try {
                val future = ProcessCameraProvider.getInstance(context)
                cameraProvider = future.get()
                if (previewView != null) {
                    imageCapture = setupCamera(lifecycleOwner, mainExecutor, cameraProvider!!, previewView!!, faceDetector) { bitmap ->
                        scope.launch {
                            viewModel.onEvent(SelfieEvent.OnPhotoCapture(bitmap))
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize camera", e)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                cameraProvider?.unbindAll()
            } catch (e: Exception) {
                Log.e(TAG, "Error cleaning up camera", e)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!cameraPermissionState.status.isGranted) {
            PermissionRequest {
                cameraPermissionState.launchPermissionRequest()
            }
        } else {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (state.savedImage != null) {
                    AsyncImage(
                        model = state.savedImage,
                        contentDescription = "Saved Selfie",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator()
                        } else {
                            Button(
                                onClick = { viewModel.onEvent(SelfieEvent.OnSaveAndContinue) },
                                modifier = Modifier.fillMaxWidth(0.8f),
                                enabled = state.savedImage != null && !state.isLoading
                            ) {
                                Text("Save and Continue")
                            }
                            
                            Button(
                                onClick = { viewModel.onEvent(SelfieEvent.OnRetakePhoto) },
                                modifier = Modifier.fillMaxWidth(0.8f),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.secondary
                                )
                            ) {
                                Text("Retake Photo")
                            }
                        }
                    }
                } else {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            PreviewView(ctx).apply {
                                implementationMode = PreviewView.ImplementationMode.PERFORMANCE
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            }.also { view -> 
                                previewView = view
                                cameraProvider?.let { provider ->
                                    imageCapture = setupCamera(lifecycleOwner, mainExecutor, provider, view, faceDetector) { bitmap ->
                                        scope.launch {
                                            viewModel.onEvent(SelfieEvent.OnPhotoCapture(bitmap))
                                        }
                                    }
                                }
                            }
                        }
                    )

                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    if (state.error != null) {
                        Text(
                            text = state.error,
                            color = MaterialTheme.colors.error,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Smile for the camera!",
                            style = MaterialTheme.typography.h6,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Position your face in the center and smile naturally",
                            style = MaterialTheme.typography.body2,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionRequest(
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Camera permission is required to take a selfie",
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            "We need camera access to capture your selfie for registration",
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Button(
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Grant Camera Permission")
        }
    }
}

private fun setupCamera(
    lifecycleOwner: LifecycleOwner,
    executor: Executor,
    cameraProvider: ProcessCameraProvider,
    previewView: PreviewView,
    faceDetector: FaceDetector,
    onSmileDetected: (Bitmap) -> Unit
): ImageCapture {
    cameraProvider.unbindAll()

    val preview = Preview.Builder()
        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
        .build()
        .also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

    val imageCapture = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
        .build()

    val imageAnalysis = ImageAnalysis.Builder()
        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()

    var isCapturing = false

    imageAnalysis.setAnalyzer(executor) { imageProxy ->
        val mediaImage = imageProxy.image
        if (mediaImage != null && !isCapturing) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            
            faceDetector.process(image)
                .addOnSuccessListener { faces ->
                    faces.firstOrNull()?.let { face ->
                        if (face.smilingProbability != null && face.smilingProbability!! > SMILE_THRESHOLD) {
                            if (!isCapturing) {
                                isCapturing = true
                                imageProxy.close()
                                imageCapture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
                                    override fun onCaptureSuccess(image: ImageProxy) {
                                        try {
                                            val bitmap = image.toBitmap()
                                            val matrix = Matrix()
                                            
                                            matrix.postRotate(
                                                when (image.imageInfo.rotationDegrees) {
                                                    90 -> 90f
                                                    270 -> 270f
                                                    180 -> 180f
                                                    else -> 0f
                                                }
                                            )
                                            
                                            matrix.postScale(-1f, 1f)
                                            
                                            val rotatedBitmap = Bitmap.createBitmap(
                                                bitmap,
                                                0,
                                                0,
                                                bitmap.width,
                                                bitmap.height,
                                                matrix,
                                                true
                                            )
                                            
                                            onSmileDetected(rotatedBitmap)
                                            isCapturing = false
                                        } catch (e: Exception) {
                                            Log.e(TAG, "Error processing captured image", e)
                                            isCapturing = false
                                        } finally {
                                            image.close()
                                        }
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        Log.e(TAG, "Failed to capture image", exception)
                                        isCapturing = false
                                    }
                                })
                                return@addOnSuccessListener
                            }
                        }
                    }
                    imageProxy.close()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Face detection failed", e)
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    try {
        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture,
            imageAnalysis
        )
    } catch (e: Exception) {
        Log.e(TAG, "Use case binding failed", e)
    }

    return imageCapture
}

private fun ImageProxy.toBitmap(): Bitmap {
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}
