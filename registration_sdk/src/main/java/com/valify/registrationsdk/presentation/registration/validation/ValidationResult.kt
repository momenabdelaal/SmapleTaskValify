package com.valify.registrationsdk.presentation.registration.validation

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)
