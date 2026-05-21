package com.allterra

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allterra.di.appModule
import com.allterra.domain.model.AppLanguage
import com.allterra.presentation.auth.AuthScreen as AuthScreenContent
import com.allterra.presentation.auth.AuthViewModel
import com.allterra.presentation.common.components.navigation.BottomTabBar
import com.allterra.presentation.common.components.navigation.MainTab
import com.allterra.presentation.common.components.redesign.AllterraButton
import com.allterra.presentation.common.components.redesign.AllterraSheet
import com.allterra.presentation.dashboard.DashboardScreen
import com.allterra.presentation.feed.FeedScreen
import com.allterra.presentation.feed.FeedViewModel
import com.allterra.presentation.gear.GearInventoryScreen
import com.allterra.presentation.gear.GearViewModel
import com.allterra.presentation.localization.LocalAppLanguage
import com.allterra.presentation.localization.LocalAppStrings
import com.allterra.presentation.localization.stringsFor
import com.allterra.presentation.map.MapScreen
import com.allterra.presentation.onboarding.OnboardingScreen
import com.allterra.presentation.packing.PackingScreen
import com.allterra.presentation.packing.PackingViewModel
import com.allterra.presentation.pois.PoisScreen
import com.allterra.presentation.pois.PoisViewModel
import com.allterra.presentation.profile.ProfileViewModel
import com.allterra.presentation.root.*
import com.allterra.presentation.routes.RoutesScreen
import com.allterra.presentation.routes.RoutesViewModel
import com.allterra.presentation.settings.SettingsScreen
import com.allterra.presentation.splash.SplashScreen
import com.allterra.presentation.theme.*
import com.allterra.presentation.trips.TripsScreen
import com.allterra.presentation.trips.TripViewModel
import com.allterra.presentation.wallet.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
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
            val currentCategory: AllterraCategory = remember(rootState.selectedMainTab, rootState.stage, rootState.overlay) {
                if (rootState.stage != RootStage.MAIN) {
                    AllterraCategory.Neutral
                } else if (rootState.overlay == MainOverlay.PACKING || rootState.overlay == MainOverlay.GEAR_INVENTORY) {
                    AllterraCategory.Gear
                } else {
                    when (rootState.selectedMainTab) {
                        MainTab.HOME -> AllterraCategory.Neutral
                        MainTab.FEED -> AllterraCategory.Social
                        MainTab.MAP -> AllterraCategory.Route
                        MainTab.WALLET -> AllterraCategory.Wallet
                        MainTab.TRIPS -> AllterraCategory.Route
                    }
                }
            }

            AllterraTheme(category = currentCategory) {
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

                        RootStage.AUTH -> AuthScreenContent(
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

                        RootStage.MAIN -> {
                            val feedViewModel: FeedViewModel = koinViewModel()
                            val profileViewModel: ProfileViewModel = koinViewModel()
                            val poisViewModel: PoisViewModel = koinViewModel()
                            val routesViewModel: RoutesViewModel = koinViewModel()
                            val walletViewModel: WalletViewModel = koinViewModel()
                            val tripViewModel: TripViewModel = koinViewModel()
                            val gearViewModel: GearViewModel = koinViewModel()

                            val routesState by routesViewModel.state.collectAsStateWithLifecycle()
                            val poisState by poisViewModel.state.collectAsStateWithLifecycle()
                            val profileState by profileViewModel.state.collectAsStateWithLifecycle()
                            val walletState by walletViewModel.state.collectAsStateWithLifecycle()

                            var showRoutesLibrary by remember { mutableStateOf(false) }

                            LaunchedEffect(rootState.stage) {
                                feedViewModel.refresh()
                                profileViewModel.refresh()
                                walletViewModel.refresh()
                                routesViewModel.refreshRoutes()
                                poisViewModel.refreshPois()
                            }

                            Box(modifier = Modifier.fillMaxSize()) {
                                when {
                                    rootState.overlay == MainOverlay.POIS -> PoisScreen(
                                        viewModel = poisViewModel,
                                        onBack = rootViewModel::closeOverlay,
                                    )

                                    rootState.overlay == MainOverlay.SETTINGS -> SettingsScreen(
                                        onLogout = rootViewModel::onLogout,
                                        onBack = rootViewModel::closeOverlay
                                    )

                                    rootState.overlay == MainOverlay.GEAR_INVENTORY -> GearInventoryScreen(
                                        viewModel = gearViewModel,
                                        onBack = rootViewModel::closeOverlay
                                    )

                                    rootState.overlay == MainOverlay.PACKING -> {
                                        val packingViewModel: PackingViewModel = koinViewModel {
                                            parametersOf(rootState.selectedTripId ?: "")
                                        }
                                        PackingScreen(
                                            viewModel = packingViewModel,
                                            onBack = rootViewModel::closeOverlay
                                        )
                                    }

                                    rootState.overlay == MainOverlay.WALLET_ADD -> AllterraSheet(onDismiss = rootViewModel::closeOverlay) {
                                        WalletAddSheet(
                                            onImportPDF = {},
                                            onImportPhoto = {},
                                            onImportEmail = {},
                                            onScan = {},
                                            onManual = {},
                                            onWalletPass = {},
                                        )
                                    }

                                    rootState.overlay == MainOverlay.WALLET_VIEW -> {
                                        val item = walletState.items.find { it.id == rootState.selectedWalletItemId }
                                        if (item != null) {
                                            AllterraSheet(onDismiss = rootViewModel::closeOverlay) {
                                                WalletItemViewer(
                                                    item = item,
                                                    onOpenOriginal = {},
                                                    onShare = {}
                                                )
                                            }
                                        }
                                    }

                                    rootState.overlay == MainOverlay.PROFILE -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("Profile Placeholder (Task 12)", style = AllterraTheme.typography.displayM)
                                        AllterraButton("Back", modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp)) {
                                            rootViewModel.closeOverlay()
                                        }
                                    }

                                    rootState.selectedMainTab == MainTab.HOME -> DashboardScreen(
                                        userName = profileState.userName.ifBlank { "Explorer" },
                                        onOpenProfile = rootViewModel::openProfile,
                                        onOpenNotifications = {}, 
                                        onOpenWallet = { rootViewModel.onMainTabSelected(MainTab.WALLET) },
                                        onNewPost = { rootViewModel.onMainTabSelected(MainTab.FEED) },
                                        onOpenTripCreate = { rootViewModel.onMainTabSelected(MainTab.TRIPS) },
                                        onOpenGear = rootViewModel::openGearInventory,
                                        onOpenMap = { rootViewModel.onMainTabSelected(MainTab.MAP) },
                                        onSeeAllTrips = { rootViewModel.onMainTabSelected(MainTab.TRIPS) }
                                    )

                                    rootState.selectedMainTab == MainTab.FEED -> FeedScreen(viewModel = feedViewModel)

                                    rootState.selectedMainTab == MainTab.TRIPS -> TripsScreen(
                                        viewModel = tripViewModel,
                                        rootViewModel = rootViewModel,
                                        routes = routesState.items,
                                        walletItems = walletState.items,
                                        onRoutesClick = { showRoutesLibrary = true },
                                    )

                                    rootState.selectedMainTab == MainTab.MAP && showRoutesLibrary -> RoutesScreen(
                                        viewModel = routesViewModel,
                                        onBack = { showRoutesLibrary = false },
                                    )

                                    rootState.selectedMainTab == MainTab.MAP -> MapScreen(
                                        onOpenRoutes = { showRoutesLibrary = true }
                                    )

                                    rootState.selectedMainTab == MainTab.WALLET -> WalletScreen(
                                        items = walletState.items,
                                        onAddItem = rootViewModel::openWalletAdd,
                                        onItemClick = { rootViewModel.openWalletView(it.id) }
                                    )
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
}

private fun AppLanguage.next(): AppLanguage {
    return when (this) {
        AppLanguage.EN -> AppLanguage.RU
        AppLanguage.RU -> AppLanguage.PL
        AppLanguage.PL -> AppLanguage.DE
        AppLanguage.DE -> AppLanguage.EN
    }
}
