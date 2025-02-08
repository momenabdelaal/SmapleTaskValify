package com.valify.registrationsdk.domain.camera

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

interface CameraManager {
    fun startCamera()
    fun stopCamera()
    suspend fun capturePhoto(): Result<Bitmap>
    fun getCameraState(): Flow<CameraState>
}

sealed class CameraState {
    data object Ready : CameraState()
    data object Loading : CameraState()
    data class Error(val message: String) : CameraState()
}
