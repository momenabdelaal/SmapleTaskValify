package com.valify.registrationsdk.presentation.registration

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.valify.registrationsdk.presentation.components.ValifyTextField

@Composable
fun RegistrationScreen(
    onRegistrationComplete: (Long) -> Unit,
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registration",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        ValifyTextField(
            value = state.username,
            onValueChange = { viewModel.onEvent(RegistrationEvent.UsernameChanged(it)) },
            label = "Username",
            error = state.usernameError,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        ValifyTextField(
            value = state.phoneNumber,
            onValueChange = { viewModel.onEvent(RegistrationEvent.PhoneNumberChanged(it)) },
            label = "Phone Number",
            error = state.phoneNumberError,
            keyboardType = KeyboardType.Phone,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        ValifyTextField(
            value = state.email,
            onValueChange = { viewModel.onEvent(RegistrationEvent.EmailChanged(it)) },
            label = "Email",
            error = state.emailError,
            keyboardType = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        ValifyTextField(
            value = state.password,
            onValueChange = { viewModel.onEvent(RegistrationEvent.PasswordChanged(it)) },
            label = "Password",
            error = state.passwordError,
            isPassword = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { 
                viewModel.onEvent(RegistrationEvent.Submit) 
            },
            enabled = !state.isLoading && state.isValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Continue")
            }
        }

        if (state.error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = state.error,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption
            )
        }
    }

    LaunchedEffect(state.registrationId) {
        if (state.registrationId != null) {
            onRegistrationComplete(state.registrationId)
        }
    }
}
