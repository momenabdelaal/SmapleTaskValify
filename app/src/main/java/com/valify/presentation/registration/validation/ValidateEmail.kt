package com.valify.presentation.registration.validation

import android.util.Patterns
import javax.inject.Inject

class ValidateEmail @Inject constructor() {
    operator fun invoke(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Email cannot be blank"
            )
        }
        if (!email.matches(EMAIL_PATTERN.toRegex())) {
            return ValidationResult(
                successful = false,
                errorMessage = "That's not a valid email"
            )
        }
        return ValidationResult(
            successful = true
        )
    }

    companion object {
        private const val EMAIL_PATTERN = 
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    }
}
