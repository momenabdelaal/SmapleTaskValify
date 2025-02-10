package com.valify.registrationsdk.presentation.selfie

import android.graphics.Bitmap

sealed class SelfieEvent {
    data class OnPhotoCapture(val bitmap: Bitmap) : SelfieEvent()
    object OnRetakePhoto : SelfieEvent()
    object OnSaveAndContinue : SelfieEvent()
}

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    data class NavigateToRegister(val registrationId: Long) : UiEvent()
}