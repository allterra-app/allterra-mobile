package com.allterra.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allterra.core.result.ApiResult
import com.allterra.core.ui.UiState
import com.allterra.domain.model.AuthTokens
import com.allterra.domain.usecase.LoginUseCase
import com.allterra.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthMode {
    Login,
    Register,
}

data class AuthFormState(
    val mode: AuthMode = AuthMode.Login,
    val email: String = "",
    val password: String = "",
    val uiState: UiState<AuthTokens> = UiState.Idle,
)

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthFormState())
    val state: StateFlow<AuthFormState> = _state.asStateFlow()

    fun onEmailChanged(value: String) {
        _state.update { it.copy(email = value) }
    }

    fun onPasswordChanged(value: String) {
        _state.update { it.copy(password = value) }
    }

    fun onModeChanged(mode: AuthMode) {
        _state.update { it.copy(mode = mode, uiState = UiState.Idle) }
    }

    fun submit() {
        val snapshot = state.value
        if (snapshot.email.isBlank() || snapshot.password.isBlank()) {
            _state.update { it.copy(uiState = UiState.Error("Email and password are required")) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(uiState = UiState.Loading) }
            val result = when (snapshot.mode) {
                AuthMode.Login -> loginUseCase(snapshot.email, snapshot.password)
                AuthMode.Register -> registerUseCase(snapshot.email, snapshot.password)
            }
            _state.update {
                it.copy(uiState = result.toUiState())
            }
        }
    }

    fun clearError() {
        if (state.value.uiState is UiState.Error) {
            _state.update { it.copy(uiState = UiState.Idle) }
        }
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
