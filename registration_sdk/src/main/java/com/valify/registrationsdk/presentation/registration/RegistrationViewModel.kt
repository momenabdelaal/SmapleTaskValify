package com.valify.registrationsdk.presentation.registration

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valify.registrationsdk.domain.model.UserRegistration
import com.valify.registrationsdk.domain.use_case.SaveRegistration
import com.valify.registrationsdk.domain.validation.ValidationService
import com.valify.registrationsdk.domain.validation.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.net.UnknownHostException
import java.sql.SQLException



@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val saveRegistration: SaveRegistration,
    private val validationService: ValidationService
) : ViewModel() {

    private val _state = mutableStateOf(RegistrationState())
    val state: State<RegistrationState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class RegistrationError(val message: String) {
        class NetworkError(message: String = "Network connection error. Please check your internet connection.") : RegistrationError(message)
        class DatabaseError(message: String = "Database error occurred. Please try again.") : RegistrationError(message)
        class ValidationError(field: String, errorMessage: String) : RegistrationError("$field: $errorMessage")
        class UnknownError(message: String = "An unexpected error occurred. Please try again.") : RegistrationError(message)
    }

    fun onEvent(event: RegistrationEvent) {
        when (event) {
            is RegistrationEvent.UsernameChanged -> {
                validateField(
                    value = event.username,
                    validateFun = validationService::validateUsername,
                    onSuccess = { 
                        _state.value = _state.value.copy(
                            username = event.username,
                            usernameError = null,
                            isUsernameValid = true
                        )
                    },
                    onError = { 
                        _state.value = _state.value.copy(
                            username = event.username,
                            usernameError = it,
                            isUsernameValid = false
                        )
                    }
                )
            }
            is RegistrationEvent.EmailChanged -> {
                validateField(
                    value = event.email,
                    validateFun = validationService::validateEmail,
                    onSuccess = { 
                        _state.value = _state.value.copy(
                            email = event.email,
                            emailError = null,
                            isEmailValid = true
                        )
                    },
                    onError = { 
                        _state.value = _state.value.copy(
                            email = event.email,
                            emailError = it,
                            isEmailValid = false
                        )
                    }
                )
            }
            is RegistrationEvent.PhoneNumberChanged -> {
                validateField(
                    value = event.phoneNumber,
                    validateFun = validationService::validatePhone,
                    onSuccess = { 
                        _state.value = _state.value.copy(
                            phoneNumber = event.phoneNumber,
                            phoneNumberError = null,
                            isPhoneValid = true
                        )
                    },
                    onError = { 
                        _state.value = _state.value.copy(
                            phoneNumber = event.phoneNumber,
                            phoneNumberError = it,
                            isPhoneValid = false
                        )
                    }
                )
            }
            is RegistrationEvent.PasswordChanged -> {
                validateField(
                    value = event.password,
                    validateFun = validationService::validatePassword,
                    onSuccess = { 
                        _state.value = _state.value.copy(
                            password = event.password,
                            passwordError = null,
                            isPasswordValid = true
                        )
                    },
                    onError = { 
                        _state.value = _state.value.copy(
                            password = event.password,
                            passwordError = it,
                            isPasswordValid = false
                        )
                    }
                )
            }
            is RegistrationEvent.Submit -> {
                submitData()
            }
        }
    }

    private fun <T> validateField(
        value: T,
        validateFun: (T) -> ValidationResult,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        when (val result = validateFun(value)) {
            is ValidationResult.Success -> onSuccess()
            is ValidationResult.Error -> onError(result.message)
        }
    }

    private fun validateAllFields(): List<RegistrationError> {
        val errors = mutableListOf<RegistrationError>()
        
        with(state.value) {
            validateField(username, validationService::validateUsername)?.let {
                errors.add(RegistrationError.ValidationError("Username", it))
            }
            validateField(email, validationService::validateEmail)?.let {
                errors.add(RegistrationError.ValidationError("Email", it))
            }
            validateField(phoneNumber, validationService::validatePhone)?.let {
                errors.add(RegistrationError.ValidationError("Phone", it))
            }
            validateField(password, validationService::validatePassword)?.let {
                errors.add(RegistrationError.ValidationError("Password", it))
            }
        }
        
        return errors
    }

    private fun <T> validateField(value: T, validateFun: (T) -> ValidationResult): String? {
        return when (val result = validateFun(value)) {
            is ValidationResult.Success -> null
            is ValidationResult.Error -> result.message
        }
    }

    private fun submitData() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)

                val validationErrors = validateAllFields()
                if (validationErrors.isNotEmpty()) {
                    handleErrors(validationErrors)
                    return@launch
                }

                val registration = UserRegistration(
                    username = state.value.username,
                    email = state.value.email,
                    phoneNumber = state.value.phoneNumber,
                    password = state.value.password
                )

                val result = saveRegistration(registration)
                if (result > 0) {
                    _eventFlow.emit(UiEvent.NavigateToSelfie(result))
                } else {
                    handleError(RegistrationError.DatabaseError("Failed to save registration"))
                }

            } catch (e: UnknownHostException) {
                handleError(RegistrationError.NetworkError())
            } catch (e: SQLException) {
                handleError(RegistrationError.DatabaseError(e.message ?: "Database error occurred"))
            } catch (e: Exception) {
                handleError(RegistrationError.UnknownError(e.message ?: "An unexpected error occurred"))
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    private suspend fun handleErrors(errors: List<RegistrationError>) {
        errors.firstOrNull()?.let { handleError(it) }
    }

    private suspend fun handleError(error: RegistrationError) {
        _eventFlow.emit(UiEvent.ShowError(error.message))
    }

    sealed class UiEvent {
        data class ShowError(val message: String) : UiEvent()
        data class NavigateToSelfie(val userId: Long) : UiEvent()
    }
}
