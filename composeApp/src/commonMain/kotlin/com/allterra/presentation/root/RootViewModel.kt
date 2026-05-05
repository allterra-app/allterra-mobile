package com.allterra.presentation.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allterra.data.local.SessionPreferences
import com.allterra.data.local.TokenStorage
import com.allterra.domain.model.AppLanguage
import com.allterra.presentation.common.components.navigation.MainTab
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class RootStage {
    SPLASH,
    ONBOARDING,
    AUTH,
    MAIN,
}

enum class AuthScreen {
    LOGIN,
    REGISTER,
}

enum class MainOverlay {
    NONE,
    POIS,
}

data class RootUiState(
    val stage: RootStage = RootStage.SPLASH,
    val authScreen: AuthScreen = AuthScreen.LOGIN,
    val selectedMainTab: MainTab = MainTab.HOME,
    val overlay: MainOverlay = MainOverlay.NONE,
    val language: AppLanguage = AppLanguage.EN,
    val backdropIndex: Int = 0,
)

class RootViewModel(
    private val sessionPreferences: SessionPreferences,
    private val tokenStorage: TokenStorage,
) : ViewModel() {

    private val _state = MutableStateFlow(
        RootUiState(
            stage = RootStage.SPLASH,
            backdropIndex = randomBackdrop(),
        )
    )
    val state: StateFlow<RootUiState> = _state.asStateFlow()

    init {
        bootstrap()
    }

    fun onLanguageChanged(language: AppLanguage) {
        _state.update { it.copy(language = language) }
        viewModelScope.launch { sessionPreferences.setLanguage(language) }
    }

    fun onOnboardingCompleted() {
        viewModelScope.launch {
            sessionPreferences.setOnboardingCompleted(true)
            _state.update { it.copy(stage = RootStage.AUTH, authScreen = AuthScreen.LOGIN) }
        }
    }

    fun onAuthSuccess() {
        viewModelScope.launch {
            sessionPreferences.setLoggedIn(true)
            sessionPreferences.setLanguage(state.value.language)

            _state.update {
                it.copy(
                    stage = RootStage.SPLASH,
                    selectedMainTab = MainTab.HOME,
                    overlay = MainOverlay.NONE,
                    backdropIndex = randomBackdrop(except = it.backdropIndex),
                )
            }
            delay(SPLASH_DELAY_MS)
            _state.update {
                it.copy(
                    stage = RootStage.MAIN,
                    selectedMainTab = MainTab.HOME,
                    overlay = MainOverlay.NONE,
                )
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            tokenStorage.clear()
            sessionPreferences.setLoggedIn(false)
            _state.update {
                it.copy(
                    stage = RootStage.AUTH,
                    authScreen = AuthScreen.LOGIN,
                    overlay = MainOverlay.NONE,
                    backdropIndex = randomBackdrop(except = it.backdropIndex),
                )
            }
        }
    }

    fun openRegister() {
        _state.update { it.copy(stage = RootStage.AUTH, authScreen = AuthScreen.REGISTER) }
    }

    fun openLogin() {
        _state.update { it.copy(stage = RootStage.AUTH, authScreen = AuthScreen.LOGIN) }
    }

    fun onMainTabSelected(tab: MainTab) {
        _state.update { it.copy(selectedMainTab = tab, overlay = MainOverlay.NONE, stage = RootStage.MAIN) }
    }

    fun openPoisFromProfile() {
        _state.update { it.copy(stage = RootStage.MAIN, selectedMainTab = MainTab.HOME, overlay = MainOverlay.POIS) }
    }

    fun openRoutesFromProfile() {
        _state.update { it.copy(stage = RootStage.MAIN, selectedMainTab = MainTab.TRIPS, overlay = MainOverlay.NONE) }
    }

    fun openWardrobePlaceholder() {
        _state.update { it.copy(stage = RootStage.MAIN, selectedMainTab = MainTab.HOME, overlay = MainOverlay.NONE) }
    }

    fun closeOverlay() {
        _state.update { it.copy(overlay = MainOverlay.NONE) }
    }

    private fun bootstrap() {
        viewModelScope.launch {
            val language = sessionPreferences.getLanguage()
            val hasSessionFlag = sessionPreferences.isLoggedIn()
            val hasAccessToken = !tokenStorage.getAccessToken().isNullOrBlank()
            val hasSession = hasSessionFlag && hasAccessToken
            val isOnboardingCompleted = sessionPreferences.isOnboardingCompleted()
            
            val targetStage = when {
                hasSession -> RootStage.MAIN
                isOnboardingCompleted -> RootStage.AUTH
                else -> RootStage.ONBOARDING
            }

            if (hasSessionFlag && !hasAccessToken) {
                sessionPreferences.setLoggedIn(false)
            }

            _state.update {
                it.copy(
                    language = language,
                    stage = RootStage.SPLASH,
                    authScreen = AuthScreen.LOGIN,
                    selectedMainTab = MainTab.HOME,
                    overlay = MainOverlay.NONE,
                    backdropIndex = randomBackdrop(except = it.backdropIndex),
                )
            }

            delay(SPLASH_DELAY_MS)

            _state.update {
                it.copy(
                    stage = targetStage,
                    selectedMainTab = if (targetStage == RootStage.MAIN) MainTab.HOME else it.selectedMainTab
                )
            }
        }
    }

    private fun randomBackdrop(except: Int? = null): Int {
        if (BACKDROP_COUNT <= 1) return 0
        var next = Random.nextInt(0, BACKDROP_COUNT)
        while (next == except) {
            next = Random.nextInt(0, BACKDROP_COUNT)
        }
        return next
    }

    private companion object {
        private const val BACKDROP_COUNT = 2
        private const val SPLASH_DELAY_MS = 2500L // Increased for redesign animation
    }
}
