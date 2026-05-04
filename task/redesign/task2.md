# Plan: Task 2 - Core UI Components (Atoms & Molecules)

## 📱 Client (Mobile - Kotlin Multiplatform)

### 1. Component Library Setup
- [ ] Create a new directory `commonMain/kotlin/com/allterra/presentation/common/components/redesign/` to host the new components.
- [ ] Implement interaction feedback: Create a `Modifier.allterraClickable` that includes the scale-down animation from `tokens.json` (`motion.press`).

### 2. Implementation of Atomic Components
- [ ] **AllterraButton**:
    - Variants: Primary (Moss), Secondary (Surface/Ink), Ghost, Terra.
    - Support for leading/trailing icons.
    - Standard and Small heights (48dp, 36dp).
- [ ] **AllterraIconButton**:
    - Square with rounded corners (12dp).
    - Border and background as per tokens.
- [ ] **AllterraCard**:
    - Shadow (sh1), border, and standard padding (14dp).
    - Support for categorical background colors.
- [ ] **AllterraInput**:
    - Custom TextField styling with focus borders and typography.
- [ ] **AllterraChip**:
    - Pill-shaped labels with categorical background/text colors.

### 3. Implementation of Complex Components (Molecules)
- [ ] **AllterraAvatar**:
    - Support for sizes (sm, md, lg) and border.
- [ ] **AllterraCheckbox & AllterraToggle**:
    - Custom visuals matching the redesign style.
- [ ] **AllterraTabbar (Shell)**:
    - Initial implementation of the floating 5-tab navigation bar (visual only for now).

### 4. Validation
- [ ] Update `ThemePreviewScreen` to include a "Components" section showing all new UI elements in different states and categories.

---

## 🖥️ Server (Spring Boot)
- [ ] **No changes required**.

---

## 🛠️ Branch Pattern
- Branch: `/feature/redesign/task2`
