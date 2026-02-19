# Allterra Mobile (Kotlin Multiplatform)

Allterra mobile client built with Kotlin Multiplatform + Compose Multiplatform for Android and iOS.

This README defines the current project contract: architecture, environments, build/run flow, and release/deployment practices.

## 1. Stack and goals

- Kotlin Multiplatform (KMP)
- Compose Multiplatform
- Ktor Client
- Kotlinx Serialization
- Koin
- Android + iOS

Goal: a production-oriented client with shared business logic in `commonMain` and minimal platform-specific code.

## 2. Architecture

Dependency flow:

- UI -> ViewModel -> UseCase -> Repository -> Network/Data

Rules:

- UI must not call APIs directly.
- Domain must not depend on platform APIs.
- Network responses must be wrapped in `ApiResult`.
- Raw exceptions must never reach UI.

Key directories:

- `composeApp/src/commonMain/kotlin/com/allterra`
- `composeApp/src/androidMain/kotlin/com/allterra`
- `composeApp/src/iosMain/kotlin/com/allterra`
- `iosApp/` (Xcode entry point)

## 3. Backend contract

API contract source:

- `docs/API.md` (in the server repository)

The client must follow the backend contract. If there is a mismatch, backend contract is the source of truth.

## 4. Environment configuration

Supported environments:

- `LOCAL`
- `STAGING`
- `PRODUCTION`

Parameters:

- `APP_ENV`
- `APP_BASE_URL`

Where values are read:

- Android: `BuildConfig.APP_ENV`, `BuildConfig.APP_BASE_URL`
- iOS: process environment `APP_ENV`, `APP_BASE_URL`

`baseUrl` resolution:

- If `APP_BASE_URL` is provided -> use it.
- If `APP_ENV=LOCAL` and `APP_BASE_URL` is not provided -> use platform local URL:
  - Android: `http://10.0.2.2:8080/api/v1`
  - iOS simulator: `http://localhost:8080/api/v1`
- If `APP_ENV=STAGING|PRODUCTION` and `APP_BASE_URL` is empty -> build fails fast.

Files:

- `composeApp/src/commonMain/kotlin/com/allterra/config/AppConfig.kt`
- `composeApp/src/androidMain/kotlin/com/allterra/config/AppConfig.android.kt`
- `composeApp/src/iosMain/kotlin/com/allterra/config/AppConfig.ios.kt`

## 5. Quick start

Requirements:

- JDK 17+
- Android Studio (latest stable)
- Xcode (for iOS)

Setup checks:

```bash
./gradlew --version
./gradlew :composeApp:tasks
```

Basic local verification:

```bash
./gradlew :composeApp:compileKotlinMetadata
./gradlew :composeApp:compileDebugKotlinAndroid
```

## 6. Build/run commands

Root aliases:

- `./gradlew runLocal`
- `./gradlew runStaging -PAPP_BASE_URL=https://staging.example.com/api/v1`
- `./gradlew runProd -PAPP_BASE_URL=https://api.example.com/api/v1`

What they do:

- `runLocal` -> `:composeApp:assembleDebug` with `LOCAL`
- `runStaging` -> `:composeApp:assembleDebug` with `STAGING`
- `runProd` -> `:composeApp:assembleRelease` with `PRODUCTION`

Important:

- `APP_BASE_URL` is required for `runStaging` and `runProd`.
- You can explicitly override `APP_ENV`:

```bash
./gradlew runLocal -PAPP_ENV=LOCAL
./gradlew runStaging -PAPP_ENV=STAGING -PAPP_BASE_URL=https://staging.example.com/api/v1
./gradlew runProd -PAPP_ENV=PRODUCTION -PAPP_BASE_URL=https://api.example.com/api/v1
```

## 7. Android

Debug build:

```bash
./gradlew :composeApp:assembleDebug
```

Install to device/emulator:

```bash
./gradlew :composeApp:installDebug
```

Release build:

```bash
./gradlew :composeApp:assembleRelease -PAPP_ENV=PRODUCTION -PAPP_BASE_URL=https://api.example.com/api/v1
```

APK/AAB outputs:

- APK usually in `composeApp/build/outputs/apk/`
- AAB in `composeApp/build/outputs/bundle/` (when using bundle tasks)

Signing:

- Use a dedicated release keystore and secure pipeline for production.
- Never commit keystores/secrets.

## 8. iOS

Open project:

- `iosApp/iosApp.xcodeproj`

The KMP framework is embedded via Gradle task from Xcode build phase.

For staging/production, set environment variables in Xcode Scheme:

- `APP_ENV=STAGING` or `APP_ENV=PRODUCTION`
- `APP_BASE_URL=https://.../api/v1`

Local simulator can run without `APP_BASE_URL`.

## 9. Network and error handling

Network layer:

- Ktor client + timeouts
- JSON serialization
- Error mapping into `ApiResult`

`ApiResult` types:

- `Success<T>`
- `ValidationError`
- `Unauthorized`
- `Forbidden`
- `NotFound`
- `ServerError`
- `NetworkError`
- `UnknownError`

Requirement:

- UI receives safe domain/UI state, not raw exceptions.

## 10. Security

- Do not hardcode production URLs in business logic.
- Do not log tokens or personal data.
- Use HTTPS in production.
- Store JWT securely (Android EncryptedSharedPreferences, iOS Keychain).

## 11. Testing

Baseline commands:

```bash
./gradlew :composeApp:test
./gradlew :composeApp:compileKotlinMetadata
./gradlew :composeApp:compileDebugKotlinAndroid
```

Priority coverage:

- use cases
- repository mappings
- error mappings (network -> ApiResult)

## 12. CI/CD (recommended baseline)

Minimum pipeline:

1. `:composeApp:compileKotlinMetadata`
2. `:composeApp:compileDebugKotlinAndroid`
3. `:composeApp:test`

Release branch/tag pipeline:

1. Android release build with `APP_ENV=PRODUCTION` and `APP_BASE_URL`
2. Artifact signing
3. Publish to internal track (Google Play) / TestFlight (iOS)

Secrets policy:

- Keep secrets only in CI secret storage
- Never commit secrets to repository

## 13. Deployment

Android (Google Play):

1. Build release artifact (AAB)
2. Sign with release key
3. Upload to Play Console (internal -> closed -> production)
4. Verify crash-free, network, auth, refresh flow

iOS (App Store Connect/TestFlight):

1. Build archive in Xcode
2. Upload to App Store Connect
3. Pass TestFlight validation
4. Roll out with phased strategy

## 14. Troubleshooting

`APP_BASE_URL is required when APP_ENV is STAGING or PRODUCTION`

- `STAGING/PRODUCTION` selected without `APP_BASE_URL`.

`Server is unavailable` / `Unable to reach server`

- Backend is not running or is unreachable by URL.
- For Android emulator local backend must be available via `10.0.2.2`.

`401/403` after login

- Verify access/refresh flow and API contract alignment with backend `API.md`.

## 15. Important documents

- `PROJECT_CONTEXT.md`
- `AI_RULES.md`
- `docs/API.md` (server repo)

When API contract changes, update backend API docs first, then client models/mappings.
