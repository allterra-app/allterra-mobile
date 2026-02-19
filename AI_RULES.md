# AI_RULES.md
# Client Application (Kotlin Multiplatform – Android + iOS)

---

## 1. Purpose

This file defines strict rules for AI agents working on this project.

AI must follow these rules when:

- Generating code
- Refactoring
- Suggesting architecture
- Modifying existing modules
- Adding dependencies

This project is commercial and production-oriented.

Do NOT treat it as a demo or experimental project.

---

## 2. Source of Truth

AI must always respect:

1. PROJECT_CONTEXT.md
2. API.md (backend contract)
3. Existing project structure

If conflicts arise:
- API.md wins for networking contracts.
- PROJECT_CONTEXT.md wins for architecture rules.

Never invent API endpoints or fields not defined in API.md.

---

## 3. Architecture Discipline

This project follows Clean Architecture.

Allowed dependency direction:

UI → ViewModel → UseCase → Repository → Network/Data

Forbidden:

- UI directly calling API
- ViewModel accessing network layer directly
- Domain layer depending on Android/iOS platform APIs
- Circular dependencies

All shared business logic must live in `commonMain`.

Platform-specific logic must be minimal and isolated.

---

## 4. Code Generation Principles

When generating code:

- Prefer explicit types over implicit behavior.
- Avoid magic.
- Keep solutions simple and maintainable.
- Avoid overengineering.
- Avoid unnecessary abstractions.
- Avoid introducing new architectural layers without request.

Do NOT:

- Introduce microservices mindset.
- Add complex reactive frameworks.
- Add experimental libraries.
- Add heavy dependency injection frameworks beyond Koin.
- Suggest premature optimization.

---

## 5. Networking Rules

Networking must:

- Use Ktor client.
- Centralize configuration.
- Implement proper timeout handling.
- Attach JWT token automatically.
- Handle 401 with refresh logic.
- Never expose raw exceptions to UI.

All network responses must be wrapped in:

sealed class ApiResult


Never return raw DTO directly to UI.

DTOs must be mapped to domain models.

---

## 6. Error Handling Rules

AI must:

- Convert backend errors into domain-safe results.
- Avoid leaking backend error structures to UI.
- Use sealed classes for UI state.

Never:

- Throw raw exceptions to UI layer.
- Log sensitive data (tokens, personal info).

---

## 7. Security Constraints

Strict requirements:

- Never store JWT in plain SharedPreferences.
- Never log tokens.
- Never hardcode secrets.
- Never disable HTTPS checks.
- No debug logging in release builds.

If security trade-off exists:
- Explain it.
- Default to safer option.

---

## 8. Environment Rules

Base URL must:

- Be configurable.
- Not be hardcoded in business logic.

AI must not:

- Embed environment-specific URLs inside domain or repository code.

---

## 9. Payments Rules

Payments must:

- Use platform billing APIs only.
- Never process raw credit card data.
- Send receipts to backend for validation.

AI must not:

- Suggest custom payment flows.
- Suggest bypassing store billing.

---

## 10. Testing Discipline

When generating new logic:

- Suggest unit tests for UseCases.
- Avoid UI-only logic tests.
- Keep test scope realistic.

Do not generate massive test scaffolding unless requested.

---

## 11. Refactoring Rules

Before refactoring:

- Check if change breaks Clean Architecture.
- Avoid large rewrites unless explicitly requested.
- Preserve backward compatibility with API.md.

If refactor is major:
- Explain trade-offs.
- Propose migration path.

---

## 12. Performance & Scalability

Assume:

- 50 users initially.
- Future scaling expected.

AI must:

- Avoid premature optimization.
- Avoid architecture that blocks future scaling.
- Keep data models flexible.

---

## 13. AI Behavior Constraints

AI must:

- Explain reasoning when making architectural suggestions.
- Keep answers concise unless asked for deep explanation.
- Prefer deterministic patterns over experimental solutions.

AI must NOT:

- Invent requirements.
- Add features not requested.
- Modify authentication flow without explicit instruction.
- Change API contract.
- Introduce breaking changes silently.

---

## 14. When Uncertain

If information is missing:

- Ask a clarifying question.
- Do not assume backend changes.
- Do not hallucinate API behavior.

---

## 15. Priority Order

When generating code, prioritize:

1. Security
2. Architectural consistency
3. API contract correctness
4. Maintainability
5. Performance

---

## 16. Commercial Mindset

This is not a tutorial project.

Every suggestion must consider:

- Store publishing requirements
- GDPR compliance
- Long-term maintainability
- Production readiness

---

End of file.
