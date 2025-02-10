package com.valify.registrationsdk.data.validation

import com.valify.registrationsdk.domain.validation.ValidationResult
import com.valify.registrationsdk.domain.validation.ValidationService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultValidationService @Inject constructor() : ValidationService {
    companion object {
        private const val EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        private const val PHONE_PATTERN = "^[+]?[0-9]{10,13}$"
        private const val MIN_USERNAME_LENGTH = 3
        private const val MIN_PASSWORD_LENGTH = 8
    }

    override fun validateEmail(email: String): ValidationResult {
        return when {
            email.isEmpty() -> ValidationResult.Error("Email is required")
            !email.matches(EMAIL_PATTERN.toRegex()) -> ValidationResult.Error("Invalid email format")
            else -> ValidationResult.Success
        }
    }

    override fun validatePhone(phone: String): ValidationResult {
        return when {
            phone.isEmpty() -> ValidationResult.Error("Phone number is required")
            !phone.matches(PHONE_PATTERN.toRegex()) -> ValidationResult.Error("Invalid phone number format")
            else -> ValidationResult.Success
        }
    }

    override fun validateUsername(username: String): ValidationResult {
        return when {
            username.isEmpty() -> ValidationResult.Error("Username is required")
            username.length < MIN_USERNAME_LENGTH -> ValidationResult.Error("Username must be at least $MIN_USERNAME_LENGTH characters")
            else -> ValidationResult.Success
        }
    }

    override fun validatePassword(password: String): ValidationResult {
        return when {
            password.isEmpty() -> ValidationResult.Error("Password is required")
            password.length < MIN_PASSWORD_LENGTH -> ValidationResult.Error("Password must be at least $MIN_PASSWORD_LENGTH characters")
            !password.any { it.isDigit() } -> ValidationResult.Error("Password must contain at least one number")
            !password.any { it.isUpperCase() } -> ValidationResult.Error("Password must contain at least one uppercase letter")
            !password.any { it.isLowerCase() } -> ValidationResult.Error("Password must contain at least one lowercase letter")
            else -> ValidationResult.Success
        }
    }
}
