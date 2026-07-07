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

## Slice 3 — Admin Staff Creation (ready to commit)

### Purpose
Allow Gym Owners (Admin) to create Staff accounts linked to their gym (FR-004).

### Files moved / created

**Backend**
| File | Responsibility |
|------|----------------|
| `feature/auth/staff/StaffCreationController.java` | `POST /api/auth/staff` |
| `feature/auth/staff/StaffCreationService.java` | Staff account creation logic |
| `feature/auth/staff/dto/CreateStaffRequest.java` | Request payload |
| `feature/auth/staff/dto/StaffAccountResponse.java` | Created staff summary |

**Web**
| File | Responsibility |
|------|----------------|
| `features/auth/staff/CreateStaffForm.jsx` | Staff creation form (admin dashboard) |
| `features/auth/staff/api.js` | `createStaffUser()` API call |

**Mobile**
| File | Responsibility |
|------|----------------|
| `feature/auth/staff/StaffCreationRepository.kt` | API call for staff creation |

### API
- `POST /api/auth/staff` — Admin-only; creates STAFF user linked to admin's gym

### Database
- `users` — new STAFF row with `gym_id` from requesting admin

### Flow
1. Admin fills staff form on dashboard (web or mobile).
2. Client calls `POST /api/auth/staff` with JWT.
3. `StaffCreationService` verifies admin role + gym link, hashes password, saves staff.
4. Success message shown; staff list refreshes.

### Removed from horizontal packages
- Deleted `AuthController` / `AuthService` (auth feature fully sliced)
- Deleted `web/src/api/auth.js`
- `createStaff()` removed from mobile `AuthRepository`
- Deleted `web/src/components/dashboard/CreateStaffForm.jsx`
- `AuthRepository` now only handles session + logout

### Testing
- [ ] Admin creates staff on web → success message, staff appears in staff tab
- [ ] Duplicate email → error message
- [ ] Non-admin cannot call endpoint
- [ ] Same flow on Android admin dashboard

### Screenshot
_[Insert screenshot of staff creation success]_

---

## Slice 4 — Subscription Plans (ready to commit)

### Purpose
Allow Gym Owners to create and manage subscription plans; members view active plans for their gym (FR-005).

### Files moved / created

**Backend**
| File | Responsibility |
|------|----------------|
| `feature/plans/PlanController.java` | `GET/POST/PUT /api/plans` |
| `feature/plans/PlanService.java` | Plan CRUD scoped to admin gym |
| `feature/plans/dto/PlanRequest.java` | Create/update payload |
| `feature/plans/dto/PlanResponse.java` | Plan summary |

**Web**
| File | Responsibility |
|------|----------------|
| `features/plans/PlanForm.jsx` | Admin plan create/edit form |
| `features/plans/PlanList.jsx` | Admin plan table |
| `features/plans/PlanPicker.jsx` | Member plan selection cards |
| `features/plans/api.js` | Plan API calls |

**Mobile**
| File | Responsibility |
|------|----------------|
| `feature/plans/PlanRepository.kt` | Plan API calls |

### API
- `GET /api/plans/active` — active plans for member's or staff's gym
- `GET /api/plans` — all plans (admin only)
- `POST /api/plans` — create plan (admin only)
- `PUT /api/plans/{id}` — update plan (admin only)

### Database
- `subscription_plans` — plan rows linked to gym

### Flow
1. Admin creates plan on dashboard → saved to their gym.
2. Member scans gym QR → enrolled → sees active plans in Plans tab.
3. Member selects plan → checkout (payments slice).

### Removed from horizontal packages
- Deleted `controller/PlanController`, `service/PlanService`, `dto/PlanRequest`, `dto/PlanResponse`
- Deleted `web/src/api/plans.js`
- Deleted `web/src/components/admin/PlanForm.jsx`, `PlanList.jsx`, `member/PlanPicker.jsx`
- Plan methods removed from mobile `GymRepository`

### Testing
- [ ] Admin creates plan on web → appears in plan list
- [ ] Admin edits plan → changes saved
- [ ] Member sees active plans after gym enrollment
- [ ] Same flows on Android

### Screenshot
_[Insert screenshot of plan management]_

---

## Slice 5 — Member Management (ready to commit)

### Purpose
Allow Gym Owners to list, search, edit members and assign subscription plans (FR-006).

### Files moved / created

**Backend**
| File | Responsibility |
|------|----------------|
| `feature/members/MemberController.java` | `GET/PUT /api/members`, `POST /api/members/assign-plan` |
| `feature/members/MemberService.java` | Member list/update and plan assignment |
| `feature/members/dto/MemberResponse.java` | Member summary with membership info |
| `feature/members/dto/MemberUpdateRequest.java` | Profile update payload |
| `feature/members/dto/AssignPlanRequest.java` | Plan assignment payload |

**Web**
| File | Responsibility |
|------|----------------|
| `features/members/MemberTable.jsx` | Member list, edit modal, assign-plan modal |
| `features/members/api.js` | Member API calls |

**Mobile**
| File | Responsibility |
|------|----------------|
| `feature/members/MemberRepository.kt` | Member API calls |

### API
- `GET /api/members` — list/search/filter members (admin only)
- `PUT /api/members/{id}` — update member profile (admin only)
- `POST /api/members/assign-plan` — assign plan to member (admin only)

### Database
- `users` — member profile updates
- `memberships` — updated via assign-plan (uses MembershipService)

### Flow
1. Admin opens Members tab → members at their gym load.
2. Edit member → PUT updates user row.
3. Assign plan → activates membership for member.

### Removed from horizontal packages
- Deleted `controller/MemberController`, `service/MemberService`
- Deleted `dto/MemberResponse`, `MemberUpdateRequest`, `AssignPlanRequest`
- Deleted `web/src/api/members.js`, `web/src/components/admin/MemberTable.jsx`
- Member methods removed from mobile `GymRepository`

### Testing
- [ ] Admin views member list on web
- [ ] Edit member name/email → saved
- [ ] Assign plan to enrolled member → status updates
- [ ] Same flows on Android admin dashboard

### Screenshot
_[Insert screenshot of member management]_

---

## Slice 6 — Membership Status (ready to commit)

### Purpose
Track member subscription status, onboarding steps, and gym enrollment (FR-007).

### Files moved / created

**Backend**
| File | Responsibility |
|------|----------------|
| `feature/membership/MembershipController.java` | `GET /api/membership/me` |
| `feature/membership/MembershipService.java` | Status computation, activation, attendance eligibility |
| `feature/membership/dto/MembershipResponse.java` | Membership + onboarding payload |

**Web**
| File | Responsibility |
|------|----------------|
| `features/membership/MembershipCard.jsx` | Member status summary |
| `features/membership/MemberOnboardingCard.jsx` | Gym enrollment / next-step guidance |
| `features/membership/api.js` | `fetchMyMembership()` |

**Mobile**
| File | Responsibility |
|------|----------------|
| `feature/membership/MembershipRepository.kt` | Membership API call |

### API
- `GET /api/membership/me` — current member's plan, status, gym, next onboarding step

### Database
- `memberships` — read/update for status and dates
- `users` — gym link and first-check-in flag

### Flow
1. Member opens dashboard → `GET /api/membership/me`.
2. `MembershipService` refreshes status (ACTIVE, EXPIRING_SOON, EXPIRED, NONE).
3. UI shows onboarding card or membership card based on `nextStep`.
4. Activation also called from payments and admin assign-plan (shared service).

### Removed from horizontal packages
- Deleted `controller/MembershipController`, `service/MembershipService`, `dto/MembershipResponse`
- Deleted `web/src/api/membership.js`
- Deleted `web/src/components/member/MembershipCard.jsx`, `MemberOnboardingCard.jsx`
- `myMembership()` removed from mobile `GymRepository`

### Testing
- [ ] New member sees "Scan Gym QR" onboarding step
- [ ] After gym scan → next step updates
- [ ] After plan purchase → membership card shows plan and dates
- [ ] Same flows on Android

### Screenshot
_[Insert screenshot of membership status]_

---

## Slice 7 — Online Payment (ready to commit)

### Purpose
Allow members to pay for subscription plans via PayMongo checkout; admins view payment history (FR-008).

### Files moved / created

**Backend**
| File | Responsibility |
|------|----------------|
| `feature/payments/PaymentController.java` | Checkout, webhook, list endpoints |
| `feature/payments/PaymentService.java` | Payment flow + membership activation |
| `feature/payments/PayMongoService.java` | PayMongo API integration (mock in dev) |
| `feature/payments/dto/CheckoutRequest.java` | Checkout payload |
| `feature/payments/dto/CheckoutResponse.java` | Checkout URL response |
| `feature/payments/dto/PaymentResponse.java` | Payment record summary |

**Web**
| File | Responsibility |
|------|----------------|
| `features/payments/PaymentTable.jsx` | Admin/member payment table |
| `features/payments/PaymentHistory.jsx` | Member payment history wrapper |
| `features/payments/PaymentSuccessPage.jsx` | Post-checkout success + mock confirm |
| `features/payments/PaymentCancelPage.jsx` | Cancelled checkout page |
| `features/payments/api.js` | Payment API calls |

**Mobile**
| File | Responsibility |
|------|----------------|
| `feature/payments/PaymentRepository.kt` | Checkout, history, mock confirm |

### API
- `POST /api/payments/checkout` — create PayMongo session
- `POST /api/payments/webhook` — PayMongo webhook handler
- `POST /api/payments/confirm-mock` — dev mock payment confirm
- `GET /api/payments/me` — member payment history
- `GET /api/payments` — admin gym payment list

### Database
- `payments` — payment records with status and PayMongo reference

### Flow
1. Member selects plan → checkout created → redirect to PayMongo (or mock URL).
2. On success → webhook or mock confirm marks PAID → membership activated.
3. Admin views all gym payments; member views own history.

### Removed from horizontal packages
- Deleted `controller/PaymentController`, `service/PaymentService`, `service/PayMongoService`
- Deleted `dto/CheckoutRequest`, `CheckoutResponse`, `PaymentResponse`
- Deleted `web/src/api/payments.js`, admin `PaymentTable.jsx`, member `PaymentHistory.jsx`
- Deleted `web/src/pages/PaymentSuccess`, `PaymentCancel`
- Payment methods removed from mobile `GymRepository`

### Testing
- [ ] Member subscribes to plan → redirected to checkout
- [ ] Mock payment success → membership activates
- [ ] Payment appears in member history and admin payments tab
- [ ] Same flows on Android

### Screenshot
_[Insert screenshot of payment success]_

---

## Next slice
When ready, say **"continue with attendance slice"** — QR check-in/out will move to `feature/attendance/`.
