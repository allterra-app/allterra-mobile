package com.allterra.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allterra.core.result.ApiResult
import com.allterra.core.ui.UiState
import com.allterra.domain.model.AuthTokens
import com.allterra.domain.usecase.LoginUseCase
import com.allterra.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthMode {
    Login,
    Register,
}

data class AuthFormState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val uiState: UiState<AuthTokens> = UiState.Idle,
)

sealed interface AuthEvent {
    data object Authorized : AuthEvent
}

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthFormState())
    val state: StateFlow<AuthFormState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    fun onEmailChanged(value: String) {
        _state.update { it.copy(email = value) }
    }

    fun onNameChanged(value: String) {
        _state.update { it.copy(name = value) }
    }

    fun onPasswordChanged(value: String) {
        _state.update { it.copy(password = value) }
    }

    fun submit(mode: AuthMode, validationMessage: String) {
        val snapshot = state.value
        val missingCredentials = snapshot.email.isBlank() || snapshot.password.isBlank()
        val missingName = mode == AuthMode.Register && snapshot.name.isBlank()
        if (missingCredentials || missingName) {
            _state.update { it.copy(uiState = UiState.Error(validationMessage)) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(uiState = UiState.Loading) }
            val result = when (mode) {
                AuthMode.Login -> loginUseCase(snapshot.email, snapshot.password)
                AuthMode.Register -> registerUseCase(snapshot.email, snapshot.password)
            }
            val uiState = result.toUiState()
            _state.update { it.copy(uiState = uiState) }
            if (uiState is UiState.Success) {
                _events.tryEmit(AuthEvent.Authorized)
            }
        }
    }

    fun clearError() {
        if (state.value.uiState is UiState.Error) {
            _state.update { it.copy(uiState = UiState.Idle) }
        }
    }

    fun resetForm() {
        _state.value = AuthFormState()
    }
}

private fun ApiResult<AuthTokens>.toUiState(): UiState<AuthTokens> {
    return when (this) {
        is ApiResult.Success -> UiState.Success(data)
        is ApiResult.ValidationError -> UiState.Error(message)
        is ApiResult.Unauthorized -> UiState.Error(message)
        is ApiResult.Forbidden -> UiState.Error(message)
        is ApiResult.NotFound -> UiState.Error(message)
        is ApiResult.ServerError -> UiState.Error(message)
        is ApiResult.NetworkError -> UiState.Error(message)
        is ApiResult.UnknownError -> UiState.Error(message)
    }
}
