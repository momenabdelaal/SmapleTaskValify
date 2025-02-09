package com.valify.registrationsdk.presentation.registration

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valify.registrationsdk.domain.model.UserRegistration
import com.valify.registrationsdk.domain.use_case.SaveRegistration
import com.valify.registrationsdk.presentation.registration.validation.ValidateEmail
import com.valify.registrationsdk.presentation.registration.validation.ValidatePassword
import com.valify.registrationsdk.presentation.registration.validation.ValidatePhoneNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
    private val validatePhoneNumber: ValidatePhoneNumber,
    private val saveRegistration: SaveRegistration
) : ViewModel() {

    private val _state = mutableStateOf(RegistrationState())
    val state: State<RegistrationState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: RegistrationEvent) {
        when (event) {
            is RegistrationEvent.UsernameChanged -> {
                val error = if (event.username.isBlank()) "Username cannot be blank" else null
                _state.value = _state.value.copy(
                    username = event.username,
                    usernameError = error
                )
            }
            is RegistrationEvent.EmailChanged -> {
                val emailResult = validateEmail(event.email)
                _state.value = _state.value.copy(
                    email = event.email,
                    emailError = if (!emailResult.successful) emailResult.errorMessage else null
                )
            }
            is RegistrationEvent.PhoneNumberChanged -> {
                val phoneResult = validatePhoneNumber(event.phoneNumber)
                _state.value = _state.value.copy(
                    phoneNumber = event.phoneNumber,
                    phoneNumberError = if (!phoneResult.successful) phoneResult.errorMessage else null
                )
            }
            is RegistrationEvent.PasswordChanged -> {
                val passwordResult = validatePassword(event.password)
                _state.value = _state.value.copy(
                    password = event.password,
                    passwordError = if (!passwordResult.successful) passwordResult.errorMessage else null
                )
            }
            is RegistrationEvent.Submit -> {
                if (validateForm()) {
                    submitData()
                }
            }
            is RegistrationEvent.NavigateToSelfie -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.NavigateToSelfie(_state.value.registrationId!!))
                }
            }
        }

        // Update overall form validation status
        updateFormValidation()
    }

    private fun updateFormValidation() {
        _state.value = _state.value.copy(
            isValid = _state.value.usernameError == null &&
                    _state.value.emailError == null &&
                    _state.value.phoneNumberError == null &&
                    _state.value.passwordError == null &&
                    _state.value.username.isNotBlank()
        )
    }

    private fun validateForm(): Boolean {
        val usernameError = if (_state.value.username.isBlank()) "Username cannot be blank" else null
        val emailResult = validateEmail(_state.value.email)
        val passwordResult = validatePassword(_state.value.password)
        val phoneResult = validatePhoneNumber(_state.value.phoneNumber)

        _state.value = _state.value.copy(
            usernameError = usernameError,
            emailError = if (!emailResult.successful) emailResult.errorMessage else null,
            passwordError = if (!passwordResult.successful) passwordResult.errorMessage else null,
            phoneNumberError = if (!phoneResult.successful) phoneResult.errorMessage else null,
            isValid = usernameError == null &&
                    emailResult.successful &&
                    passwordResult.successful &&
                    phoneResult.successful
        )

        return _state.value.isValid
    }

    private fun submitData() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, error = null)

                val registration = UserRegistration(
                    username = state.value.username,
                    email = state.value.email,
                    phoneNumber = state.value.phoneNumber,
                    password = state.value.password
                )

                val registrationId = saveRegistration(registration)
                _state.value = _state.value.copy(
                    registrationId = registrationId,
                    isLoading = false
                )

                // Navigate to CameraActivity after successful registration
                _eventFlow.emit(UiEvent.NavigateToCamera)
            } catch (e: IllegalArgumentException) {
                _state.value = _state.value.copy(
                    error = e.message,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "An unexpected error occurred",
                    isLoading = false
                )
            }
        }
    }

    sealed class UiEvent {
        data class NavigateToSelfie(val registrationId: Long) : UiEvent()
        object NavigateToCamera : UiEvent()
    }
}
