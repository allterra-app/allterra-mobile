package com.allterra.di

import com.allterra.data.local.PersistentTokenStorage
import com.allterra.data.local.SessionPreferences
import com.allterra.data.local.SessionPreferencesImpl
import com.allterra.data.local.TokenStorage
import com.allterra.data.repository.AuthRepositoryImpl
import com.allterra.data.repository.PoisRepositoryImpl
import com.allterra.data.repository.PostsRepositoryImpl
import com.allterra.data.repository.RoutesRepositoryImpl
import com.allterra.domain.repository.AuthRepository
import com.allterra.domain.repository.PoisRepository
import com.allterra.domain.repository.PostsRepository
import com.allterra.domain.repository.RoutesRepository
import com.allterra.domain.usecase.LoginUseCase
import com.allterra.domain.usecase.RegisterUseCase
import com.allterra.network.auth.AuthApi
import com.allterra.network.auth.AuthApiImpl
import com.allterra.network.createHttpClient
import com.allterra.network.media.MediaApi
import com.allterra.network.media.MediaApiImpl
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
import com.allterra.network.user.UserApi
import com.allterra.network.user.UserApiImpl
import com.allterra.presentation.auth.AuthViewModel
import com.allterra.presentation.feed.FeedViewModel
import com.allterra.presentation.pois.PoisViewModel
import com.allterra.presentation.profile.ProfileViewModel
import com.allterra.presentation.root.RootViewModel
import com.allterra.presentation.routes.RoutesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { createHttpClient() }
    single<AuthApi> { AuthApiImpl(httpClient = get()) }
    single<UserApi> { UserApiImpl(httpClient = get(), tokenStorage = get()) }
    single<RouteApi> { RouteApiImpl(httpClient = get(), tokenStorage = get()) }
    single<PoiApi> { PoiApiImpl(httpClient = get(), tokenStorage = get()) }
    single<PoiPhotoApi> { PoiPhotoApiImpl(httpClient = get(), tokenStorage = get()) }
    single<PostApi> { PostApiImpl(httpClient = get(), tokenStorage = get()) }
    single<PostPhotoApi> { PostPhotoApiImpl(httpClient = get(), tokenStorage = get()) }
    single<MediaApi> { MediaApiImpl(httpClient = get(), tokenStorage = get()) }
    single<TokenStorage> { PersistentTokenStorage() }
    single<SessionPreferences> { SessionPreferencesImpl() }
    single<AuthRepository> { AuthRepositoryImpl(authApi = get(), tokenStorage = get()) }
    single<RoutesRepository> { RoutesRepositoryImpl(userApi = get(), routeApi = get(), mediaApi = get()) }
    single<PoisRepository> { PoisRepositoryImpl(userApi = get(), poiApi = get(), poiPhotoApi = get(), mediaApi = get()) }
    single<PostsRepository> {
        PostsRepositoryImpl(
            userApi = get(),
            postApi = get(),
            postPhotoApi = get(),
            mediaApi = get(),
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
}
