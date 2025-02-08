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
                _state.value = _state.value.copy(
                    username = event.username,
                    usernameError = if (event.username.isBlank()) "Username cannot be blank" else null
                )
                validateForm()
            }
            is RegistrationEvent.EmailChanged -> {
                _state.value = _state.value.copy(
                    email = event.email,
                    emailError = null
                )
                validateForm()
            }
            is RegistrationEvent.PhoneNumberChanged -> {
                _state.value = _state.value.copy(
                    phoneNumber = event.phoneNumber,
                    phoneNumberError = null
                )
                validateForm()
            }
            is RegistrationEvent.PasswordChanged -> {
                _state.value = _state.value.copy(
                    password = event.password,
                    passwordError = null
                )
                validateForm()
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
    }

    private fun validateForm(): Boolean {
        val emailResult = validateEmail(state.value.email)
        val passwordResult = validatePassword(state.value.password)
        val phoneResult = validatePhoneNumber(state.value.phoneNumber)
        val usernameValid = state.value.username.isNotBlank()

        _state.value = _state.value.copy(
            emailError = if (!emailResult.successful) emailResult.errorMessage else null,
            passwordError = if (!passwordResult.successful) passwordResult.errorMessage else null,
            phoneNumberError = if (!phoneResult.successful) phoneResult.errorMessage else null,
            usernameError = if (!usernameValid) "Username cannot be blank" else null,
            isValid = emailResult.successful && passwordResult.successful && 
                      phoneResult.successful && usernameValid
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
