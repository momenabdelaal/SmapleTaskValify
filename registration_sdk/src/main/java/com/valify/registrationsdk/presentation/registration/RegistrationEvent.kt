package com.valify.registrationsdk.presentation.registration

sealed class RegistrationEvent {
    data class UsernameChanged(val username: String) : RegistrationEvent()
    data class EmailChanged(val email: String) : RegistrationEvent()
    data class PhoneNumberChanged(val phoneNumber: String) : RegistrationEvent()
    data class PasswordChanged(val password: String) : RegistrationEvent()
    object Submit : RegistrationEvent()
}
