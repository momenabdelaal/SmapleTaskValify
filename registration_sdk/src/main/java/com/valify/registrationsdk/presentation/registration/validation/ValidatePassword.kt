package com.valify.registrationsdk.presentation.registration.validation

import javax.inject.Inject

class ValidatePassword @Inject constructor() {
    operator fun invoke(password: String): ValidationResult {
        if (password.length < 8) {
            return ValidationResult(
                successful = false,
                errorMessage = "Password needs to consist of at least 8 characters"
            )
        }
        val containsLettersAndDigits = password.any { it.isLetter() } && 
                password.any { it.isDigit() }
        if (!containsLettersAndDigits) {
            return ValidationResult(
                successful = false,
                errorMessage = "Password needs to contain at least one letter and digit"
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}
