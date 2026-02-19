import org.gradle.api.GradleException
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
}

val requestedTasks = gradle.startParameter.taskNames
val envFromTask = when {
    requestedTasks.any { it.endsWith("runStaging") || it.contains(":runStaging") } -> "STAGING"
    requestedTasks.any { it.endsWith("runProd") || it.contains(":runProd") } -> "PRODUCTION"
    requestedTasks.any { it.endsWith("runLocal") || it.contains(":runLocal") } -> "LOCAL"
    else -> null
}

val appEnv = providers.gradleProperty("APP_ENV")
    .orElse(providers.environmentVariable("APP_ENV"))
    .orElse(envFromTask ?: "LOCAL")
    .get()

val appBaseUrl = providers.gradleProperty("APP_BASE_URL")
    .orElse(providers.environmentVariable("APP_BASE_URL"))
    .orElse("")
    .get()

if (appEnv.equals("STAGING", ignoreCase = true) || appEnv.equals("PRODUCTION", ignoreCase = true)) {
    if (appBaseUrl.isBlank()) {
        throw GradleException("APP_BASE_URL is required when APP_ENV is STAGING or PRODUCTION")
    }
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.ui.text.google.fonts)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)

            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.lifecycle.viewmodel)
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.android)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.ios)
        }
    }
}

android {
    namespace = "com.allterra"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.allterra"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "APP_ENV", "\"$appEnv\"")
        buildConfigField("String", "APP_BASE_URL", "\"$appBaseUrl\"")
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

tasks.register("runLocal") {
    group = "application"
    description = "Assemble Android debug build with LOCAL environment"
    dependsOn("assembleDebug")
}

tasks.register("runStaging") {
    group = "application"
    description = "Assemble Android debug build with STAGING environment (requires APP_BASE_URL)"
    dependsOn("assembleDebug")
}

tasks.register("runProd") {
    group = "application"
    description = "Assemble Android release build with PRODUCTION environment (requires APP_BASE_URL)"
    dependsOn("assembleRelease")
}

dependencies {
    debugImplementation(compose.uiTooling)
}
