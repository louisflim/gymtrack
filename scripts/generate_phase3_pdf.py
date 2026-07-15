"""Generate GymTrack course submission PDF."""
from pathlib import Path

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_JUSTIFY, TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import inch
from reportlab.platypus import (
    HRFlowable,
    ListFlowable,
    ListItem,
    PageBreak,
    Paragraph,
    SimpleDocTemplate,
    Spacer,
    Table,
    TableStyle,
)

OUT = Path(__file__).resolve().parent.parent / "docs" / "Lim_LouisFrancis_Phase3.pdf"
REPO = "https://github.com/louisflim/gymtrack"
COMMIT = f"{REPO}/commit"

COMMITS = [
    ("User Settings: change password & delete account (web + mobile)", "180a35b"),
    ("Admin remove member from gym (soft unenroll, confirm dialog)", "315b9a3"),
    ("Fix staff login after forced first-login password change", "b5d441d"),
    ("Security: API role hardening and production secrets", "2ff61af"),
    ("Staff first-login password change (mustChangePassword)", "bbcb15c"),
    ("User-friendly error messages (web, mobile, backend)", "1f77a7e"),
    ("Fix mobile register Create Account button enable state", "5fffd37"),
    ("Admin member/staff delete option & PayMongo fixes", "d3f259d"),
    ("QR code bugfix and UI updates", "be3010b"),
    ("Fix Sign In button not enabling on login", "6f58dfb"),
    ("Gym entity bug fixes", "44e7acb"),
    ("Phase 2 documentation PDF", "4088f3b"),
    ("Vertical slice: staff accounts", "65f2c36"),
    ("Vertical slice: admin dashboard KPIs", "61cd3ea"),
    ("Vertical slice: QR attendance check-in/out", "7740660"),
    ("Vertical slice: PayMongo checkout payments", "f7f26f7"),
    ("Vertical slice: membership status", "8ac7024"),
    ("Vertical slice: member management", "887c770"),
    ("Vertical slice: plan management", "672bfbe"),
    ("Vertical slice: admin staff creation", "3407f40"),
    ("Vertical slice: user login", "544bab8"),
    ("Vertical slice: user registration", "e87ed8d"),
    ("Layout and UI updates", "0e3151b"),
    ("Mobile login and register", "d47a6ce"),
    ("Initialize Android mobile app", "38ec787"),
    ("Admin/member dashboard", "6b9b568"),
    ("Project README", "2b11064"),
    ("Initial Commit", "b9449f6"),
]


def styles():
    base = getSampleStyleSheet()
    return {
        "title": ParagraphStyle(
            "T",
            parent=base["Title"],
            fontSize=20,
            spaceAfter=6,
            textColor=colors.HexColor("#0f0f12"),
            alignment=TA_CENTER,
        ),
        "subtitle": ParagraphStyle(
            "ST",
            parent=base["Normal"],
            fontSize=11,
            alignment=TA_CENTER,
            textColor=colors.HexColor("#444"),
            spaceAfter=4,
        ),
        "h1": ParagraphStyle(
            "H1",
            parent=base["Heading1"],
            fontSize=14,
            spaceBefore=14,
            spaceAfter=8,
            textColor=colors.HexColor("#0f0f12"),
        ),
        "h2": ParagraphStyle(
            "H2",
            parent=base["Heading2"],
            fontSize=12,
            spaceBefore=10,
            spaceAfter=6,
            textColor=colors.HexColor("#222"),
        ),
        "body": ParagraphStyle(
            "B",
            parent=base["Normal"],
            fontSize=10,
            leading=14,
            alignment=TA_JUSTIFY,
            spaceAfter=6,
        ),
        "left": ParagraphStyle(
            "L",
            parent=base["Normal"],
            fontSize=10,
            leading=14,
            alignment=TA_LEFT,
            spaceAfter=4,
        ),
        "small": ParagraphStyle(
            "S",
            parent=base["Normal"],
            fontSize=8,
            leading=11,
            alignment=TA_LEFT,
        ),
        "note": ParagraphStyle(
            "N",
            parent=base["Normal"],
            fontSize=9,
            leading=12,
            textColor=colors.HexColor("#666"),
            alignment=TA_CENTER,
            spaceBefore=4,
            spaceAfter=8,
        ),
        "cell": ParagraphStyle(
            "C",
            parent=base["Normal"],
            fontSize=8,
            leading=10,
        ),
        "cell_link": ParagraphStyle(
            "CL",
            parent=base["Normal"],
            fontSize=7,
            leading=9,
            textColor=colors.HexColor("#0645AD"),
        ),
    }


def section(story, s, title):
    story.append(Paragraph(title, s["h1"]))
    story.append(HRFlowable(width="100%", thickness=1, color=colors.HexColor("#c8ff3d"), spaceAfter=8))


def bullets(items, style):
    return ListFlowable(
        [ListItem(Paragraph(i, style), leftIndent=8, bulletColor=colors.HexColor("#333")) for i in items],
        bulletType="bullet",
        start="•",
        leftIndent=16,
        spaceAfter=8,
    )


def screenshot_box(story, s, label, description):
    story.append(Paragraph(label, s["h2"]))
    story.append(Paragraph(description, s["left"]))
    data = [[Paragraph(f"<b>[SCREENSHOT]</b><br/>{label}<br/><font size='8'>Capture from running web/mobile and replace this box if needed.</font>", s["note"])]]
    t = Table(data, colWidths=[6.5 * inch])
    t.setStyle(
        TableStyle(
            [
                ("BOX", (0, 0), (-1, -1), 1, colors.HexColor("#999")),
                ("BACKGROUND", (0, 0), (-1, -1), colors.HexColor("#f5f5f5")),
                ("TOPPADDING", (0, 0), (-1, -1), 36),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 36),
                ("ALIGN", (0, 0), (-1, -1), "CENTER"),
                ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
            ]
        )
    )
    story.append(t)
    story.append(Spacer(1, 8))


def build():
    OUT.parent.mkdir(parents=True, exist_ok=True)
    doc = SimpleDocTemplate(
        str(OUT),
        pagesize=A4,
        leftMargin=0.75 * inch,
        rightMargin=0.75 * inch,
        topMargin=0.7 * inch,
        bottomMargin=0.7 * inch,
        title="GymTrack Phase 3 — Development Progress Report",
        author="Louis Francis Lim",
    )
    s = styles()
    story = []

    # Cover
    story.append(Spacer(1, 40))
    story.append(Paragraph("GymTrack", s["title"]))
    story.append(Paragraph("Gym Membership Management System", s["subtitle"]))
    story.append(Spacer(1, 8))
    story.append(Paragraph("Phase 3 — Development Progress & Feature Integration Report", s["subtitle"]))
    story.append(Spacer(1, 20))
    story.append(HRFlowable(width="100%", thickness=2, color=colors.HexColor("#0f0f12"), spaceAfter=16))

    info = [
        ["Project Title", "GymTrack — Gym Membership Management System"],
        ["Student Name", "Louis Francis Lim"],
        ["Course / Section", "IT342-G01 — Systems Integration and Architecture 1"],
        ["Institution", "Cebu Institute of Technology — University (CIT-U)"],
        ["Date", "July 15, 2026"],
        ["GitHub Repository", REPO],
    ]
    info_rows = [[Paragraph(f"<b>{a}</b>", s["cell"]), Paragraph(b, s["cell"])] for a, b in info]
    info_table = Table(info_rows, colWidths=[1.8 * inch, 4.7 * inch])
    info_table.setStyle(
        TableStyle(
            [
                ("GRID", (0, 0), (-1, -1), 0.4, colors.HexColor("#ccc")),
                ("BACKGROUND", (0, 0), (0, -1), colors.HexColor("#f0f0f0")),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("TOPPADDING", (0, 0), (-1, -1), 6),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 6),
                ("LEFTPADDING", (0, 0), (-1, -1), 6),
            ]
        )
    )
    story.append(info_table)
    story.append(Spacer(1, 12))
    story.append(
        Paragraph(
            f'Repository link: <link href="{REPO}">{REPO}</link>',
            s["left"],
        )
    )

    # Progress
    section(story, s, "1. Development Progress Summary")
    story.append(
        Paragraph(
            "GymTrack is a full-stack gym membership system with a Spring Boot backend, React web client, "
            "and Android mobile app. Early work established authentication, dashboards, plans, members, "
            "memberships, PayMongo payments, and QR attendance. Mid-phase work reorganized the codebase into "
            "vertical feature slices (auth, plans, members, membership, payments, attendance, staff, dashboard) "
            "shared across web and mobile. Recent work focused on account security, safer admin member removal, "
            "production-ready API hardening, clearer errors, and a shared Settings experience for all roles.",
            s["body"],
        )
    )
    story.append(Paragraph("Progress milestones:", s["left"]))
    story.append(
        bullets(
            [
                "Backend + web + mobile foundations (auth, gym model, dashboards).",
                "Feature vertical slices for registration, login, staff creation, plans, members, membership, payments, attendance, and admin KPIs.",
                "PayMongo checkout integration (mock/test-friendly for school deployment).",
                "QR enrollment and attendance check-in/check-out for staff and members.",
                "Staff forced password change on first login; reusable change-password API.",
                "API method security and production secret/profile guards.",
                "Admin “remove member from gym” (unenroll) instead of hard-deleting the user account.",
                "Settings menu (web + mobile): change password and self-delete account.",
            ],
            s["left"],
        )
    )

    # New features
    section(story, s, "2. Newly Implemented Features")
    story.append(Paragraph("2.1 Account Settings (Web &amp; Mobile)", s["h2"]))
    story.append(
        Paragraph(
            "A Settings tab is available to Admin, Staff, and Member. Users can change their password and "
            "permanently delete their own account (password confirmation + confirm dialog). Light/dark theme "
            "toggles were intentionally not included. Shared components keep web and mobile behavior aligned "
            "(ChangePasswordForm / SettingsPanel on web; layout_settings_panel + SettingsViewModel on mobile).",
            s["body"],
        )
    )
    story.append(Paragraph("2.2 Staff First-Login Password Reset", s["h2"]))
    story.append(
        Paragraph(
            "When an admin creates a staff account, mustChangePassword is set. Staff must set a new password via "
            "POST /api/auth/change-password before using the dashboard. Web and mobile redirect accordingly.",
            s["body"],
        )
    )
    story.append(Paragraph("2.3 Soft Remove Member from Gym", s["h2"]))
    story.append(
        Paragraph(
            "Gym owners remove members from their gym without deleting the users row. Memberships are expired, "
            "gym_id is cleared, and the login account remains so the person can join again later. Confirm dialogs "
            "on web and mobile clarify this behavior.",
            s["body"],
        )
    )
    story.append(Paragraph("2.4 Security Hardening for Deployment", s["h2"]))
    story.append(
        Paragraph(
            "Added @EnableMethodSecurity, @PreAuthorize on controllers, HTTP role matchers, production profile "
            "validation for secrets, CORS/webhook guards, and safer PayMongo mock ownership checks.",
            s["body"],
        )
    )
    story.append(Paragraph("2.5 UX Reliability Fixes", s["h2"]))
    story.append(
        Paragraph(
            "Fixed login/register buttons that stayed disabled until re-render, staff login after password change, "
            "and friendlier API error messages across clients.",
            s["body"],
        )
    )

    # Integration
    section(story, s, "3. How Features Were Integrated")
    story.append(
        Paragraph(
            "Integration follows a shared API contract: React (axios) and Android (Retrofit) call the same "
            "Spring Boot endpoints under /api/*. Session tokens (JWT) are stored in localStorage (web) and "
            "DataStore (mobile). Feature modules are organized as vertical slices so UI, repositories, and "
            "backend packages stay aligned (e.g., feature/auth, feature/settings, feature/members).",
            s["body"],
        )
    )
    story.append(
        bullets(
            [
                "Settings → POST /api/auth/change-password (existing) and DELETE /api/auth/account (new AccountService).",
                "Admin member remove → DELETE /api/members/{id} now unenrolls (MemberService) instead of hard delete.",
                "Staff password gate → AuthResponse.mustChangePassword drives /change-password and ChangePasswordFragment.",
                "Nav config synced: web dashboardUi.js ↔ mobile DashboardUiCopy.kt (including Settings tab).",
                "Reusable UI: ConfirmDialog (web), MaterialAlertDialogBuilder (mobile), shared password form on web.",
            ],
            s["left"],
        )
    )

    story.append(PageBreak())

    # Screenshots
    section(story, s, "4. Screenshots of Working Features")
    story.append(
        Paragraph(
            "Capture these views while the backend, web, and/or Android app are running. Placeholder frames "
            "below mark the required screens for the submission package.",
            s["body"],
        )
    )
    screenshot_box(
        story,
        s,
        "Screenshot A — Web Settings tab",
        "Dashboard → Settings: Change Password form and Delete Account section (all roles).",
    )
    screenshot_box(
        story,
        s,
        "Screenshot B — Mobile Settings tab",
        "Android dashboard Settings panel with password fields and Delete Account action.",
    )
    screenshot_box(
        story,
        s,
        "Screenshot C — Staff first-login password reset",
        "Forced reset password screen after staff signs in with a temporary password.",
    )
    screenshot_box(
        story,
        s,
        "Screenshot D — Admin remove member confirmation",
        "Confirm dialog stating the member is removed from the gym; account is not deleted from the database.",
    )
    screenshot_box(
        story,
        s,
        "Screenshot E — Member QR / attendance (integrated core flow)",
        "Member QR code or staff scan flow confirming attendance integration still works.",
    )

    # E2E tests
    section(story, s, "5. End-to-End Workflow Test Results")
    tests = [
        [
            "Test #",
            "Workflow",
            "Steps",
            "Expected",
            "Result",
        ],
        [
            "E2E-1",
            "Staff first login → change password → dashboard",
            "Admin creates staff → staff logs in → forced password page → sets new password → dashboard loads → can log in again with new password",
            "mustChangePassword cleared; JWT session continues; no login loop",
            "PASSED",
        ],
        [
            "E2E-2",
            "Admin removes member from gym (soft unenroll)",
            "Admin opens Members → Remove → confirm → list refreshes → check users table for same email",
            "Member disappears from gym list; users row remains with gym_id NULL; memberships expired",
            "PASSED",
        ],
        [
            "E2E-3",
            "Settings: change password then delete account",
            "Any role → Settings → update password → sign in again → Settings → enter password → Delete Account → confirm",
            "Password updated successfully; after delete, login fails and related personal records are removed",
            "PASSED",
        ],
        [
            "E2E-4 (bonus)",
            "Member PayMongo / mock pay → membership active → QR check-in",
            "Member enrolls via gym QR → chooses plan → completes checkout (mock/test) → shows member QR → staff scans",
            "Membership ACTIVE; attendance log created",
            "PASSED (mock/test mode)",
        ],
    ]
    test_data = []
    for row in tests:
        test_data.append([Paragraph(str(c), s["cell"]) for c in row])
    test_table = Table(test_data, colWidths=[0.55 * inch, 1.25 * inch, 2.0 * inch, 1.55 * inch, 0.85 * inch])
    test_table.setStyle(
        TableStyle(
            [
                ("GRID", (0, 0), (-1, -1), 0.4, colors.HexColor("#bbb")),
                ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#0f0f12")),
                ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("TOPPADDING", (0, 0), (-1, -1), 4),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 4),
                ("LEFTPADDING", (0, 0), (-1, -1), 3),
                ("BACKGROUND", (0, 1), (-1, 1), colors.HexColor("#f8fff0")),
                ("BACKGROUND", (0, 2), (-1, 2), colors.HexColor("#f8fff0")),
                ("BACKGROUND", (0, 3), (-1, 3), colors.HexColor("#f8fff0")),
            ]
        )
    )
    story.append(test_table)

    # Issues
    section(story, s, "6. Integration Issues Encountered and Solutions")
    issues = [
        [
            "Issue",
            "Cause",
            "Solution",
        ],
        [
            "Admin “delete member” wiped the user from the database",
            "DELETE /api/members/{id} called a hard-delete helper; also old Spring Boot process kept serving previous bytecode",
            "Changed MemberService to unenroll (clear gym, expire memberships). Restart backend after code changes. Soft-remove confirmed in users table.",
        ],
        [
            "Login / Create Account buttons stayed disabled while typing",
            "Enabled state only updated when ViewModel uiState emitted, not on text change",
            "Added local updateButtonState() on doAfterTextChanged for login and register (mobile).",
        ],
        [
            "Staff could not log in smoothly after first password change",
            "mustChangePassword / session routing edge cases after reset",
            "Standardized AuthResponse flag handling and post-change navigation to dashboard; regression fix commit b5d441d.",
        ],
        [
            "Deploy security risk (open routes / weak secrets)",
            "Role checks incomplete; prod secrets not validated",
            "Added method security, HTTP matchers, application-prod profile validator, webhook signature requirement.",
        ],
        [
            "Web & mobile Settings needed identical behavior without theme complexity",
            "Risk of duplicating password/delete logic per platform",
            "Reusable ChangePasswordForm + SettingsPanel (web); SettingsRepository reuses ChangePasswordRepository (mobile); shared /api/auth endpoints.",
        ],
    ]
    issue_data = [[Paragraph(str(c), s["cell"]) for c in row] for row in issues]
    issue_table = Table(issue_data, colWidths=[1.9 * inch, 2.2 * inch, 2.4 * inch])
    issue_table.setStyle(
        TableStyle(
            [
                ("GRID", (0, 0), (-1, -1), 0.4, colors.HexColor("#bbb")),
                ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#0f0f12")),
                ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("TOPPADDING", (0, 0), (-1, -1), 4),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 4),
            ]
        )
    )
    story.append(issue_table)

    story.append(PageBreak())

    # Commit history
    section(story, s, "7. Commit History Table")
    story.append(
        Paragraph(
            "Selected commits mapped to integration work. Full history is on GitHub main branch.",
            s["body"],
        )
    )
    commit_header = [
        Paragraph("<b>Feature / Change</b>", s["cell"]),
        Paragraph("<b>GitHub Commit Link</b>", s["cell"]),
    ]
    commit_rows = [commit_header]
    for label, sha in COMMITS:
        link = f"{COMMIT}/{sha}"
        commit_rows.append(
            [
                Paragraph(label, s["cell"]),
                Paragraph(f'<link href="{link}">{link}</link>', s["cell_link"]),
            ]
        )
    commit_table = Table(commit_rows, colWidths=[3.2 * inch, 3.3 * inch])
    commit_table.setStyle(
        TableStyle(
            [
                ("GRID", (0, 0), (-1, -1), 0.35, colors.HexColor("#ccc")),
                ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#0f0f12")),
                ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("TOPPADDING", (0, 0), (-1, -1), 3),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 3),
                ("LEFTPADDING", (0, 0), (-1, -1), 4),
                ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white, colors.HexColor("#f7f7f7")]),
            ]
        )
    )
    story.append(commit_table)

    # Contribution
    section(story, s, "8. Individual Contribution Statement")
    story.append(
        Paragraph(
            "I, <b>Louis Francis Lim</b>, am the sole developer of GymTrack for IT342-G01. I designed and "
            "implemented the Spring Boot backend, React web application, and Android mobile client; integrated "
            "PayMongo payments and QR attendance; refactored features into vertical slices; added staff "
            "first-login password change, API security hardening, soft member removal from gyms, and the "
            "Settings menus for password change and account deletion on both platforms. All commits in the "
            f'<link href="{REPO}">louisflim/gymtrack</link> repository reflect my individual work unless otherwise noted.',
            s["body"],
        )
    )
    story.append(Spacer(1, 16))
    story.append(Paragraph("_______________________________", s["left"]))
    story.append(Paragraph("Signature over Printed Name", s["small"]))
    story.append(Spacer(1, 8))
    story.append(Paragraph("Date: July 15, 2026", s["left"]))

    story.append(Spacer(1, 24))
    story.append(HRFlowable(width="100%", thickness=1, color=colors.HexColor("#ccc"), spaceAfter=8))
    story.append(
        Paragraph(
            "GymTrack · IT342 Systems Integration and Architecture 1 · Louis Francis Lim · 2026",
            s["note"],
        )
    )

    doc.build(story)
    print(f"Wrote {OUT}")


if __name__ == "__main__":
    build()
