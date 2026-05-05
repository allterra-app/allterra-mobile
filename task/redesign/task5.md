# Plan: Task 5 - Wallet Module (Categorical: Moss)

## 📱 Client (Mobile - Kotlin Multiplatform)

### 1. Module Foundation
- [ ] Create `commonMain/kotlin/com/allterra/presentation/wallet/` directory.
- [ ] Define `WalletItem` domain/data models (Title, Category, Expiry Date, File Reference).

### 2. Implementation of Wallet Screen
- [ ] **WalletScreen**:
    - Categorical background: `mossSoft` (Light) / Moss-tinted (Dark).
    - Grouped list: "Active Tickets", "Bookings", "Insurance".
    - `AllterraCard` for each item with Moss accents.
    - Offline status indicator (Moss chip).
- [ ] **WalletItemRow**:
    - Item icon based on category.
    - Title, sub-details (e.g., flight number, hotel name).
    - Status badge.

### 3. Document Import & Viewer
- [ ] **WalletAddSheet**:
    - Grid of import options: PDF, Photo, Email, Scan, Apple/Google Wallet.
    - Uses `AllterraIconButton` and `AllterraTheme.typography.tab`.
- [ ] **WalletItemViewer**:
    - Hero section with categorical Moss background.
    - Large QR code display.
    - Meta-data grid (Date, Time, Location).
    - "Open PDF" and "Share" buttons.

### 4. Component Refinement
- [ ] Implement `AllterraSheet` (Redesign style: 24dp top radius, slide-up animation).

### 5. Integration
- [ ] Connect `WalletScreen` in `App.kt` (replace the current placeholder).
- [ ] Add basic state management for the wallet list (mock data initially).

### 6. Validation
- [ ] Verify categorical coloring consistency.
- [ ] Test the "Add" sheet interaction.

---

## 🖥️ Server (Spring Boot)
- [ ] (Optional) Review existing File/Photo APIs to ensure they support Wallet metadata requirements. No immediate changes planned for this task.

---

## 🛠️ Branch Pattern
- Branch: `/feature/redesign/task5`
