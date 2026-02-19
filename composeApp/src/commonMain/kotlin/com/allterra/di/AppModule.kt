package com.allterra.di

import com.allterra.data.local.InMemoryTokenStorage
import com.allterra.data.local.TokenStorage
import com.allterra.data.repository.AuthRepositoryImpl
import com.allterra.domain.repository.AuthRepository
import com.allterra.domain.usecase.LoginUseCase
import com.allterra.domain.usecase.RegisterUseCase
import com.allterra.network.auth.AuthApi
import com.allterra.network.auth.AuthApiImpl
import com.allterra.network.createHttpClient
import com.allterra.presentation.auth.AuthViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { createHttpClient() }
    single<AuthApi> { AuthApiImpl(httpClient = get()) }
    single<TokenStorage> { InMemoryTokenStorage() }
    single<AuthRepository> { AuthRepositoryImpl(authApi = get(), tokenStorage = get()) }

    factory { LoginUseCase(authRepository = get()) }
    factory { RegisterUseCase(authRepository = get()) }

    viewModel {
        AuthViewModel(
            loginUseCase = get(),
            registerUseCase = get(),
        )
    }
}
