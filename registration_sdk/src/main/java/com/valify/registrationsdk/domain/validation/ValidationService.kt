package com.valify.registrationsdk.domain.validation

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

interface ValidationService {
    fun validateEmail(email: String): ValidationResult
    fun validatePhone(phone: String): ValidationResult
    fun validateUsername(username: String): ValidationResult
    fun validatePassword(password: String): ValidationResult
}
