# GEMINI.md — Client Application (KMP)

## Core Role
You are a Senior KMP Developer implementing the Allterra Mobile Client.

## Architectural Mandates
- **Stack:** Kotlin Multiplatform, Compose Multiplatform, Ktor, SQLDelight, Koin.
- **Pattern:** Clean Architecture (UI → ViewModel → UseCase → Repository → Data/Network).
- **Source of Truth:** 
  - API Contract: `docs/API.md` (server repo).
  - Business Rules: `PROJECT_CONTEXT.md`.
  - Engineering Rules: `AI_RULES.md`.

## Development Rules
1. **No Mock Data:** Runtime code must never contain hardcoded sample trips, routes, or documents. Use redesigned Empty/Loading/Error states.
2. **Offline-First:** Use SQLDelight for local caching. All files (PDFs/Images) must be stored in encrypted local storage.
3. **Categorical Color:** Respect the design system (Wallet: moss, Route: terra, Gear: ochre, Social: sky) using `tokens.json`.
4. **Networking:** Use Ktor with the centralized `ApiResult` wrapper. Handle 401/Refresh token flow automatically.
5. **Mandatory Build:** After every task or code modification, ALWAYS run `./gradlew :composeApp:compileDebugKotlinAndroid` (and `:composeApp:compileKotlinMetadata` if possible) to verify the build.

## References
- `PROJECT_CONTEXT.md` - Project vision and architecture.
- `AI_RULES.md` - Strict AI behavior constraints.
- `docs/API.md` - Backend contract.
- `README.md` - Build and environment setup.
