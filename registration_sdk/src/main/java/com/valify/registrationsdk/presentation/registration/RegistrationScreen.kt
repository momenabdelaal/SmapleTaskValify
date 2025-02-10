package com.valify.registrationsdk.presentation.registration

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is RegistrationViewModel.UiEvent.NavigateToSelfie -> {
                    onRegistrationComplete(event.userId)
                }
                is RegistrationViewModel.UiEvent.ShowError -> {
                    showError = event.message
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(100.dp),
                backgroundColor = theme.colors.primary,
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Register",
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
                    label = "Email",
                    keyboardType = KeyboardType.Email,
                    error = state.emailError,
                    isEnabled = !state.isLoading
                )

                ValifyTextField(
                    maxLength = 11,
                    value = state.phoneNumber,
                    onValueChange = { viewModel.onEvent(RegistrationEvent.PhoneNumberChanged(it)) },
                    label = "Phone Number",
                    keyboardType = KeyboardType.Phone,
                    error = state.phoneNumberError,
                    isEnabled = !state.isLoading
                )

                ValifyTextField(
                    value = state.password,
                    onValueChange = { viewModel.onEvent(RegistrationEvent.PasswordChanged(it)) },
                    label = "Password",
                    keyboardType = KeyboardType.Password,
                    error = state.passwordError,
                    isPassword = true,
                    isEnabled = !state.isLoading
                )

                Button(
                    onClick = { viewModel.onEvent(RegistrationEvent.Submit) },
                    enabled = state.isFormValid && !state.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor =  Color(0xFF263AC2) ,
                        contentColor = Color.White,
                        disabledBackgroundColor = Color(0xFFB0BEC5),
                        disabledContentColor = Color.White
                    ),
                    modifier = Modifier
                        .height(80.dp).fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text(text = if (state.isLoading) "Registering..." else "Register")
                }
            }

            ValifyProgressIndicator(
                message = "Processing registration...",
                isVisible = state.isLoading
            )

            if (showError != null) {
                AlertDialog(
                    onDismissRequest = { showError = null },
                    title = { Text("Error") },
                    text = { Text(showError!!) },
                    confirmButton = {
                        TextButton(onClick = { showError = null }) {
                            Text("OK")
                        }
                    },
                    backgroundColor = MaterialTheme.colors.surface,
                    contentColor = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}
