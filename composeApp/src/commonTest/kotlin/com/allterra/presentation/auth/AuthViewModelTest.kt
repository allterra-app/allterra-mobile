package com.allterra.presentation.auth

import com.allterra.core.result.ApiResult
import com.allterra.core.ui.UiState
import com.allterra.domain.model.AuthTokens
import com.allterra.domain.repository.AuthRepository
import com.allterra.domain.usecase.LoginUseCase
import com.allterra.domain.usecase.RegisterUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun submit_withEmptyFields_returnsValidationError() {
        val dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)
        val viewModel = buildViewModel(FakeAuthRepository(success = true))

        viewModel.submit(mode = AuthMode.Login, validationMessage = "Validation")

        val state = viewModel.state.value.uiState
        assertIs<UiState.Error>(state)
        assertEquals("Validation", state.message)
    }

    @Test
    fun submit_success_setsSuccessState() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        val viewModel = buildViewModel(FakeAuthRepository(success = true))
        viewModel.onEmailChanged("test@example.com")
        viewModel.onPasswordChanged("secret")

        viewModel.submit(mode = AuthMode.Login, validationMessage = "Validation")
        advanceUntilIdle()

        assertIs<UiState.Success<AuthTokens>>(viewModel.state.value.uiState)
    }

    @Test
    fun submit_registerWithoutName_returnsValidationError() {
        val dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)
        val viewModel = buildViewModel(FakeAuthRepository(success = true))
        viewModel.onEmailChanged("test@example.com")
        viewModel.onPasswordChanged("secret")

        viewModel.submit(mode = AuthMode.Register, validationMessage = "Validation")

        val state = viewModel.state.value.uiState
        assertIs<UiState.Error>(state)
        assertEquals("Validation", state.message)
    }

    private fun buildViewModel(repository: AuthRepository): AuthViewModel {
        return AuthViewModel(
            loginUseCase = LoginUseCase(repository),
            registerUseCase = RegisterUseCase(repository),
        )
    }
}

private class FakeAuthRepository(
    private val success: Boolean,
) : AuthRepository {
    override suspend fun login(email: String, password: String): ApiResult<AuthTokens> {
        return if (success) {
            ApiResult.Success(AuthTokens(accessToken = "a", refreshToken = "r"))
        } else {
            ApiResult.Unauthorized("Unauthorized")
        }
    }

    override suspend fun register(email: String, password: String): ApiResult<AuthTokens> {
        return if (success) {
            ApiResult.Success(AuthTokens(accessToken = "a", refreshToken = "r"))
        } else {
            ApiResult.ValidationError("Validation", emptyMap())
        }
    }
}
