package com.allterra

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allterra.di.appModule
import com.allterra.domain.model.AppLanguage
import com.allterra.presentation.auth.AuthScreen
import com.allterra.presentation.auth.AuthViewModel
import com.allterra.presentation.common.components.navigation.BottomTabBar
import com.allterra.presentation.common.components.navigation.MainTab
import com.allterra.presentation.feed.FeedScreen
import com.allterra.presentation.feed.FeedViewModel
import com.allterra.presentation.localization.LocalAppLanguage
import com.allterra.presentation.localization.LocalAppStrings
import com.allterra.presentation.localization.stringsFor
import com.allterra.presentation.map.MapScreen
import com.allterra.presentation.pois.PoisScreen
import com.allterra.presentation.pois.PoisViewModel
import com.allterra.presentation.profile.ProfileScreen
import com.allterra.presentation.profile.ProfileViewModel
import com.allterra.presentation.onboarding.OnboardingScreen
import com.allterra.presentation.dashboard.DashboardScreen
import com.allterra.presentation.root.MainOverlay
import com.allterra.presentation.root.RootStage
import com.allterra.presentation.root.RootViewModel
import com.allterra.presentation.routes.RoutesScreen
import com.allterra.presentation.routes.RoutesViewModel
import com.allterra.presentation.settings.SettingsScreen
import com.allterra.presentation.splash.SplashScreen
import com.allterra.presentation.theme.AllterraTheme
import com.allterra.presentation.theme.ThemePreviewScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    val showThemePreview = false // DEBUG: Set to true to verify redesign tokens

    KoinApplication(application = { modules(appModule) }) {
        val rootViewModel: RootViewModel = koinViewModel()
        val authViewModel: AuthViewModel = koinViewModel()

        val rootState by rootViewModel.state.collectAsStateWithLifecycle()
        val strings = stringsFor(rootState.language)

        CompositionLocalProvider(
            LocalAppLanguage provides rootState.language,
            LocalAppStrings provides strings,
        ) {
            AllterraTheme {
                if (showThemePreview) {
                    ThemePreviewScreen()
                } else {
                    LaunchedEffect(rootState.stage) {
                        if (rootState.stage == RootStage.AUTH) authViewModel.resetForm()
                    }

                    when (rootState.stage) {
                        RootStage.SPLASH -> SplashScreen(backdropIndex = rootState.backdropIndex)

                        RootStage.ONBOARDING -> OnboardingScreen(
                            onCompleted = rootViewModel::onOnboardingCompleted
                        )

                        RootStage.AUTH -> AuthScreen(
                            mode = rootState.authScreen,
                            viewModel = authViewModel,
                            backdropIndex = rootState.backdropIndex,
                            selectedLanguage = rootState.language,
                            onLanguageSwitchClick = {
                                rootViewModel.onLanguageChanged(rootState.language.next())
                            },
                            onOpenLogin = rootViewModel::openLogin,
                            onOpenRegister = rootViewModel::openRegister,
                            onAuthorized = rootViewModel::onAuthSuccess,
                        )

                        RootStage.MAIN -> Box(modifier = Modifier.fillMaxSize()) {
                        val feedViewModel: FeedViewModel = koinViewModel()
                        val profileViewModel: ProfileViewModel = koinViewModel()
                        val poisViewModel: PoisViewModel = koinViewModel()
                        val routesViewModel: RoutesViewModel = koinViewModel()
                        val routesState by routesViewModel.state.collectAsStateWithLifecycle()
                        val poisState by poisViewModel.state.collectAsStateWithLifecycle()
                        val profileState by profileViewModel.state.collectAsStateWithLifecycle()

                        LaunchedEffect(rootState.stage) {
                            feedViewModel.refresh()
                            profileViewModel.refresh()
                            routesViewModel.refreshRoutes()
                            poisViewModel.refreshPois()
                        }

                        Box(modifier = Modifier.fillMaxSize()) {
                            when {
                                rootState.overlay == MainOverlay.POIS -> PoisScreen(
                                    viewModel = poisViewModel,
                                    onBack = rootViewModel::closeOverlay,
                                )

                                rootState.selectedMainTab == MainTab.HOME -> DashboardScreen(
                                    userName = profileState.user?.username ?: "Explorer",
                                    onLogout = rootViewModel::onLogout,
                                    onOpenPacking = {},
                                    onNewPost = { rootViewModel.onMainTabSelected(MainTab.FEED) },
                                    onAddDoc = { rootViewModel.onMainTabSelected(MainTab.WALLET) }
                                )

                                rootState.selectedMainTab == MainTab.FEED -> FeedScreen(viewModel = feedViewModel)

                                rootState.selectedMainTab == MainTab.TRIPS -> RoutesScreen(
                                    viewModel = routesViewModel,
                                    onBack = { rootViewModel.onMainTabSelected(MainTab.HOME) },
                                )

                                rootState.selectedMainTab == MainTab.MAP -> MapScreen()

                                rootState.selectedMainTab == MainTab.WALLET -> Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    androidx.compose.material3.Text("Wallet Coming Soon", style = AllterraTheme.typography.displayM)
                                }

                                else -> SettingsScreen()
                            }
                        }

                        BottomTabBar(
                            modifier = Modifier.align(Alignment.BottomCenter),
                            selectedTab = rootState.selectedMainTab,
                            strings = strings,
                            onTabSelected = rootViewModel::onMainTabSelected,
                        )
                    }
                    }
                }
            }
        }
    }
}

private fun AppLanguage.next(): AppLanguage {
    return when (this) {
        AppLanguage.EN -> AppLanguage.RU
        AppLanguage.RU -> AppLanguage.PL
        AppLanguage.PL -> AppLanguage.EN
    }
}
