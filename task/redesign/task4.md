# Plan: Task 4 - Main Navigation & Dashboard (Home)

## 📱 Client (Mobile - Kotlin Multiplatform)

### 1. Navigation Restructuring
- [ ] Update `BottomTabBar` to match the redesign tokens (floating, 22dp radius, moss accent).
- [ ] Ensure the 5 tabs are correctly represented: Home, Feed, Map, Wallet, Trips.

### 2. Implementation of Dashboard (Home)
- [ ] **DashboardScreen**:
    - Background: `bg` (neutral).
    - Greeting section with `AllterraAvatar`.
    - "Upcoming Trip" card using `AllterraCard`.
    - "Packing Progress" linear widget.
    - Weather widget placeholder.
    - "Recent Trips" horizontal list using small `AllterraCard`s.
    - Quick actions grid (Packing, New Post, Add Doc).

### 3. Component Updates
- [ ] Implement `AllterraProgressBar` (Redesign style: 6dp height, moss fill).
- [ ] Add `AllterraTabPill` (if needed for section switching).

### 4. Integration
- [ ] Connect `DashboardScreen` to `RootViewModel` and `App.kt`.
- [ ] Ensure navigation between Dashboard and other tabs works seamlessly.

### 5. Validation
- [ ] Verify the layout on different screen sizes (Android/iOS).
- [ ] Check dark mode transitions for all Dashboard elements.

---

## 🖥️ Server (Spring Boot)
- [ ] **No changes required** (assuming existing APIs cover basic dashboard needs for now).

---

## 🛠️ Branch Pattern
- Branch: `/feature/redesign/task4`
