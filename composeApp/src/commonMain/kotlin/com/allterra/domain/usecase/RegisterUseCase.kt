package com.allterra.domain.usecase

import com.allterra.core.result.ApiResult
import com.allterra.domain.model.AuthTokens
import com.allterra.domain.repository.AuthRepository

class RegisterUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): ApiResult<AuthTokens> {
        return authRepository.register(email = email.trim(), password = password)
    }
}
