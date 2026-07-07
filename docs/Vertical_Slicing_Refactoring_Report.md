# GymTrack — Vertical Slicing Refactoring Report

**Student:** Louis Francis Lim  
**Course:** IT342-G01 — Systems Integration and Architecture 1  
**Repository:** https://github.com/louisflim/gymtrack  
**Approach:** Option B — refactor on `main` going forward; one feature per new commit.

---

## What is vertical slicing?

**Before (horizontal):** code grouped by technical layer (`controller/`, `service/`, `pages/`, `api/`).

**After (vertical):** code grouped by **feature**, each slice owns UI + API client + backend endpoint + service logic for that capability.

```
feature/auth/registration/
  Backend:  RegistrationController, RegistrationService, RegisterRequest DTO
  Web:      RegisterPage, RegisterForm, registration/api.js
  Mobile:   RegisterFragment, RegistrationViewModel, RegistrationRepository
```

Shared infrastructure (`User` entity, `UserRepository`, JWT, session) remains in shared packages until later slices extract their own domain.

---

## Commit log (fill in hash after you commit)

| # | Feature | Commit message | Commit hash | Date |
|---|---------|----------------|-------------|------|
| 1 | User Registration | `refactor(auth): vertical slice for user registration` | _paste after commit_ | _paste_ |
| 2 | User Login | `refactor(auth): vertical slice for user login` | pending | |
| 3 | Staff creation | `refactor(auth): vertical slice for admin staff creation` | pending | |
| 4 | Subscription plans | `refactor(plans): vertical slice for plan management` | pending | |
| 5 | Member management | `refactor(members): vertical slice for member management` | pending | |
| 6 | Membership status | `refactor(membership): vertical slice for membership` | pending | |
| 7 | Online payment | `refactor(payments): vertical slice for PayMongo checkout` | pending | |
| 8 | QR attendance | `refactor(attendance): vertical slice for QR check-in/out` | pending | |
| 9 | Admin dashboard | `refactor(dashboard): vertical slice for admin KPIs` | pending | |
| 10 | Staff management | `refactor(staff): vertical slice for staff accounts` | pending | |

---

## Slice 1 — User Registration (ready to commit)

### Purpose
Allow Gym Owners and Members to self-register via a single form with role selection (FR-001, FR-002).

### Files moved / created

**Backend**
| File | Responsibility |
|------|----------------|
| `feature/auth/registration/RegistrationController.java` | `POST /api/auth/register` |
| `feature/auth/registration/RegistrationService.java` | Registration business logic |
| `feature/auth/registration/dto/RegisterRequest.java` | Request payload |

**Web**
| File | Responsibility |
|------|----------------|
| `features/auth/registration/RegisterPage.jsx` | Registration screen |
| `features/auth/registration/RegisterForm.jsx` | Form UI |
| `features/auth/registration/api.js` | `registerUser()` API call |

**Mobile**
| File | Responsibility |
|------|----------------|
| `feature/auth/registration/RegisterFragment.kt` | Registration screen |
| `feature/auth/registration/RegistrationViewModel.kt` | UI state + validation |
| `feature/auth/registration/RegistrationRepository.kt` | API call + session save |

### API
- `POST /api/auth/register` — creates Admin (with gym) or Member account

### Database
- `users` — new user row
- `gyms` — created when role is ADMIN

### Flow
1. User fills form on web or mobile.
2. Client calls `POST /api/auth/register`.
3. `RegistrationService` validates role, hashes password, optionally creates gym.
4. JWT returned; client stores session.
5. User navigates to dashboard.

### Removed from horizontal packages
- `register()` removed from `AuthController` / `AuthService`
- `registerUser` removed from `web/src/api/auth.js`
- `register()` removed from mobile `AuthRepository` / `AuthViewModel`
- Deleted `web/src/pages/Register/*`

### Testing
- [ ] Register as Member on web → lands on dashboard
- [ ] Register as Gym Owner with gym name → dashboard shows admin role
- [ ] Duplicate email → error message
- [ ] Same flows on Android app

### Screenshot
_[Insert screenshot of registration success]_

---

## Slice 2 — User Login (ready to commit)

### Purpose
Allow registered users (Admin, Staff, Member) to sign in with email and password (FR-003).

### Files moved / created

**Backend**
| File | Responsibility |
|------|----------------|
| `feature/auth/login/LoginController.java` | `POST /api/auth/login` |
| `feature/auth/login/LoginService.java` | Authentication + JWT issuance |
| `feature/auth/login/dto/LoginRequest.java` | Request payload |

**Web**
| File | Responsibility |
|------|----------------|
| `features/auth/login/LoginPage.jsx` | Login screen |
| `features/auth/login/LoginForm.jsx` | Form UI |
| `features/auth/login/api.js` | `loginUser()` API call |

**Mobile**
| File | Responsibility |
|------|----------------|
| `feature/auth/login/LoginFragment.kt` | Login screen |
| `feature/auth/login/LoginViewModel.kt` | UI state |
| `feature/auth/login/LoginRepository.kt` | API call + session save |

### API
- `POST /api/auth/login` — validates credentials, returns JWT + user profile

### Database
- `users` — read-only lookup by email

### Flow
1. User enters email and password.
2. Client calls `POST /api/auth/login`.
3. `LoginService` checks account is active, authenticates via Spring Security, issues JWT.
4. Client stores session and navigates to dashboard.

### Removed from horizontal packages
- `login()` removed from `AuthController` / `AuthService`
- `loginUser` removed from `web/src/api/auth.js`
- `login()` removed from mobile `AuthRepository` / `AuthViewModel`
- Deleted `web/src/pages/Login/*`
- `AuthViewModel` now only handles session + logout (used by dashboard)

### Testing
- [ ] Sign in with valid credentials on web → dashboard
- [ ] Wrong password → "Invalid email or password"
- [ ] Deactivated account → forbidden message
- [ ] Same flows on Android app
- [ ] Sign out from dashboard → returns to login

### Screenshot
_[Insert screenshot of login success]_

---

## Next slice
When ready, say **"continue with staff creation slice"** — staff creation will move to `feature/auth/staff/`.
