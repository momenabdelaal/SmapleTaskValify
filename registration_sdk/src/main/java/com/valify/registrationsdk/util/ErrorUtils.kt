package com.valify.registrationsdk.util

sealed class ValifyError(val code: String, val message: String) {
    class ValidationError(field: String, message: String) : 
        ValifyError("E00${getFieldCode(field)}", message)
    
    class CameraError(message: String) : 
        ValifyError("E005", message)
    
    class StorageError(message: String) : 
        ValifyError("E006", message)

    companion object {
        private fun getFieldCode(field: String): String = when(field.lowercase()) {
            "email" -> "1"
            "phone" -> "2"
            "username" -> "3"
            "password" -> "4"
            else -> "0"
        }
    }
}

sealed class ValifyResult<out T> {
    data class Success<T>(val data: T) : ValifyResult<T>()
    data class Error(val error: ValifyError) : ValifyResult<Nothing>()
}

fun ValifyError.toUserFriendlyMessage(): String {
    return when (this) {
        is ValifyError.ValidationError -> message
        is ValifyError.CameraError -> "Camera error: $message. Please check camera permissions and try again."
        is ValifyError.StorageError -> "Storage error: $message. Please check available space and permissions."
    }
}

fun Exception.toValifyError(): ValifyError {
    return when (this) {
        is SecurityException -> ValifyError.CameraError("Camera permission denied")
        is IllegalStateException -> ValifyError.CameraError("Camera not available")
        is OutOfMemoryError -> ValifyError.StorageError("Not enough memory")
        else -> ValifyError.StorageError("An unexpected error occurred: ${this.message}")
    }
}
