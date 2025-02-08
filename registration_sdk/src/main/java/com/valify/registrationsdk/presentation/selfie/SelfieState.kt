package com.valify.registrationsdk.presentation.selfie

import android.graphics.Bitmap
import android.net.Uri

sealed class SelfieEvent {
    data class OnPhotoCapture(val bitmap: Bitmap) : SelfieEvent()
    object OnRetakePhoto : SelfieEvent()
    object OnSaveAndContinue : SelfieEvent()
}

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    data class NavigateToRegister(val registrationId: Long) : UiEvent()
}

data class SelfieState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCaptureSuccess: Boolean = false,
    val isSaved: Boolean = false,
    val savedImage: Uri? = null
)