package com.valify.registrationsdk.presentation.registration

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valify.registrationsdk.domain.model.UserRegistration
import com.valify.registrationsdk.domain.use_case.GetRegistration
import com.valify.registrationsdk.domain.use_case.SaveRegistration
import com.valify.registrationsdk.domain.validation.ValidationResult
import com.valify.registrationsdk.domain.validation.ValidationService
import com.valify.registrationsdk.util.DeviceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val saveRegistration: SaveRegistration,
    private val getRegistration: GetRegistration,
    private val validationService: ValidationService,
    private val deviceUtils: DeviceUtils
) : ViewModel() {

    private val _state = mutableStateOf(RegistrationState())
    val state: State<RegistrationState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            val deviceId = deviceUtils.getDeviceId()
            _state.value = _state.value.copy(deviceId = deviceId)
            
            getRegistration().collect { registration ->
                registration?.let {
                    _state.value = _state.value.copy(
                        isRegistered = true,
                        registrationId = it.id,
                        selfieImagePath = it.selfieImagePath
                    )
                }
            }
        }
    }

    fun onEvent(event: RegistrationEvent) {
        when (event) {
            is RegistrationEvent.UsernameChanged -> {
                when (val result = validationService.validateUsername(event.username)) {
                    is ValidationResult.Success -> {
                        _state.value = _state.value.copy(
                            username = event.username,
                            usernameError = null
                        )
                    }
                    is ValidationResult.Error -> {
                        _state.value = _state.value.copy(
                            username = event.username,
                            usernameError = result.message
                        )
                    }
                }
                updateFormValidity()
            }
            is RegistrationEvent.EmailChanged -> {
                when (val result = validationService.validateEmail(event.email)) {
                    is ValidationResult.Success -> {
                        _state.value = _state.value.copy(
                            email = event.email,
                            emailError = null
                        )
                    }
                    is ValidationResult.Error -> {
                        _state.value = _state.value.copy(
                            email = event.email,
                            emailError = result.message
                        )
                    }
                }
                updateFormValidity()
            }
            is RegistrationEvent.PhoneNumberChanged -> {
                when (val result = validationService.validatePhone(event.phoneNumber)) {
                    is ValidationResult.Success -> {
                        _state.value = _state.value.copy(
                            phoneNumber = event.phoneNumber,
                            phoneNumberError = null
                        )
                    }
                    is ValidationResult.Error -> {
                        _state.value = _state.value.copy(
                            phoneNumber = event.phoneNumber,
                            phoneNumberError = result.message
                        )
                    }
                }
                updateFormValidity()
            }
            is RegistrationEvent.PasswordChanged -> {
                when (val result = validationService.validatePassword(event.password)) {
                    is ValidationResult.Success -> {
                        _state.value = _state.value.copy(
                            password = event.password,
                            passwordError = null
                        )
                    }
                    is ValidationResult.Error -> {
                        _state.value = _state.value.copy(
                            password = event.password,
                            passwordError = result.message
                        )
                    }
                }
                updateFormValidity()
            }
            is RegistrationEvent.Submit -> {
                register()
            }
        }
    }

    private fun updateFormValidity() {
        val isValid = with(_state.value) {
            username.isNotBlank() && email.isNotBlank() && 
            phoneNumber.isNotBlank() && password.isNotBlank() &&
            usernameError == null && emailError == null && 
            phoneNumberError == null && passwordError == null
        }
        _state.value = _state.value.copy(isFormValid = isValid)
    }

    private fun register() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val registrationId = saveRegistration(
                    UserRegistration(
                        username = _state.value.username,
                        email = _state.value.email,
                        phone = _state.value.phoneNumber,
                        password = _state.value.password,
                        deviceId = _state.value.deviceId
                    )
                )
                _eventFlow.emit(UiEvent.NavigateToSelfie(registrationId))
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowError(e.message ?: "An unexpected error occurred"))
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
}
