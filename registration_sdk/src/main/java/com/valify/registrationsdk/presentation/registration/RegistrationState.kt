package com.valify.registrationsdk.presentation.registration

data class RegistrationState(
    val username: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val deviceId: String = "",
    val usernameError: String? = null,
    val emailError: String? = null,
    val phoneNumberError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isFormValid: Boolean = false,
    val registrationId: Long? = null,
    val isRegistered: Boolean = false,
    val selfieImagePath: String? = null
)
