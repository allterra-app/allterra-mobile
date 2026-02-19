# PROJECT_CONTEXT.md

# Mobile Client (Kotlin Multiplatform – Android + iOS)

---

## 1. Project Overview

This project is a cross-platform mobile application built with:

- Kotlin Multiplatform (KMP)
- Compose Multiplatform
- Android Studio as the primary IDE

Target platforms:

- Android (Google Play)
- iOS (App Store)

The backend is a separate Java (Spring Boot) service.

This is a commercial product in development (alpha → beta → production).

The project is developed solo with AI assistance and must remain:

- Clean
- Secure
- Scalable
- Production-ready

---

## 2. Architecture Principles

The application follows:

- Clean Architecture
- Feature-based modular structure
- Strict separation of:
    - UI
    - Domain
    - Data
    - Networking

Shared business logic must live in `commonMain`.

Platform-specific code must be isolated in `androidMain` and `iosMain`.

UI must never call API directly.

---

## 3. Tech Stack

### Core

- Kotlin Multiplatform
- Compose Multiplatform
- Coroutines
- Flow / StateFlow
- Ktor client
- Kotlinx.serialization

### Dependency Injection

- Koin (preferred for simplicity in KMP)

### Local Storage

- SQLDelight (preferred)
  OR
- Platform-specific storage if necessary

### Secure Storage

- Android: EncryptedSharedPreferences
- iOS: Keychain

JWT tokens must NEVER be stored in plain SharedPreferences.

---

## 4. Backend Contract

Backend API contract is defined in: docs/API.md


This file is the single source of truth for:

- Endpoints
- Request/Response models
- Authentication flow
- Error handling
- Access control

If contract mismatch occurs:
- Backend specification wins
- Client models must be updated

---

## 5. Authentication Model

Backend uses:

- JWT Access Token
- Refresh Token

Client responsibilities:

- Store tokens securely
- Attach `Authorization: Bearer <token>` header
- Handle 401 responses
- Attempt token refresh once
- Logout user if refresh fails

No business logic may depend on UI state.

---

## 6. Project Structure

Example:

shared/
├── commonMain/
│ ├── domain/
│ ├── data/
│ ├── network/
│ ├── repository/
│ └── usecase/
│
├── androidMain/
└── iosMain/

androidApp/
iosApp/


Rules:

- UI → ViewModel
- ViewModel → UseCase
- UseCase → Repository
- Repository → API client

Never violate dependency direction.

---

## 7. Networking Layer

Must include:

- Ktor HTTP client
- JSON serialization
- Timeout configuration
- Centralized error handling
- Debug logging (disabled in release)

All API calls must return:

sealed class ApiResult


Example:

- Success<T>
- NetworkError
- ServerError
- Unauthorized
- UnknownError

No raw exceptions should reach UI.

---

## 8. Error Handling Strategy

Convert backend errors into domain-level results.

UI layer must receive:

sealed class UiState


Examples:

- Loading
- Success
- Error(message)

Never expose raw backend JSON errors directly to UI.

---

## 9. Environment Configuration

Must support:

- Local
- Staging
- Production

Base URL must NOT be hardcoded in business logic.

Use:

- BuildConfig (Android)
- Platform configuration for iOS

Switching environments must be easy and centralized.

---

## 10. Security Requirements

From early stage:

- HTTPS only
- No plain HTTP
- Secure token storage
- No sensitive logs in production
- Obfuscation enabled for release builds
- Certificate pinning (production stage)

---

## 11. Payments Strategy

Payments handled via:

- Google Play Billing (Android)
- Apple In-App Purchases (iOS)

Do NOT implement custom payment processing in mobile client.

Client responsibilities:

- Trigger purchase flow
- Send receipt to backend
- Update local subscription state

Server validates receipts.

---

## 12. Offline Strategy (Phase 2)

Initial phase:

- Online-first

Future phase:

- Cache user profile
- Cache feed
- Background synchronization

---

## 13. Analytics & Monitoring (Pre-Production)

Before public release:

- Firebase Analytics
- Crash reporting
- Performance monitoring

Analytics must not break core functionality.

---

## 14. Testing Strategy

Minimum:

- Unit tests for use cases
- Repository tests
- ViewModel tests

Later:

- UI tests
- Integration tests

---

## 15. AI Agent Rules

This project is AI-assisted.

When generating code:

1. Follow Clean Architecture strictly.
2. Do not create tight coupling.
3. Respect API.md contract.
4. Avoid unnecessary abstractions.
5. Keep solution production-oriented.
6. Do not introduce heavy frameworks without strong justification.
7. Think long-term scalability.

When modifying architecture:

- Explain trade-offs
- Avoid premature optimization
- Avoid microservices mindset

---

## 16. Current Stage

Phase: Early Alpha

Backend:

- JWT auth
- CRUD
- Dockerized

Client:

- Architecture phase
- UI not implemented yet

Target:

- Internal alpha (~50 users)
- Then store beta release

---

## 17. Long-Term Vision

This is a commercial product.

Goals:

- Publish in stores
- Monetize via subscription
- Scale beyond MVP
- Maintain clean architecture
- Avoid rewrites due to poor early decisions

All decisions must consider long-term growth.

---

End of document.


