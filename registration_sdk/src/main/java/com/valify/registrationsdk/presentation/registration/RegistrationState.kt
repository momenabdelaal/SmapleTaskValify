package com.valify.registrationsdk.presentation.registration

data class RegistrationState(
    val username: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val usernameError: String? = null,
    val emailError: String? = null,
    val phoneNumberError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isUsernameValid: Boolean = false,
    val isEmailValid: Boolean = false,
    val isPhoneValid: Boolean = false,
    val isPasswordValid: Boolean = false
) {
    val isFormValid: Boolean
        get() = isUsernameValid && isEmailValid && isPhoneValid && isPasswordValid && 
                username.isNotBlank() && email.isNotBlank() && 
                phoneNumber.isNotBlank() && password.isNotBlank()
}
