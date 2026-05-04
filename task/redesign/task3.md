# Plan: Task 3 - Splash Screen & Onboarding Flow

## 📱 Client (Mobile - Kotlin Multiplatform)

### 1. Resources & Animation Setup
- [ ] Add icon/illustration resources for onboarding steps (Wallet, Map, Gear, Share) if available, or use placeholders based on categorical icons.
- [ ] Define animation constants for the Splash Screen (bounce, fade-in).

### 2. Implementation of Splash Screen
- [ ] **SplashScreen**:
    - Pastel mountain background (using `bgSub` or custom gradient).
    - Red pin drop animation using `Compose Animation`.
    - Allterra wordmark fade-in effect.
    - Logic to navigate to Onboarding or Dashboard after animation completes.

### 3. Implementation of Onboarding Flow
- [ ] **OnboardingOverview**:
    - 2x2 grid layout showing the four main features.
    - Categorical colors for each quadrant.
- [ ] **OnboardingStep**:
    - Reusable pager-like screen for each feature.
    - Title, description, and feature-specific illustration.
    - Progress dots at the bottom.
    - "Next" and "Skip" buttons using `AllterraButton`.

### 4. Navigation & State
- [ ] Integrate with the navigation system (Decompose/Voyager).
- [ ] Use a `SettingsRepository` (or simple `DataStore`) to persist whether the user has completed onboarding.

### 5. Validation
- [ ] Add the Onboarding flow to `ThemePreviewScreen` or create a temporary debug entry point in `App.kt`.

---

## 🖥️ Server (Spring Boot)
- [ ] **No changes required**.

---

## 🛠️ Branch Pattern
- Branch: `/feature/redesign/task3`
