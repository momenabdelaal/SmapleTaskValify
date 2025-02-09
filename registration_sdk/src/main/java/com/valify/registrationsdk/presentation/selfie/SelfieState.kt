package com.valify.registrationsdk.presentation.selfie

import android.graphics.Bitmap
import android.net.Uri



data class SelfieState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCaptureSuccess: Boolean = false,
    val isSaved: Boolean = false,
    val savedImage: Uri? = null
)