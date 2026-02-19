package com.allterra

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.allterra.di.appModule
import com.allterra.presentation.auth.AuthScreen
import com.allterra.presentation.auth.AuthViewModel
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        MaterialTheme {
            val authViewModel: AuthViewModel = koinViewModel()
            AuthScreen(viewModel = authViewModel)
        }
    }
}
