package com.valify.registrationsdk.presentation.selfie

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun SelfieScreen(
    registrationId: Long,
    onRegistrationComplete: () -> Unit,
    viewModel: SelfieViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val storagePermissionState = rememberPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val state = viewModel.state.value

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var cameraError by remember { mutableStateOf<String?>(null) }
    var showCamera by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = true) {
        cameraPermissionState.launchPermissionRequest()
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            storagePermissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(state.capturedImageUri) {
        if (state.capturedImageUri != null) {
            showCamera = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Take a Selfie",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (cameraPermissionState.status.isGranted && 
            (Build.VERSION.SDK_INT > Build.VERSION_CODES.P || storagePermissionState.status.isGranted)) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (showCamera) {
                    AndroidView(
                        factory = { context ->
                            PreviewView(context).apply {
                                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                        update = { previewView ->
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                            cameraProviderFuture.addListener({
                                try {
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build()

                                    val capture = ImageCapture.Builder()
                                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                        .build()
                                    imageCapture = capture

                                    val cameraSelector = CameraSelector.Builder()
                                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                                        .build()

                                    try {
                                        cameraProvider.unbindAll()
                                        cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            cameraSelector,
                                            preview,
                                            capture
                                        )
                                        preview.setSurfaceProvider(previewView.surfaceProvider)
                                        cameraError = null
                                    } catch (e: Exception) {
                                        Log.e("SelfieScreen", "Use case binding failed", e)
                                        cameraError = "Failed to initialize camera: ${e.message}"
                                    }
                                } catch (e: Exception) {
                                    Log.e("SelfieScreen", "Camera initialization failed", e)
                                    cameraError = "Failed to initialize camera: ${e.message}"
                                }
                            }, ContextCompat.getMainExecutor(context))
                        }
                    )
                } else {
                    state.capturedImageUri?.let { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = "Captured selfie",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

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
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    )
                }

                if (cameraError != null) {
                    Text(
                        text = cameraError!!,
                        color = MaterialTheme.colors.error,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (!showCamera) {
                    Button(
                        onClick = { showCamera = true },
                        enabled = !state.isLoading,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Retake")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = onRegistrationComplete,
                        enabled = !state.isLoading,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Done")
                    }
                } else {
                    Button(
                        onClick = {
                            imageCapture?.let { capture ->
                                viewModel.capturePhoto(capture)
                            } ?: run {
                                cameraError = "Camera is not ready yet. Please wait."
                            }
                        },
                        enabled = !state.isLoading && imageCapture != null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Take Photo")
                    }
                }
            }
        } else {
            Text(
                text = if (!cameraPermissionState.status.isGranted) 
                    "Camera permission is required to take a selfie"
                else 
                    "Storage permission is required to save photos",
                color = MaterialTheme.colors.error,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    LaunchedEffect(state.registrationComplete) {
        if (state.registrationComplete) {
            onRegistrationComplete()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                ProcessCameraProvider.getInstance(context).get()?.unbindAll()
                imageCapture = null
            } catch (e: Exception) {
                Log.e("SelfieScreen", "Error cleaning up camera", e)
            }
        }
    }
}
