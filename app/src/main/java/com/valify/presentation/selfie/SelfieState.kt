package com.valify.presentation.selfie

data class SelfieState(
    val registrationId: Long = 0,
    val isSmileDetected: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val registrationComplete: Boolean = false
)
