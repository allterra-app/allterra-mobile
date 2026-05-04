# Plan: Task 1 - Design Tokens & Theme Foundation

## 📱 Client (Mobile - Kotlin Multiplatform)

### 1. Resource Preparation
- [ ] Download and add font files to `commonMain/composeResources/font/`:
    - `Manrope` (Regular, Medium, SemiBold, Bold)
    - `Space Grotesk` (SemiBold)
    - `JetBrains Mono` (Medium)
- [ ] Define font families in code using `FontFamily`.

### 2. Implementation of Design Tokens (`commonMain`)
- [ ] **Colors**:
    - Implement `AllterraColors` data class.
    - Define `LightPalette` and `DarkPalette` based on `tokens.json`.
    - Implement `CategoricalColors` (Moss, Terra, Ochre, Sky) and integrate them into the theme.
- [ ] **Typography**:
    - Create `AllterraTypography` object with all scales: `DisplayXL`, `DisplayL`, `DisplayM`, `Title`, `Body`, `BodyStrong`, `Small`, `SmallStrong`, `Caption`, `Tab`, `Mono`.
- [ ] **Shapes & Spacing**:
    - Define `AllterraRadius` (xs, sm, md, lg, xl, etc.).
    - Define `AllterraSpacing` constants.
- [ ] **Elevation**:
    - Implement `AllterraElevation` for light and dark modes.

### 3. Theme Integration
- [ ] Create `AllterraTheme` composable.
- [ ] Use `CompositionLocalProvider` to make colors, typography, and spacing available throughout the app.
- [ ] Implement a basic Theme Switcher (Light/Dark/System) for testing purposes.

### 4. Validation
- [ ] Create a "Theme Preview" screen to verify all colors, fonts, and components' default styles.

---

## 🖥️ Server (Spring Boot)

### 1. Task Analysis
- [ ] **No changes required** for this task. The design tokens and theme foundation are strictly UI-related and implemented on the client side.
- [ ] (Future) In later tasks, we might add a `theme` field to the user profile API to persist user preference.

---

## 🛠️ Branch Pattern
- Branch: `/feature/redesign/task1`
