package com.allterra.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allterra.core.ui.UiState

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Allterra",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "Social outdoor activities",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            TextButton(onClick = { viewModel.onModeChanged(AuthMode.Login) }) {
                Text("Login")
            }
            TextButton(onClick = { viewModel.onModeChanged(AuthMode.Register) }) {
                Text("Register")
            }
        }

        OutlinedTextField(
            value = state.email,
            onValueChange = {
                viewModel.clearError()
                viewModel.onEmailChanged(it)
            },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
        )

        OutlinedTextField(
            value = state.password,
            onValueChange = {
                viewModel.clearError()
                viewModel.onPasswordChanged(it)
            },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
        )

        Button(
            onClick = viewModel::submit,
            enabled = state.uiState !is UiState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
        ) {
            Text(if (state.mode == AuthMode.Login) "Sign In" else "Create Account")
        }

        when (val uiState = state.uiState) {
            UiState.Idle -> Unit
            UiState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            is UiState.Error -> Text(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp),
            )
            is UiState.Success -> Text(
                text = "Success. Session is active.",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}
