package com.valify.registrationsdk.presentation.selfie

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valify.registrationsdk.domain.repository.SelfieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelfieViewModel @Inject constructor(
    private val selfieRepository: SelfieRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(SelfieState())
    val state: State<SelfieState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentRegistrationId: Long = 0L
        set(value) {
            if (field != value) {
                field = value
                savedStateHandle["registration_id"] = value
                loadSavedImage()
            }
        }

    init {

        savedStateHandle.get<Long>("registration_id")?.let { id ->
            currentRegistrationId = id
            loadSavedImage()
        }
    }

    fun setRegistrationId(id: Long) {
        if (id != currentRegistrationId) {
            currentRegistrationId = id
            _state.value = _state.value.copy(
                savedImage = null,
                isCaptureSuccess = false,
                error = null,
                isLoading = false,
                isSaved = false
            )
            loadSavedImage()
        }
    }

    fun onEvent(event: SelfieEvent) {
        when (event) {
            is SelfieEvent.OnPhotoCapture -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(isLoading = true)
                    selfieRepository.deleteSelfie(currentRegistrationId)
                    
                    selfieRepository.saveSelfie(currentRegistrationId, event.bitmap)
                        .onSuccess { uri ->
                            _state.value = _state.value.copy(
                                savedImage = uri,
                                isCaptureSuccess = true,
                                error = null,
                                isLoading = false
                            )
                        }
                        .onFailure { e ->
                            _state.value = _state.value.copy(
                                error = "Failed to save photo: ${e.message}",
                                isLoading = false
                            )
                        }
                }
            }
            is SelfieEvent.OnRetakePhoto -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(isLoading = true)
                    selfieRepository.deleteSelfie(currentRegistrationId)
                        .onSuccess {
                            _state.value = _state.value.copy(
                                savedImage = null,
                                isCaptureSuccess = false,
                                error = null,
                                isLoading = false
                            )
                        }
                        .onFailure { e ->
                            _state.value = _state.value.copy(
                                error = "Failed to delete photo: ${e.message}",
                                isLoading = false
                            )
                        }
                }
            }
            is SelfieEvent.OnSaveAndContinue -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(isLoading = true)
                    if (state.value.savedImage != null) {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isSaved = true
                        )
                        _eventFlow.emit(UiEvent.NavigateToRegister(currentRegistrationId))
                    } else {
                        _state.value = _state.value.copy(
                            error = "Please take a photo first",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun loadSavedImage() {
        if (currentRegistrationId == 0L) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            selfieRepository.getSelfie(currentRegistrationId)
                .onSuccess { uri ->
                    _state.value = _state.value.copy(
                        savedImage = uri,
                        isCaptureSuccess = true,
                        error = null,
                        isLoading = false
                    )
                }
                .onFailure { e ->
                    if (e.message != "Selfie not found") {
                        _state.value = _state.value.copy(
                            error = "Failed to load saved photo: ${e.message}",
                            isLoading = false
                        )
                    } else {
                        _state.value = _state.value.copy(isLoading = false)
                    }
                }
        }
    }


}
