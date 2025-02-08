package com.valify.registrationsdk.presentation.registration

data class RegistrationState(
    val username: String = "",
    val usernameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val phoneNumber: String = "",
    val phoneNumberError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isValid: Boolean = false,
    val registrationId: Long? = null,
    val error: String? = null
)
