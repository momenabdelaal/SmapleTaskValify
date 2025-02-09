package com.valify.registrationsdk.presentation.registration

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.valify.registrationsdk.R
import com.valify.registrationsdk.presentation.components.ValifyTextField

@Composable
fun RegistrationScreen(
    onRegistrationComplete: (Long) -> Unit, viewModel: RegistrationViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    Scaffold(

        topBar = {
            TopAppBar(
                modifier = Modifier.height(100.dp),
                backgroundColor = Color(0xFF263AC2),
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
                            color = Color.White,
                            textAlign = TextAlign.Center

                        )
                    }
                },
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .width(200.dp)
                    .height(100.dp)

            )
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
                maxLength = 11,
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
                modifier = Modifier.fillMaxWidth(),

            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { viewModel.onEvent(RegistrationEvent.Submit) },
                enabled = !state.isLoading && state.isValid,
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (!state.isLoading && state.isValid) Color(0xFF263AC2) else Color(
                        0xFFB0BEC5
                    ),
                    contentColor = Color.White,
                    disabledBackgroundColor = Color(0xFFB0BEC5),
                    disabledContentColor = Color.White
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
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
}

