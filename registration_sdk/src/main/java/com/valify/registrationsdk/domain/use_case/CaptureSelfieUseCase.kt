package com.valify.registrationsdk.domain.use_case

import android.net.Uri
import com.valify.registrationsdk.domain.camera.CameraManager
import com.valify.registrationsdk.domain.face.FaceDetector
import com.valify.registrationsdk.domain.repository.SelfieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CaptureSelfieUseCase @Inject constructor(
    private val cameraManager: CameraManager,
    private val faceDetector: FaceDetector,
    private val selfieRepository: SelfieRepository
) {
    suspend operator fun invoke(registrationId: Long): Flow<CaptureResult> = flow {
        emit(CaptureResult.Processing)
        
        try {
            val photoResult = cameraManager.capturePhoto()
            val bitmap = photoResult.getOrThrow()
            
            val saveResult = selfieRepository.saveSelfie(registrationId, bitmap)
            val uri = saveResult.getOrThrow()
            
            emit(CaptureResult.Success(uri))
        } catch (e: Exception) {
            emit(CaptureResult.Error(e.message ?: "Failed to capture selfie"))
        }
    }
}

sealed class CaptureResult {
    data object Processing : CaptureResult()
    data class Success(val uri: Uri) : CaptureResult()
    data class Error(val message: String) : CaptureResult()
}
