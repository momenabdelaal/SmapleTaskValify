package com.valify.registrationsdk.presentation.registration.validation

import javax.inject.Inject

class ValidatePhoneNumber @Inject constructor() {
    operator fun invoke(phoneNumber: String): ValidationResult {
        if (phoneNumber.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Phone number cannot be blank"
            )
        }
        if (!phoneNumber.matches(PHONE_PATTERN.toRegex())) {
            return ValidationResult(
                successful = false,
                errorMessage = "That's not a valid phone number"
            )
        }
        return ValidationResult(
            successful = true
        )
    }

    companion object {
        // Matches Egyptian phone numbers starting with +20 or 0
        private const val PHONE_PATTERN = "^(\\+20|0)\\d{10}$"
    }
}
