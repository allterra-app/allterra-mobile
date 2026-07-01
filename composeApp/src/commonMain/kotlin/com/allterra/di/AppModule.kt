package com.allterra.di

import com.allterra.data.local.PersistentTokenStorage
import com.allterra.data.local.SessionPreferences
import com.allterra.data.local.SessionPreferencesImpl
import com.allterra.data.local.TokenStorage
import com.allterra.data.repository.*
import com.allterra.domain.repository.*
import com.allterra.domain.usecase.LoginUseCase
import com.allterra.domain.usecase.RegisterUseCase
import com.allterra.network.auth.AuthApi
import com.allterra.network.auth.AuthApiImpl
import com.allterra.network.createHttpClient
import com.allterra.network.document.DocumentApi
import com.allterra.network.gear.GearApi
import com.allterra.network.media.MediaApi
import com.allterra.network.media.MediaApiImpl
import com.allterra.network.notification.NotificationApi
import com.allterra.network.notification.NotificationApiImpl
import com.allterra.network.packing.PackingApi
import com.allterra.network.poi.PoiApi
import com.allterra.network.poi.PoiApiImpl
import com.allterra.network.poi.PoiPhotoApi
import com.allterra.network.poi.PoiPhotoApiImpl
import com.allterra.network.post.PostApi
import com.allterra.network.post.PostApiImpl
import com.allterra.network.post.PostPhotoApi
import com.allterra.network.post.PostPhotoApiImpl
import com.allterra.network.route.RouteApi
import com.allterra.network.route.RouteApiImpl
import com.allterra.network.trip.TripApi
import com.allterra.network.user.UserApi
import com.allterra.network.user.UserApiImpl
import com.allterra.presentation.auth.AuthViewModel
import com.allterra.presentation.feed.FeedViewModel
import com.allterra.presentation.gear.GearViewModel
import com.allterra.presentation.packing.PackingViewModel
import com.allterra.presentation.pois.PoisViewModel
import com.allterra.presentation.profile.ProfileViewModel
import com.allterra.presentation.root.RootViewModel
import com.allterra.presentation.routes.RoutesViewModel
import com.allterra.presentation.trips.TripViewModel
import com.allterra.presentation.wallet.WalletViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { createHttpClient() }
    single<AuthApi> { AuthApiImpl(httpClient = get()) }
    single<UserApi> { UserApiImpl(httpClient = get(), tokenStorage = get()) }
    single { DocumentApi(httpClient = get(), tokenStorage = get()) }
    single { TripApi(httpClient = get(), tokenStorage = get()) }
    single { GearApi(httpClient = get(), tokenStorage = get()) }
    single { PackingApi(httpClient = get(), tokenStorage = get()) }
    single<RouteApi> { RouteApiImpl(httpClient = get(), tokenStorage = get()) }
    single<PoiApi> { PoiApiImpl(httpClient = get(), tokenStorage = get()) }
    single<PoiPhotoApi> { PoiPhotoApiImpl(httpClient = get(), tokenStorage = get()) }
    single<PostApi> { PostApiImpl(httpClient = get(), tokenStorage = get()) }
    single<PostPhotoApi> { PostPhotoApiImpl(httpClient = get(), tokenStorage = get()) }
    single<MediaApi> { MediaApiImpl(httpClient = get(), tokenStorage = get()) }
    single<NotificationApi> { NotificationApiImpl(httpClient = get(), tokenStorage = get()) }
    single<TokenStorage> { PersistentTokenStorage() }
    single<SessionPreferences> { SessionPreferencesImpl() }
    single<AuthRepository> { AuthRepositoryImpl(authApi = get(), tokenStorage = get()) }
    single<RoutesRepository> { RoutesRepositoryImpl(userApi = get(), routeApi = get(), mediaApi = get()) }
    single<PoisRepository> { PoisRepositoryImpl(userApi = get(), poiApi = get(), poiPhotoApi = get(), mediaApi = get()) }
    single<WalletRepository> { WalletRepositoryImpl(api = get()) }
    single<TripRepository> { TripRepositoryImpl(api = get()) }
    single<GearRepository> { GearRepositoryImpl(api = get()) }
    single<PackingRepository> { PackingRepositoryImpl(packingApi = get()) }
    single<PostsRepository> {
        PostsRepositoryImpl(
            userApi = get(),
            postApi = get(),
            postPhotoApi = get(),
            mediaApi = get(),
            notificationApi = get(),
        )
    }

    factory { LoginUseCase(authRepository = get()) }
    factory { RegisterUseCase(authRepository = get()) }

    viewModel {
        RootViewModel(
            sessionPreferences = get(),
            tokenStorage = get(),
        )
    }

    viewModel {
        AuthViewModel(
            loginUseCase = get(),
            registerUseCase = get(),
        )
    }

    viewModel { FeedViewModel(postsRepository = get()) }
    viewModel { ProfileViewModel(postsRepository = get()) }
    viewModel { PoisViewModel(poisRepository = get()) }
    viewModel { RoutesViewModel(routesRepository = get()) }
    viewModel { WalletViewModel(repository = get()) }
    viewModel { TripViewModel(repository = get()) }
    viewModel { GearViewModel(repository = get()) }
    viewModel { (tripId: String) -> PackingViewModel(packingRepository = get(), tripId = tripId) }
}
