package com.valify.registrationsdk.presentation.registration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.valify.registrationsdk.R
import com.valify.registrationsdk.presentation.components.ValifyProgressIndicator
import com.valify.registrationsdk.presentation.components.ValifyTextField
import com.valify.registrationsdk.presentation.theme.LocalValifyTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RegistrationScreen(
    onRegistrationComplete: (Long) -> Unit,
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val theme = LocalValifyTheme.current
    val context = LocalContext.current
    var showError by remember { mutableStateOf<String?>(null) }
    var showSelfiePreview by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.NavigateToSelfie -> {
                    onRegistrationComplete(event.userId)
                }
                is UiEvent.ShowError -> {
                    showError = event.message
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(85.dp),
                backgroundColor = theme.colors.primary,
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.register),
                            style = MaterialTheme.typography.h6,
                            color = theme.colors.onPrimary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (showSelfiePreview && state.selfieImagePath != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = state.selfieImagePath,
                        contentDescription = "Selfie Preview",
                        modifier = Modifier
                            .size(300.dp)
                            .padding(16.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = { showSelfiePreview = false }) {
                            Text(stringResource(R.string.back))
                        }
                        Button(
                            onClick = { 
                                state.registrationId?.let { onRegistrationComplete(it) }
                            }
                        ) {
                            Text(stringResource(R.string.retake_photo))
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ValifyTextField(
                        value = state.username,
                        onValueChange = { viewModel.onEvent(RegistrationEvent.UsernameChanged(it)) },
                        label = "Username",
                        error = state.usernameError,
                        isEnabled = !state.isLoading
                    )

                    ValifyTextField(
                        value = state.email,
                        onValueChange = { viewModel.onEvent(RegistrationEvent.EmailChanged(it)) },
                        label = stringResource(R.string.email),
                        keyboardType = KeyboardType.Email,
                        error = state.emailError,
                        isEnabled = !state.isLoading
                    )

                    ValifyTextField(
                        maxLength = 11,
                        value = state.phoneNumber,
                        onValueChange = { viewModel.onEvent(RegistrationEvent.PhoneNumberChanged(it)) },
                        label = stringResource(R.string.phone_number),
                        keyboardType = KeyboardType.Phone,
                        error = state.phoneNumberError,
                        isEnabled = !state.isLoading
                    )

                    ValifyTextField(
                        value = state.password,
                        onValueChange = { viewModel.onEvent(RegistrationEvent.PasswordChanged(it)) },
                        label = stringResource(R.string.password),
                        keyboardType = KeyboardType.Password,
                        error = state.passwordError,
                        isPassword = true,
                        isEnabled = !state.isLoading
                    )



                    Button(
                        onClick = { viewModel.onEvent(RegistrationEvent.Submit) },
                        enabled = (!state.isRegistered && state.isFormValid && !state.isLoading),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF263AC2),
                            contentColor = Color.White,
                            disabledBackgroundColor = Color(0xFFB0BEC5),
                            disabledContentColor = Color.White
                        ),
                        modifier = Modifier
                            .height(80.dp)
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text(text = if (state.isLoading) stringResource(R.string.registering) else stringResource(R.string.register))
                    }

                    if (state.isRegistered) {
                        Text(
                            text = stringResource(R.string.already_registered),
                            color = Color(0xFF263AC2),
                            modifier = Modifier
                                .clickable {
                                    if (state.selfieImagePath != null) {
                                        showSelfiePreview = true
                                    } else {
                                        state.registrationId?.let { onRegistrationComplete(it) }
                                    }
                                }
                                .padding(8.dp)
                        )
                    }
                }
            }

            ValifyProgressIndicator(
                message = stringResource(R.string.processing_registration),
                isVisible = state.isLoading
            )

            if (showError != null) {
                AlertDialog(
                    onDismissRequest = { showError = null },
                    title = { Text("Error") },
                    text = { Text(showError!!) },
                    confirmButton = {
                        TextButton(onClick = { showError = null }) {
                            Text(stringResource(R.string.ok))
                        }
                    },
                    backgroundColor = MaterialTheme.colors.surface,
                    contentColor = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}
