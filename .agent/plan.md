# Project Plan

School Management System app named "Madrasaa Ifaa Islama".
Actors: Admin, Teacher, Student, Parent (4 distinct roles with custom portals/dashboards).

Key Features & Requirements:
1. Student Portal:
   - Course/semester enrollment and registration.
   - Result card showing pass/fail per subject, overall score, GPA/average.
   - Download / print result cards.
2. Staff & Teacher Management:
   - Staff profiles, roles, and attendance tracking.
   - Teacher score entry for subjects.
3. Centralized Score & Grade Calculation:
   - Collect scores from different teachers/subjects and aggregate into central grading, statistics, and report cards.
4. Financial Management (Payment & Salary):
   - Student fee payments tracking.
   - Staff salary calculation and payout records.
   - Graphical statistics for payments received vs. salaries paid out.
5. Attendance & Schedule Management:
   - Student & Staff daily attendance.
   - Class timetable & daily schedules.
6. Parent Portal:
   - Parents view their children's attendance, grades, payment status, and schedules.
7. Chat & Communication System:
   - Integrated messaging system for communication between Admin, Teacher, Parent, and Student.
8. Reports & Document Export:
   - PDF & Excel (CSV) report generation and print capability for result cards, financial reports, attendance, and student lists.
9. UI/UX:
   - Modern, clean, professional Material 3 Jetpack Compose UI with dark/light mode support, dashboard navigation, responsive layouts, special branded Islamic school aesthetic ("Madrasaa Ifaa Islama").

## Project Brief

# Project Brief: Madrasaa Ifaa Islama

## Project Overview
"Madrasaa Ifaa Islama" is a modern School Management System Android application designed to unify administrative, academic, financial, and communication workflows across four key user roles: Admin, Teacher, Student, and Parent. The initial Minimum Viable Product (MVP) focuses on delivering essential management tools through a clean, responsive Jetpack Compose interface styled with a custom Islamic school aesthetic.

## Features
1. **Multi-Role Portals & Dashboards**: Dedicated portals tailored for Admins, Teachers, Students, and Parents with role-specific views and quick access to essential actions.
2. **Academic & Result Management**: Teacher score input per subject, centralized automated grade/GPA calculation, and digital result card viewing and export.
3. **Attendance & Timetable Tracking**: Daily attendance logging for students and staff, paired with interactive class timetables.
4. **Financial Management & Overview**: Student fee payment logging, staff salary records, and graphical dashboard summaries comparing revenue vs. expenses.
5. **Integrated Communication Hub**: Direct in-app messaging and announcements facilitating clear communication across all 4 actor roles.

## High-Level Tech Stack
- **Programming Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3 (custom "Madrasaa Ifaa Islama" theme supporting light & dark modes)
- **Navigation**: Jetpack Navigation 3 (state-driven navigation)
- **Adaptive Strategy**: Compose Material Adaptive library (responsive, multi-pane layouts for phones, foldables, and tablets)
- **Asynchronous Execution**: Kotlin Coroutines & StateFlow
- **Architecture**: MVVM (Model-View-ViewModel) with Unidirectional Data Flow (UDF) using AndroidX ViewModel

## Implementation Steps

### Task_1_CoreArchitectureAndDataRepository: Set up core architecture, data models for 4 user roles (Admin, Teacher, Student, Parent), Room DB or Repository with initial mock data, and Jetpack Navigation scaffold with role-switching capability.
- **Status:** COMPLETED
- **Updates:** Created domain & data models (User, StudentProfile, TeacherProfile, StaffProfile, Course, Semester, Score, SubjectResult, ReportCard, AttendanceRecord, TimetableItem, PaymentRecord, SalaryRecord, ChatMessage). Implemented SchoolRepository pre-populated with rich sample data for Madrasaa Ifaa Islama. Configured Islamic emerald/gold Material 3 theme, role switcher top bar, and role-based navigation scaffold. Debug build succeeded without errors.
- **Acceptance Criteria:**
  - Data models and Repository created for users, scores, attendance, financial records, and messages
  - Jetpack Navigation scaffold and role switcher implemented
  - project builds successfully

### Task_2_MultiRoleDashboardsAndAcademicFeatures: Implement UI screens for Admin, Teacher, Student, and Parent dashboards, score collection, automatic GPA and grade calculations, and digital result card viewing with PDF export and printing.
- **Status:** COMPLETED
- **Updates:** Implemented enhanced multi-role academic features: Student Portal with semester course registration and result card; Teacher Portal with score collection and automatic central grade/GPA calculation; Admin academic overview with course & semester management; Parent child progress & result card. Created native PdfReportGenerator with PrintManager integration and CsvExportHelper with FileProvider setup for CSV export. Unit tests and debug build passed cleanly.
- **Acceptance Criteria:**
  - Role-based dashboards created for Admin, Teacher, Student, and Parent
  - Score input and automatic GPA calculation working
  - Result card screen created with working PDF export and print capability
  - project builds successfully

### Task_3_FinancialAttendanceAndCommunication: Implement attendance logging, timetable/schedule tracking, financial overview (fee collection, salary logging, revenue vs expense stats), and communication chat hub.
- **Status:** COMPLETED
- **Updates:** Implemented financial management & statistics dashboard with revenue vs. expense visual bar chart, staff salary calculator (Base + Bonus - Deductions) with payout records, student fee payment logger, student and staff attendance tracking with percentage calculations, interactive timetable with multi-filter and day presets, cross-role messaging chat hub with recipient selection and broadcast announcements, and PDF/CSV export helper methods. All 12 unit tests passed and assembleDebug succeeded.
- **Acceptance Criteria:**
  - Attendance logging and class timetable views functional
  - Financial logging and summary statistics UI working
  - Communication chat hub active for cross-role messaging
  - build pass
  - make sure all existing tests pass
  - app does not crash

### Task_4_RunAndVerify: Perform final build and verification, polish UI theme with Madrasaa Islamic aesthetic, and instruct critic_agent to verify application stability (no crashes), confirm alignment with user requirements, and report critical UI issues.
- **Status:** COMPLETED
- **Updates:** Verified app stability, build status, 100% unit test pass rate, Islamic aesthetic design, and full feature compliance across all 4 actor roles (Admin, Teacher, Student, Parent), PDF generation & print capabilities, score collection, financial bar chart statistics, staff salary calculator, attendance logging, and cross-role chat hub. Quality gate passed with zero crashes or issues reported.
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - make sure all existing tests pass
  - Application verified for stability, role features, and UI alignment without crashes
- **Duration:** N/A

