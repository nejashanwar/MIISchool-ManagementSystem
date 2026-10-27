package com.example.marasaaapp.repository

import android.content.Context
import com.example.marasaaapp.data.local.MadrasaaDatabase
import com.example.marasaaapp.data.local.dao.SchoolDao
import com.example.marasaaapp.data.local.entity.*
import com.example.marasaaapp.model.*
import com.example.marasaaapp.ui.theme.ThemeAccent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.UUID

object SchoolRepository {

    private var schoolDao: SchoolDao? = null
    private val repoScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun initialize(context: Context) {
        if (schoolDao == null) {
            val db = MadrasaaDatabase.getDatabase(context, repoScope)
            schoolDao = db.schoolDao()
        }
    }

    // --- Theme Customizer Accent ---
    private val _themeAccent = MutableStateFlow(ThemeAccent.EMERALD)
    val themeAccent: StateFlow<ThemeAccent> = _themeAccent.asStateFlow()

    fun setThemeAccent(accent: ThemeAccent) {
        _themeAccent.value = accent
    }

    // --- Push Notifications Simulation ---
    data class AppNotification(
        val id: String,
        val title: String,
        val message: String,
        val timestamp: String,
        val isRead: Boolean = false
    )

    private val _notifications = MutableStateFlow(
        listOf(
            AppNotification("notif_1", "Welcome to Madrasaa Ifaa Islama", "Academy management system initialized successfully with Room DB.", "Oct 25, 08:00 AM", true),
            AppNotification("notif_2", "Midterm Assembly", "Midterm Academic Assembly scheduled for next Monday at 9:00 AM.", "Oct 25, 08:30 AM", false)
        )
    )
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    fun addNotification(title: String, message: String) {
        val notif = AppNotification(
            id = UUID.randomUUID().toString(),
            title = title,
            message = message,
            timestamp = "Just Now",
            isRead = false
        )
        _notifications.update { listOf(notif) + it }
    }

    fun markNotificationAsRead(id: String) {
        _notifications.update { list ->
            list.map { if (it.id == id) it.copy(isRead = true) else it }
        }
    }

    // --- Active Users by Role ---
    val adminUser = User(
        id = "usr_admin_1",
        name = "Sheikh Ibrahim Al-Hassan",
        email = "ibrahim.principal@madrasaa-ifaa.edu",
        role = UserRole.ADMIN,
        phone = "+251 91 123 4567"
    )

    val teacherUser = User(
        id = "usr_teacher_1",
        name = "Ustadh Abdurrahman Mahmoud",
        email = "abdurrahman@madrasaa-ifaa.edu",
        role = UserRole.TEACHER,
        phone = "+251 91 234 5678"
    )

    val studentUser = User(
        id = "usr_student_1",
        name = "Ahmed Ali",
        email = "ahmed.ali@student.madrasaa-ifaa.edu",
        role = UserRole.STUDENT,
        phone = "+251 91 345 6789"
    )

    val parentUser = User(
        id = "usr_parent_1",
        name = "Ali Ahmed (Parent)",
        email = "ali.ahmed@parent.com",
        role = UserRole.PARENT,
        phone = "+251 91 456 7890"
    )

    val usersList = listOf(
        adminUser,
        teacherUser,
        User("usr_teacher_2", "Ustadha Mariam Hassan", "mariam@madrasaa-ifaa.edu", UserRole.TEACHER, phone = "+251 91 234 5679"),
        User("usr_teacher_3", "Ustadh Bilal Omar", "bilal@madrasaa-ifaa.edu", UserRole.TEACHER, phone = "+251 91 234 5680"),
        studentUser,
        User("usr_student_2", "Fatima Hassan", "fatima.h@student.madrasaa-ifaa.edu", UserRole.STUDENT, phone = "+251 91 345 6790"),
        User("usr_student_3", "Mohamed Omar", "mohamed.o@student.madrasaa-ifaa.edu", UserRole.STUDENT, phone = "+251 91 345 6791"),
        parentUser,
        User("usr_parent_2", "Hassan Mahmoud (Parent)", "hassan.m@parent.com", UserRole.PARENT, phone = "+251 91 456 7891")
    )

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    val currentUser: User?
        get() = if (_isLoggedIn.value) _activeUser.value else null

    private val _activeUser = MutableStateFlow(adminUser)
    val activeUser: StateFlow<User> = _activeUser.asStateFlow()

    private val _activeRole = MutableStateFlow(UserRole.ADMIN)
    val activeRole: StateFlow<UserRole> = _activeRole.asStateFlow()

    fun login(email: String, password: String, role: UserRole): Boolean {
        val cleanEmail = email.trim().lowercase()
        val cleanPass = password.trim()

        val isValid = when (role) {
            UserRole.ADMIN -> {
                (cleanEmail == "admin@ifaa.edu" || cleanEmail == adminUser.email.lowercase() || cleanEmail == "admin") && cleanPass == "admin123"
            }
            UserRole.TEACHER -> {
                (cleanEmail == "teacher@ifaa.edu" || cleanEmail == teacherUser.email.lowercase() || cleanEmail == "teacher") && cleanPass == "teacher123"
            }
            UserRole.STUDENT -> {
                (cleanEmail == "student@ifaa.edu" || cleanEmail == studentUser.email.lowercase() || cleanEmail == "student") && cleanPass == "student123"
            }
            UserRole.PARENT -> {
                (cleanEmail == "parent@ifaa.edu" || cleanEmail == parentUser.email.lowercase() || cleanEmail == "parent") && cleanPass == "parent123"
            }
        }

        if (isValid) {
            _activeRole.value = role
            _activeUser.value = when (role) {
                UserRole.ADMIN -> adminUser
                UserRole.TEACHER -> teacherUser
                UserRole.STUDENT -> studentUser
                UserRole.PARENT -> parentUser
            }
            _isLoggedIn.value = true
            addNotification("Signed In", "Welcome back, ${_activeUser.value.name} (${role.label})")
            return true
        }
        return false
    }

    fun quickLogin(role: UserRole) {
        _activeRole.value = role
        _activeUser.value = when (role) {
            UserRole.ADMIN -> adminUser
            UserRole.TEACHER -> teacherUser
            UserRole.STUDENT -> studentUser
            UserRole.PARENT -> parentUser
        }
        _isLoggedIn.value = true
        addNotification("Signed In", "Quick login as ${role.label}")
    }

    fun logout() {
        _isLoggedIn.value = false
        addNotification("Signed Out", "Session ended securely.")
    }

    // --- Profiles ---
    val studentProfilesList = listOf(
        StudentProfile(
            id = "std_prof_1",
            userId = "usr_student_1",
            studentIdNumber = "MII-2024-001",
            name = "Ahmed Ali",
            gradeLevel = "Grade 8 - Tahfeedh",
            parentId = "usr_parent_1",
            parentName = "Ali Ahmed",
            dateOfBirth = "2010-05-14",
            enrollmentDate = "2021-09-01",
            gender = "Male"
        ),
        StudentProfile(
            id = "std_prof_2",
            userId = "usr_student_2",
            studentIdNumber = "MII-2024-002",
            name = "Fatima Hassan",
            gradeLevel = "Grade 8 - Tahfeedh",
            parentId = "usr_parent_2",
            parentName = "Hassan Mahmoud",
            dateOfBirth = "2010-08-22",
            enrollmentDate = "2021-09-01",
            gender = "Female"
        ),
        StudentProfile(
            id = "std_prof_3",
            userId = "usr_student_3",
            studentIdNumber = "MII-2024-003",
            name = "Mohamed Omar",
            gradeLevel = "Grade 10 - Shariah",
            parentId = "usr_parent_1",
            parentName = "Ali Ahmed",
            dateOfBirth = "2008-11-03",
            enrollmentDate = "2019-09-01",
            gender = "Male"
        )
    )

    val teacherProfilesList = listOf(
        TeacherProfile(
            id = "tch_prof_1",
            userId = "usr_teacher_1",
            employeeId = "STAFF-101",
            name = "Ustadh Abdurrahman Mahmoud",
            qualification = "B.A. Islamic Studies & Ijazah in Ten Qira'at",
            specialization = "Quran Recitation & Tajweed",
            assignedClasses = listOf("Grade 8 - Tahfeedh", "Grade 10 - Shariah"),
            joinDate = "2018-08-15"
        ),
        TeacherProfile(
            id = "tch_prof_2",
            userId = "usr_teacher_2",
            employeeId = "STAFF-102",
            name = "Ustadha Mariam Hassan",
            qualification = "M.A. Arabic Literature",
            specialization = "Classical Arabic & Seerah",
            assignedClasses = listOf("Grade 8 - Tahfeedh", "Grade 9"),
            joinDate = "2019-01-10"
        ),
        TeacherProfile(
            id = "tch_prof_3",
            userId = "usr_teacher_3",
            employeeId = "STAFF-103",
            name = "Ustadh Bilal Omar",
            qualification = "B.S. Islamic Jurisprudence (Fiqh)",
            specialization = "Fiqh & Aqeedah",
            assignedClasses = listOf("Grade 10 - Shariah"),
            joinDate = "2020-09-01"
        )
    )

    val staffProfilesList = listOf(
        StaffProfile(
            id = "stf_prof_1",
            userId = "usr_admin_1",
            employeeId = "STAFF-001",
            name = "Sheikh Ibrahim Al-Hassan",
            roleTitle = "Academy Principal & General Director",
            department = "Administration",
            joinDate = "2015-06-01"
        ),
        StaffProfile(
            id = "stf_prof_2",
            userId = "usr_teacher_1",
            employeeId = "STAFF-101",
            name = "Ustadh Abdurrahman Mahmoud",
            roleTitle = "Head of Quranic Studies",
            department = "Academics",
            joinDate = "2018-08-15"
        )
    )

    // --- Courses & Semesters ---
    val currentSemester = Semester(
        id = "sem_1",
        name = "Semester 1, 2024/2025",
        startDate = "2024-09-01",
        endDate = "2025-01-31",
        isCurrent = true
    )

    val upcomingSemester = Semester(
        id = "sem_2",
        name = "Semester 2, 2024/2025",
        startDate = "2025-02-15",
        endDate = "2025-06-30",
        isCurrent = false
    )

    val coursesList = listOf(
        Course("crs_101", "QRN-101", "Quran Hifz (Memorization)", "Comprehensive Quranic memorization.", "usr_teacher_1", "Ustadh Abdurrahman Mahmoud", "Grade 8 - Tahfeedh", 4),
        Course("crs_102", "TJW-102", "Tajweed (Recitation Rules)", "Detailed study of Makharij, Sifaat, and recitation rules.", "usr_teacher_1", "Ustadh Abdurrahman Mahmoud", "Grade 8 - Tahfeedh", 3),
        Course("crs_201", "ARB-201", "Arabic Grammar (Nahw/Sarf)", "Grammar, Morphology, and vocabulary building.", "usr_teacher_2", "Ustadha Mariam Hassan", "Grade 8 - Tahfeedh", 3),
        Course("crs_202", "FQH-202", "Islamic Jurisprudence (Fiqh)", "Study of Taharah, Salah, Zakah, and Sawm according to Shariah.", "usr_teacher_3", "Ustadh Bilal Omar", "Grade 10 - Shariah", 3),
        Course("crs_105", "AQD-105", "Aqeedah (Islamic Creed)", "Fundamentals of Islamic faith, Tawheed, and creed.", "usr_teacher_3", "Ustadh Bilal Omar", "Grade 8 - Tahfeedh", 3),
        Course("crs_103", "HIS-103", "Seerah & Islamic History", "Life of Prophet Muhammad (PBUH) and Early Caliphates.", "usr_teacher_2", "Ustadha Mariam Hassan", "Grade 8 - Tahfeedh", 2),
        Course("crs_104", "MTH-104", "Mathematics & Arithmetic", "Algebra, Geometry, and financial calculations.", "usr_teacher_2", "Ustadha Mariam Hassan", "Grade 8 - Tahfeedh", 3)
    )

    private val initialEnrollments = listOf(
        Enrollment("enr_1", "usr_student_1", "crs_101", "sem_1", "2024-09-01"),
        Enrollment("enr_2", "usr_student_1", "crs_102", "sem_1", "2024-09-01"),
        Enrollment("enr_3", "usr_student_1", "crs_201", "sem_1", "2024-09-01"),
        Enrollment("enr_4", "usr_student_1", "crs_105", "sem_1", "2024-09-01"),
        Enrollment("enr_5", "usr_student_1", "crs_103", "sem_1", "2024-09-01"),
        Enrollment("enr_6", "usr_student_2", "crs_101", "sem_1", "2024-09-01"),
        Enrollment("enr_7", "usr_student_2", "crs_102", "sem_1", "2024-09-01"),
        Enrollment("enr_8", "usr_student_3", "crs_202", "sem_1", "2024-09-01")
    )

    private val _enrollments = MutableStateFlow(initialEnrollments)
    val enrollments: StateFlow<List<Enrollment>> = _enrollments.asStateFlow()

    private val _students = MutableStateFlow(studentProfilesList)
    val students: StateFlow<List<StudentProfile>> = _students.asStateFlow()

    private val _teachers = MutableStateFlow(teacherProfilesList)
    val teachers: StateFlow<List<TeacherProfile>> = _teachers.asStateFlow()

    private val _staff = MutableStateFlow(staffProfilesList)
    val staff: StateFlow<List<StaffProfile>> = _staff.asStateFlow()

    private val _courses = MutableStateFlow(coursesList)
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    private val _semesters = MutableStateFlow(listOf(currentSemester, upcomingSemester))
    val semesters: StateFlow<List<Semester>> = _semesters.asStateFlow()

    // --- Report Cards & Scores ---
    private val initialReportCards = listOf(
        ReportCard(
            id = "rc_1",
            studentId = "usr_student_1",
            studentName = "Ahmed Ali",
            gradeLevel = "Grade 8 - Tahfeedh",
            semesterName = "Semester 1, 2024/2025",
            results = listOf(
                SubjectResult("QRN-101", "Quran Hifz (Memorization)", Score("crs_101", "Quran Hifz", "Ustadh Abdurrahman", assignmentsMarks = 15.0, midtermMarks = 23.0, finalExamMarks = 57.0)),
                SubjectResult("TJW-102", "Tajweed (Recitation Rules)", Score("crs_102", "Tajweed", "Ustadh Abdurrahman", assignmentsMarks = 14.0, midtermMarks = 21.0, finalExamMarks = 53.0)),
                SubjectResult("ARB-201", "Arabic Grammar (Nahw/Sarf)", Score("crs_201", "Arabic Grammar", "Ustadha Mariam", assignmentsMarks = 14.0, midtermMarks = 20.0, finalExamMarks = 50.0)),
                SubjectResult("AQD-105", "Aqeedah (Islamic Creed)", Score("crs_105", "Aqeedah", "Ustadh Bilal Omar", assignmentsMarks = 15.0, midtermMarks = 22.0, finalExamMarks = 55.0)),
                SubjectResult("HIS-103", "Seerah & Islamic History", Score("crs_103", "Seerah", "Ustadha Mariam", assignmentsMarks = 15.0, midtermMarks = 21.0, finalExamMarks = 54.0)),
                SubjectResult("MTH-104", "Mathematics & Arithmetic", Score("crs_104", "Mathematics", "Ustadha Mariam", assignmentsMarks = 14.0, midtermMarks = 21.0, finalExamMarks = 51.0))
            ),
            teacherComments = "MashaAllah! Ahmed exhibits exemplary discipline in Hifz, Tajweed, and Islamic sciences.",
            issueDate = "2024-10-15"
        ),
        ReportCard(
            id = "rc_2",
            studentId = "usr_student_2",
            studentName = "Fatima Hassan",
            gradeLevel = "Grade 8 - Tahfeedh",
            semesterName = "Semester 1, 2024/2025",
            results = listOf(
                SubjectResult("QRN-101", "Quran Hifz (Memorization)", Score("crs_101", "Quran Hifz", "Ustadh Abdurrahman", assignmentsMarks = 15.0, midtermMarks = 24.0, finalExamMarks = 59.0)),
                SubjectResult("TJW-102", "Tajweed (Recitation Rules)", Score("crs_102", "Tajweed", "Ustadh Abdurrahman", assignmentsMarks = 15.0, midtermMarks = 23.0, finalExamMarks = 58.0)),
                SubjectResult("ARB-201", "Arabic Grammar (Nahw/Sarf)", Score("crs_201", "Arabic Grammar", "Ustadha Mariam", assignmentsMarks = 15.0, midtermMarks = 22.0, finalExamMarks = 55.0)),
                SubjectResult("AQD-105", "Aqeedah (Islamic Creed)", Score("crs_105", "Aqeedah", "Ustadh Bilal Omar", assignmentsMarks = 15.0, midtermMarks = 24.0, finalExamMarks = 58.0)),
                SubjectResult("HIS-103", "Seerah & Islamic History", Score("crs_103", "Seerah", "Ustadha Mariam", 15.0, 23.0, 57.0)),
                SubjectResult("MTH-104", "Mathematics & Arithmetic", Score("crs_104", "Mathematics", "Ustadha Mariam", 14.0, 22.0, 55.0))
            ),
            teacherComments = "Outstanding student with top marks in all Hifz, Tajweed, Fiqh, and academic subjects.",
            issueDate = "2024-10-15"
        ),
        ReportCard(
            id = "rc_3",
            studentId = "usr_student_3",
            studentName = "Mohamed Omar",
            gradeLevel = "Grade 10 - Shariah",
            semesterName = "Semester 1, 2024/2025",
            results = listOf(
                SubjectResult("FQH-202", "Islamic Jurisprudence (Fiqh)", Score("crs_202", "Fiqh", "Ustadh Bilal Omar", 13.0, 20.0, 52.0)),
                SubjectResult("QRN-101", "Quran Hifz (Memorization)", Score("crs_101", "Quran Hifz", "Ustadh Abdurrahman", 13.0, 19.0, 50.0)),
                SubjectResult("ARB-201", "Arabic Grammar (Nahw/Sarf)", Score("crs_201", "Arabic Grammar", "Ustadha Mariam", 12.0, 18.0, 48.0))
            ),
            teacherComments = "Good progress overall, requires additional focus on Classical Arabic grammar and Fiqh texts.",
            issueDate = "2024-10-15"
        )
    )

    private val _reportCards = MutableStateFlow(initialReportCards)
    val reportCards: StateFlow<List<ReportCard>> = _reportCards.asStateFlow()

    // Derived computation for ReportCard from students, courses, and scores
    fun getReportCardsForStudent(studentId: String): ReportCard? {
        val student = _students.value.find { it.userId == studentId } ?: return null
        val existingCard = _reportCards.value.find { it.studentId == studentId }
        if (existingCard != null) return existingCard

        return ReportCard(
            id = "rc_" + studentId,
            studentId = studentId,
            studentName = student.name,
            gradeLevel = student.gradeLevel,
            semesterName = currentSemester.name,
            results = emptyList(),
            teacherComments = "Enrolled in academy.",
            issueDate = "2024-10-25"
        )
    }

    // --- Attendance Logs ---
    private val initialAttendance = listOf(
        AttendanceRecord("att_1", "usr_student_1", "Ahmed Ali", "crs_101", "Quran Hifz", "2024-10-21", AttendanceStatus.PRESENT, "Punctual"),
        AttendanceRecord("att_2", "usr_student_1", "Ahmed Ali", "crs_102", "Tajweed Rules", "2024-10-22", AttendanceStatus.PRESENT, "Punctual"),
        AttendanceRecord("att_3", "usr_student_1", "Ahmed Ali", "crs_201", "Arabic Grammar", "2024-10-23", AttendanceStatus.LATE, "Arrived 10 mins late"),
        AttendanceRecord("att_4", "usr_student_1", "Ahmed Ali", "crs_105", "Aqeedah Creed", "2024-10-24", AttendanceStatus.PRESENT, "Punctual"),
        AttendanceRecord("att_5", "usr_student_1", "Ahmed Ali", "crs_103", "Seerah History", "2024-10-25", AttendanceStatus.PRESENT, "Active in class"),
        AttendanceRecord("att_6", "usr_student_2", "Fatima Hassan", "crs_101", "Quran Hifz", "2024-10-21", AttendanceStatus.PRESENT, "Active participant"),
        AttendanceRecord("att_7", "usr_student_2", "Fatima Hassan", "crs_102", "Tajweed Rules", "2024-10-22", AttendanceStatus.PRESENT, "Punctual"),
        AttendanceRecord("att_8", "usr_student_2", "Fatima Hassan", "crs_201", "Arabic Grammar", "2024-10-23", AttendanceStatus.PRESENT, "Punctual"),
        AttendanceRecord("att_9", "usr_student_2", "Fatima Hassan", "crs_105", "Aqeedah Creed", "2024-10-24", AttendanceStatus.PRESENT, "Top score in quiz"),
        AttendanceRecord("att_10", "usr_student_3", "Mohamed Omar", "crs_202", "Islamic Fiqh", "2024-10-21", AttendanceStatus.EXCUSED, "Medical notice submitted"),
        AttendanceRecord("att_11", "usr_student_3", "Mohamed Omar", "crs_101", "Quran Hifz", "2024-10-22", AttendanceStatus.PRESENT, "Punctual"),
        AttendanceRecord("att_12", "usr_student_3", "Mohamed Omar", "crs_201", "Arabic Grammar", "2024-10-23", AttendanceStatus.ABSENT, "Unexcused absence")
    )

    private val _attendanceRecords = MutableStateFlow(initialAttendance)
    val attendanceRecords: StateFlow<List<AttendanceRecord>> = _attendanceRecords.asStateFlow()

    private val initialStaffAttendance = listOf(
        StaffAttendanceRecord("satt_1", "usr_teacher_1", "Ustadh Abdurrahman Mahmoud", "2024-10-21", AttendanceStatus.PRESENT, "07:45 AM", "03:30 PM", "Normal shift"),
        StaffAttendanceRecord("satt_2", "usr_teacher_1", "Ustadh Abdurrahman Mahmoud", "2024-10-22", AttendanceStatus.PRESENT, "07:42 AM", "03:30 PM", "Normal shift"),
        StaffAttendanceRecord("satt_3", "usr_teacher_2", "Ustadha Mariam Hassan", "2024-10-21", AttendanceStatus.PRESENT, "07:50 AM", "03:30 PM", "Normal shift"),
        StaffAttendanceRecord("satt_4", "usr_teacher_2", "Ustadha Mariam Hassan", "2024-10-22", AttendanceStatus.PRESENT, "07:48 AM", "03:30 PM", "Normal shift"),
        StaffAttendanceRecord("satt_5", "usr_teacher_3", "Ustadh Bilal Omar", "2024-10-21", AttendanceStatus.PRESENT, "08:00 AM", "03:30 PM", "Normal shift"),
        StaffAttendanceRecord("satt_6", "usr_teacher_3", "Ustadh Bilal Omar", "2024-10-22", AttendanceStatus.LATE, "08:15 AM", "03:30 PM", "Traffic delay"),
        StaffAttendanceRecord("satt_7", "usr_admin_1", "Sheikh Ibrahim Al-Hassan", "2024-10-21", AttendanceStatus.PRESENT, "07:30 AM", "04:30 PM", "Principal duty"),
        StaffAttendanceRecord("satt_8", "usr_admin_1", "Sheikh Ibrahim Al-Hassan", "2024-10-22", AttendanceStatus.PRESENT, "07:30 AM", "04:30 PM", "Principal duty")
    )

    private val _staffAttendanceRecords = MutableStateFlow(initialStaffAttendance)
    val staffAttendanceRecords: StateFlow<List<StaffAttendanceRecord>> = _staffAttendanceRecords.asStateFlow()

    // --- Timetable Items ---
    val timetableList = listOf(
        TimetableItem("tt_1", "crs_101", "Quran Hifz (Memorization)", "QRN-101", "Ustadh Abdurrahman Mahmoud", "Hall 10A", DayOfWeekName.MONDAY, "08:00 AM", "09:30 AM", "Grade 8 - Tahfeedh"),
        TimetableItem("tt_2", "crs_102", "Tajweed (Recitation Rules)", "TJW-102", "Ustadh Abdurrahman Mahmoud", "Hall 10A", DayOfWeekName.MONDAY, "09:45 AM", "11:15 AM", "Grade 8 - Tahfeedh"),
        TimetableItem("tt_3", "crs_201", "Arabic Grammar (Nahw/Sarf)", "ARB-201", "Ustadha Mariam Hassan", "Room 4B", DayOfWeekName.TUESDAY, "08:00 AM", "09:30 AM", "Grade 8 - Tahfeedh"),
        TimetableItem("tt_4", "crs_105", "Aqeedah (Islamic Creed)", "AQD-105", "Ustadh Bilal Omar", "Room 4B", DayOfWeekName.WEDNESDAY, "09:00 AM", "10:30 AM", "Grade 8 - Tahfeedh"),
        TimetableItem("tt_5", "crs_103", "Seerah & Islamic History", "HIS-103", "Ustadha Mariam Hassan", "Room 4B", DayOfWeekName.WEDNESDAY, "10:45 AM", "12:15 PM", "Grade 8 - Tahfeedh"),
        TimetableItem("tt_6", "crs_104", "Mathematics & Arithmetic", "MTH-104", "Ustadha Mariam Hassan", "Room 4B", DayOfWeekName.THURSDAY, "08:00 AM", "09:30 AM", "Grade 8 - Tahfeedh"),
        TimetableItem("tt_7", "crs_202", "Islamic Jurisprudence (Fiqh)", "FQH-202", "Ustadh Bilal Omar", "Lecture Hall 2", DayOfWeekName.MONDAY, "10:00 AM", "11:30 AM", "Grade 10 - Shariah"),
        TimetableItem("tt_8", "crs_101", "Quran Hifz (Memorization)", "QRN-101", "Ustadh Abdurrahman Mahmoud", "Hall 10A", DayOfWeekName.SUNDAY, "08:30 AM", "10:00 AM", "Grade 8 - Tahfeedh")
    )

    private val _timetableItems = MutableStateFlow(timetableList)
    val timetableItems: StateFlow<List<TimetableItem>> = _timetableItems.asStateFlow()

    fun addTimetableItem(
        courseId: String,
        courseTitle: String,
        courseCode: String,
        teacherName: String,
        classroom: String,
        dayOfWeek: DayOfWeekName,
        startTime: String,
        endTime: String,
        targetGrade: String
    ) {
        val newItem = TimetableItem(
            id = "tt_${System.currentTimeMillis()}",
            courseId = courseId.ifBlank { "crs_101" },
            courseTitle = courseTitle,
            courseCode = courseCode,
            teacherName = teacherName,
            classroom = classroom,
            dayOfWeek = dayOfWeek,
            startTime = startTime,
            endTime = endTime,
            targetGrade = targetGrade
        )
        _timetableItems.value = _timetableItems.value + newItem
        addNotification("Timetable Updated", "Added schedule for $courseTitle ($courseCode) on ${dayOfWeek.label}")
    }

    fun updateTimetableItem(
        id: String,
        courseTitle: String,
        courseCode: String,
        teacherName: String,
        classroom: String,
        dayOfWeek: DayOfWeekName,
        startTime: String,
        endTime: String,
        targetGrade: String
    ) {
        _timetableItems.value = _timetableItems.value.map {
            if (it.id == id) {
                it.copy(
                    courseTitle = courseTitle,
                    courseCode = courseCode,
                    teacherName = teacherName,
                    classroom = classroom,
                    dayOfWeek = dayOfWeek,
                    startTime = startTime,
                    endTime = endTime,
                    targetGrade = targetGrade
                )
            } else it
        }
        addNotification("Timetable Updated", "Updated schedule for $courseTitle ($courseCode)")
    }

    fun deleteTimetableItem(id: String) {
        _timetableItems.value = _timetableItems.value.filter { it.id != id }
        addNotification("Timetable Updated", "Deleted schedule item $id")
    }

    // --- Payments & Salaries ---
    private val initialPayments = listOf(
        PaymentRecord("pay_1", "usr_student_1", "Ahmed Ali", FeeType.TUITION, 150.0, "2024-09-05", "2024-09-03", PaymentStatus.PAID, "TXN-98421"),
        PaymentRecord("pay_2", "usr_student_1", "Ahmed Ali", FeeType.BOOKS, 45.0, "2024-09-10", "2024-09-08", PaymentStatus.PAID, "TXN-98422"),
        PaymentRecord("pay_3", "usr_student_2", "Fatima Hassan", FeeType.TUITION, 150.0, "2024-09-05", "2024-09-04", PaymentStatus.PAID, "TXN-98430"),
        PaymentRecord("pay_4", "usr_student_2", "Fatima Hassan", FeeType.TRANSPORT, 30.0, "2024-10-01", null, PaymentStatus.PENDING, null),
        PaymentRecord("pay_5", "usr_student_3", "Mohamed Omar", FeeType.TUITION, 150.0, "2024-09-05", null, PaymentStatus.OVERDUE, null),
        PaymentRecord("pay_6", "usr_student_1", "Ahmed Ali", FeeType.TUITION, 150.0, "2024-10-05", "2024-10-02", PaymentStatus.PAID, "TXN-98450"),
        PaymentRecord("pay_7", "usr_student_2", "Fatima Hassan", FeeType.TUITION, 150.0, "2024-10-05", "2024-10-03", PaymentStatus.PAID, "TXN-98451")
    )

    private val _paymentRecords = MutableStateFlow(initialPayments)
    val paymentRecords: StateFlow<List<PaymentRecord>> = _paymentRecords.asStateFlow()

    private val initialSalaries = listOf(
        SalaryRecord("sal_1", "usr_teacher_1", "Ustadh Abdurrahman Mahmoud", "Head of Quranic Studies", "September 2024", 1200.0, 150.0, 0.0, paymentDate = "2024-09-25", status = PaymentStatus.PAID),
        SalaryRecord("sal_2", "usr_teacher_2", "Ustadha Mariam Hassan", "Arabic & Seerah Educator", "September 2024", 1200.0, 100.0, 0.0, paymentDate = "2024-09-25", status = PaymentStatus.PAID),
        SalaryRecord("sal_3", "usr_teacher_1", "Ustadh Abdurrahman Mahmoud", "Head of Quranic Studies", "October 2024", 1200.0, 150.0, 0.0, paymentDate = "2024-10-25", status = PaymentStatus.PAID),
        SalaryRecord("sal_4", "usr_teacher_2", "Ustadha Mariam Hassan", "Arabic & Seerah Educator", "October 2024", 1200.0, 100.0, 0.0, paymentDate = "2024-10-25", status = PaymentStatus.PAID),
        SalaryRecord("sal_5", "usr_teacher_3", "Ustadh Bilal Omar", "Fiqh Educator", "October 2024", 1150.0, 50.0, 0.0, paymentDate = null, status = PaymentStatus.PENDING)
    )

    private val _salaryRecords = MutableStateFlow(initialSalaries)
    val salaryRecords: StateFlow<List<SalaryRecord>> = _salaryRecords.asStateFlow()

    // --- Chat Messages ---
    private val initialChats = listOf(
        ChatMessage("msg_1", "usr_admin_1", "Sheikh Ibrahim Al-Hassan", UserRole.ADMIN, "usr_teacher_1", "Ustadh Abdurrahman Mahmoud", UserRole.TEACHER, "Assalamu Alaikum Ustadh, please submit the Quran memorization progress log for Grade 8.", "2024-10-24 09:15 AM", true, DeliveryStatus.SEEN),
        ChatMessage("msg_2", "usr_teacher_1", "Ustadh Abdurrahman Mahmoud", UserRole.TEACHER, "usr_admin_1", "Sheikh Ibrahim Al-Hassan", UserRole.ADMIN, "Wa Alaikum Assalam Sheikh Ibrahim. The report card data has been updated in the system.", "2024-10-24 09:30 AM", true, DeliveryStatus.SEEN),
        ChatMessage("msg_3", "usr_parent_1", "Ali Ahmed", UserRole.PARENT, "usr_teacher_1", "Ustadh Abdurrahman Mahmoud", UserRole.TEACHER, "Assalamu Alaikum Ustadh. How is Ahmed performing in Tajweed this semester?", "2024-10-24 02:00 PM", true, DeliveryStatus.SEEN),
        ChatMessage("msg_4", "usr_teacher_1", "Ustadh Abdurrahman Mahmoud", UserRole.TEACHER, "usr_parent_1", "Ali Ahmed", UserRole.PARENT, "Wa Alaikum Assalam Brother Ali. MashaAllah Ahmed is top of his class in Tajweed!", "2024-10-24 02:15 PM", false, DeliveryStatus.DELIVERED),
        ChatMessage("msg_5", "usr_teacher_1", "Ustadh Abdurrahman Mahmoud", UserRole.TEACHER, "usr_student_1", "Ahmed Ali", UserRole.STUDENT, "Assalamu Alaikum Ahmed, remember to review Surah Al-Kahf verses 1-30 today.", "2024-10-24 04:00 PM", false, DeliveryStatus.SENT),
        ChatMessage("msg_6", "usr_admin_1", "Sheikh Ibrahim Al-Hassan", UserRole.ADMIN, "all", "All Academy Community", UserRole.ADMIN, "📢 ANNOUNCEMENT: Midterm Academic Assembly scheduled for next Monday at 9:00 AM.", "2024-10-25 08:00 AM", false, DeliveryStatus.SENT, isBroadcast = true)
    )

    private val _chatMessages = MutableStateFlow(initialChats)
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // --- School Bus & Transportation ---
    private val initialBusRoutes = listOf(
        BusRouteInfo(
            id = "bus_1",
            routeName = "Route A - Bole & Kazanchis Express",
            busNumber = "Bus #04",
            driverName = "Ustadh Qasim Ibrahim",
            driverPhone = "+251 91 112 2334",
            vehiclePlate = "AA-3-45678",
            morningPickupTime = "06:45 AM",
            eveningDropTime = "04:30 PM",
            currentStatus = "On Time - En Route",
            studentId = "usr_student_1"
        ),
        BusRouteInfo(
            id = "bus_2",
            routeName = "Route B - Sarbet & Jemo Shuttle",
            busNumber = "Bus #07",
            driverName = "Ustadh Bilal Yusuf",
            driverPhone = "+251 92 223 3445",
            vehiclePlate = "AA-2-89012",
            morningPickupTime = "07:00 AM",
            eveningDropTime = "04:45 PM",
            currentStatus = "Arrived at Madrasaa",
            studentId = "usr_student_3"
        ),
        BusRouteInfo(
            id = "bus_3",
            routeName = "Route C - CMC & Megenagna Line",
            busNumber = "Bus #02",
            driverName = "Ustadh Hamza Said",
            driverPhone = "+251 93 334 4556",
            vehiclePlate = "AA-4-12345",
            morningPickupTime = "06:30 AM",
            eveningDropTime = "05:00 PM",
            currentStatus = "Delayed - Traffic",
            studentId = "usr_student_2"
        )
    )

    private val _busRoutes = MutableStateFlow(initialBusRoutes)
    val busRoutes: StateFlow<List<BusRouteInfo>> = _busRoutes.asStateFlow()

    fun updateBusStatus(routeId: String, newStatus: String) {
        _busRoutes.update { list ->
            list.map { if (it.id == routeId) it.copy(currentStatus = newStatus) else it }
        }
        addNotification("Bus Status Updated", "Route status changed to $newStatus")
    }

    // --- Management & Registration Actions ---

    fun switchRole(role: UserRole) {
        _activeRole.value = role
        val defaultUser = when (role) {
            UserRole.ADMIN -> adminUser
            UserRole.TEACHER -> teacherUser
            UserRole.STUDENT -> studentUser
            UserRole.PARENT -> parentUser
        }
        _activeUser.value = defaultUser
        addNotification("Role Switched", "Switched active perspective to ${role.label}")
    }

    fun switchActiveUser(user: User) {
        _activeUser.value = user
        _activeRole.value = user.role
        addNotification("User Switched", "Active user is now ${user.name}")
    }

    fun updateProfile(userId: String, name: String, email: String, phone: String, bioOrExtra: String) {
        if (_activeUser.value.id == userId) {
            _activeUser.update { it.copy(name = name, email = email, phone = phone) }
        }
        _students.update { list ->
            list.map { if (it.userId == userId) it.copy(name = name) else it }
        }
        _teachers.update { list ->
            list.map { if (it.userId == userId) it.copy(name = name, qualification = if (bioOrExtra.isNotBlank()) bioOrExtra else it.qualification) else it }
        }
        _staff.update { list ->
            list.map { if (it.userId == userId) it.copy(name = name, roleTitle = if (bioOrExtra.isNotBlank()) bioOrExtra else it.roleTitle) else it }
        }
        schoolDao?.let { dao ->
            repoScope.launch {
                dao.getUserById(userId)?.let { user ->
                    dao.updateUser(user.copy(name = name, email = email, phone = phone))
                }
            }
        }
        addNotification("Profile Updated", "Account details for $name were updated successfully.")
    }

    fun registerStudent(
        name: String,
        email: String,
        phone: String,
        gradeLevel: String,
        gender: String,
        dateOfBirth: String,
        parentName: String
    ) {
        val userId = "usr_student_" + UUID.randomUUID().toString().take(6)
        val studentIdNumber = "MII-2024-" + (100..999).random()
        val newUser = User(
            id = userId,
            name = name,
            email = email,
            role = UserRole.STUDENT,
            phone = phone
        )
        val newProfile = StudentProfile(
            id = "std_prof_" + UUID.randomUUID().toString().take(6),
            userId = userId,
            studentIdNumber = studentIdNumber,
            name = name,
            gradeLevel = gradeLevel,
            parentId = "usr_parent_1",
            parentName = parentName,
            dateOfBirth = dateOfBirth,
            enrollmentDate = "2024-10-25",
            gender = gender
        )
        _students.update { it + newProfile }
        schoolDao?.let { dao ->
            repoScope.launch {
                dao.insertUser(UserEntity.fromDomainModel(newUser))
                dao.insertStudentProfile(StudentProfileEntity.fromDomainModel(newProfile))
            }
        }
        addNotification("New Student Admission", "$name ($studentIdNumber) admitted to $gradeLevel.")
    }

    fun registerStaff(
        name: String,
        email: String,
        phone: String,
        roleTitle: String,
        department: String,
        employeeId: String,
        isTeacher: Boolean,
        qualification: String = "",
        specialization: String = ""
    ) {
        val userId = "usr_staff_" + UUID.randomUUID().toString().take(6)
        val role = if (isTeacher) UserRole.TEACHER else UserRole.ADMIN
        val newUser = User(
            id = userId,
            name = name,
            email = email,
            role = role,
            phone = phone
        )
        var newTeacher: TeacherProfile? = null
        if (isTeacher) {
            newTeacher = TeacherProfile(
                id = "tch_prof_" + UUID.randomUUID().toString().take(6),
                userId = userId,
                employeeId = employeeId,
                name = name,
                qualification = qualification.ifBlank { "B.A. Islamic Studies & Shariah" },
                specialization = specialization.ifBlank { "Quran & Islamic Sciences" },
                assignedClasses = listOf("Grade 8 - Tahfeedh"),
                joinDate = "2024-10-25"
            )
            _teachers.update { it + newTeacher }
        }
        val newStaff = StaffProfile(
            id = "stf_prof_" + UUID.randomUUID().toString().take(6),
            userId = userId,
            employeeId = employeeId,
            name = name,
            roleTitle = roleTitle,
            department = department,
            joinDate = "2024-10-25"
        )
        _staff.update { it + newStaff }
        schoolDao?.let { dao ->
            repoScope.launch {
                dao.insertUser(UserEntity.fromDomainModel(newUser))
                newTeacher?.let { dao.insertTeacherProfile(TeacherProfileEntity.fromDomainModel(it)) }
                dao.insertStaffProfile(StaffProfileEntity.fromDomainModel(newStaff))
            }
        }
        addNotification("New Staff Onboarding", "$name onboarded as $roleTitle ($department).")
    }

    fun enrollStudentInCourse(studentId: String, courseId: String, semesterId: String): Boolean {
        val existing = _enrollments.value.find { it.studentId == studentId && it.courseId == courseId && it.semesterId == semesterId }
        if (existing != null) return false

        val newEnrollment = Enrollment(
            id = UUID.randomUUID().toString(),
            studentId = studentId,
            courseId = courseId,
            semesterId = semesterId,
            enrollmentDate = "2024-10-25"
        )
        _enrollments.update { it + newEnrollment }

        schoolDao?.let { dao ->
            repoScope.launch {
                dao.insertEnrollment(EnrollmentEntity.fromDomainModel(newEnrollment))
            }
        }

        val course = _courses.value.find { it.id == courseId }
        val student = _students.value.find { it.id == studentId || it.userId == studentId }
        if (course != null && student != null) {
            _reportCards.update { cards ->
                val cardIndex = cards.indexOfFirst { it.studentId == studentId }
                if (cardIndex != -1) {
                    val existingCard = cards[cardIndex]
                    val hasCourse = existingCard.results.any { it.score.subjectId == courseId }
                    if (!hasCourse) {
                        val newResult = SubjectResult(
                            courseCode = course.code,
                            courseTitle = course.title,
                            score = Score(
                                subjectId = course.id,
                                subjectName = course.title,
                                teacherName = course.teacherName,
                                assignmentsMarks = 15.0,
                                midtermMarks = 25.0,
                                finalExamMarks = 45.0
                            )
                        )
                        val updatedResults = existingCard.results + newResult
                        val updatedCard = existingCard.copy(results = updatedResults)
                        cards.toMutableList().apply { set(cardIndex, updatedCard) }
                    } else {
                        cards
                    }
                } else {
                    val newCard = ReportCard(
                        id = UUID.randomUUID().toString(),
                        studentId = studentId,
                        studentName = student.name,
                        gradeLevel = student.gradeLevel,
                        semesterName = "Semester 1, 2024/2025",
                        results = listOf(
                            SubjectResult(
                                courseCode = course.code,
                                courseTitle = course.title,
                                score = Score(
                                    subjectId = course.id,
                                    subjectName = course.title,
                                    teacherName = course.teacherName,
                                    assignmentsMarks = 15.0,
                                    midtermMarks = 25.0,
                                    finalExamMarks = 45.0
                                )
                            )
                        ),
                        teacherComments = "Newly enrolled in semester course.",
                        issueDate = "2024-10-25"
                    )
                    cards + newCard
                }
            }
        }
        return true
    }

    fun updateOrSubmitScore(
        studentId: String,
        courseId: String,
        assignmentsMarks: Double,
        midtermMarks: Double,
        finalExamMarks: Double,
        comments: String = ""
    ) {
        val course = _courses.value.find { it.id == courseId } ?: return
        val student = _students.value.find { it.userId == studentId || it.id == studentId } ?: return

        _reportCards.update { cards ->
            val existingCardIndex = cards.indexOfFirst { it.studentId == studentId }
            val newScore = Score(
                subjectId = course.id,
                subjectName = course.title,
                teacherName = course.teacherName,
                assignmentsMarks = assignmentsMarks,
                midtermMarks = midtermMarks,
                finalExamMarks = finalExamMarks
            )
            val newSubjectResult = SubjectResult(
                courseCode = course.code,
                courseTitle = course.title,
                score = newScore
            )

            val scoreEntity = ScoreEntity.fromDomainModel(
                scoreId = "scr_" + studentId + "_" + courseId,
                studentId = studentId,
                courseId = courseId,
                score = newScore
            )
            schoolDao?.let { dao ->
                repoScope.launch {
                    dao.insertScore(scoreEntity)
                }
            }

            if (existingCardIndex != -1) {
                val existingCard = cards[existingCardIndex]
                val existingResultIndex = existingCard.results.indexOfFirst { it.score.subjectId == courseId || it.courseCode == course.code }
                val updatedResults = if (existingResultIndex != -1) {
                    existingCard.results.toMutableList().apply { set(existingResultIndex, newSubjectResult) }
                } else {
                    existingCard.results + newSubjectResult
                }
                val updatedComments = if (comments.isNotBlank()) comments else existingCard.teacherComments
                val updatedCard = existingCard.copy(results = updatedResults, teacherComments = updatedComments)
                cards.toMutableList().apply { set(existingCardIndex, updatedCard) }
            } else {
                val newCard = ReportCard(
                    id = UUID.randomUUID().toString(),
                    studentId = studentId,
                    studentName = student.name,
                    gradeLevel = student.gradeLevel,
                    semesterName = "Semester 1, 2024/2025",
                    results = listOf(newSubjectResult),
                    teacherComments = if (comments.isNotBlank()) comments else "Satisfactory progress.",
                    issueDate = "2024-10-25"
                )
                cards + newCard
            }
        }
        addNotification("Grade Updated", "Scores updated for ${student.name} in ${course.title}.")
    }

    fun addCourse(
        code: String,
        title: String,
        description: String,
        teacherId: String,
        teacherName: String,
        gradeLevel: String,
        creditHours: Int = 3
    ) {
        val newCourse = Course(
            id = "crs_" + UUID.randomUUID().toString().take(6),
            code = code,
            title = title,
            description = description,
            teacherId = teacherId,
            teacherName = teacherName,
            gradeLevel = gradeLevel,
            creditHours = creditHours
        )
        _courses.update { it + newCourse }
        schoolDao?.let { dao ->
            repoScope.launch {
                dao.insertCourse(CourseEntity.fromDomainModel(newCourse))
            }
        }
        addNotification("Course Added", "New course $title ($code) added.")
    }

    fun addSemester(name: String, startDate: String, endDate: String, isCurrent: Boolean = false) {
        val newSemester = Semester(
            id = "sem_" + UUID.randomUUID().toString().take(6),
            name = name,
            startDate = startDate,
            endDate = endDate,
            isCurrent = isCurrent
        )
        _semesters.update { it + newSemester }
        schoolDao?.let { dao ->
            repoScope.launch {
                dao.insertSemester(SemesterEntity.fromDomainModel(newSemester))
            }
        }
    }

    fun addAttendanceRecord(studentId: String, studentName: String, courseId: String, courseName: String, status: AttendanceStatus, notes: String) {
        val newRecord = AttendanceRecord(
            id = UUID.randomUUID().toString(),
            studentId = studentId,
            studentName = studentName,
            courseId = courseId,
            courseName = courseName,
            date = "2024-10-25",
            status = status,
            notes = notes
        )
        _attendanceRecords.update { listOf(newRecord) + it }
        schoolDao?.let { dao ->
            repoScope.launch {
                dao.insertAttendance(AttendanceEntity.fromDomainModel(newRecord))
            }
        }
    }

    fun addStaffAttendanceRecord(
        staffId: String,
        staffName: String,
        date: String = "2024-10-25",
        status: AttendanceStatus,
        checkInTime: String = "07:45 AM",
        checkOutTime: String = "03:30 PM",
        notes: String = ""
    ) {
        val newRecord = StaffAttendanceRecord(
            id = UUID.randomUUID().toString(),
            staffId = staffId,
            staffName = staffName,
            date = date,
            status = status,
            checkInTime = checkInTime,
            checkOutTime = checkOutTime,
            notes = notes
        )
        _staffAttendanceRecords.update { listOf(newRecord) + it }
        schoolDao?.let { dao ->
            repoScope.launch {
                dao.insertStaffAttendance(StaffAttendanceEntity.fromDomainModel(newRecord))
            }
        }
    }

    fun sendChatMessage(receiverId: String, receiverName: String, receiverRole: UserRole, text: String) {
        val sender = _activeUser.value
        val newMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = sender.id,
            senderName = sender.name,
            senderRole = sender.role,
            receiverId = receiverId,
            receiverName = receiverName,
            receiverRole = receiverRole,
            text = text,
            timestamp = "Just Now",
            isRead = false,
            deliveryStatus = DeliveryStatus.SENT
        )
        _chatMessages.update { it + newMessage }
        schoolDao?.let { dao ->
            repoScope.launch {
                dao.insertChatMessage(ChatMessageEntity.fromDomainModel(newMessage))
            }
        }
        addNotification("New Message: ${sender.name}", text)
    }

    fun updateMessageStatus(messageId: String, status: DeliveryStatus) {
        _chatMessages.update { list ->
            list.map { msg ->
                if (msg.id == messageId) {
                    val updated = msg.copy(deliveryStatus = status, isRead = (status == DeliveryStatus.SEEN))
                    schoolDao?.let { dao ->
                        repoScope.launch {
                            dao.insertChatMessage(ChatMessageEntity.fromDomainModel(updated))
                        }
                    }
                    updated
                } else msg
            }
        }
    }

    fun toggleMessageStatus(messageId: String) {
        _chatMessages.update { list ->
            list.map { msg ->
                if (msg.id == messageId) {
                    val nextStatus = when (msg.deliveryStatus) {
                        DeliveryStatus.SENT -> DeliveryStatus.DELIVERED
                        DeliveryStatus.DELIVERED -> DeliveryStatus.SEEN
                        DeliveryStatus.SEEN -> DeliveryStatus.SENT
                    }
                    val updated = msg.copy(deliveryStatus = nextStatus, isRead = (nextStatus == DeliveryStatus.SEEN))
                    schoolDao?.let { dao ->
                        repoScope.launch {
                            dao.insertChatMessage(ChatMessageEntity.fromDomainModel(updated))
                        }
                    }
                    updated
                } else msg
            }
        }
    }

    fun simulateInterRoleReply(fromUser: User, toUser: User, text: String) {
        val newMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = fromUser.id,
            senderName = fromUser.name,
            senderRole = fromUser.role,
            receiverId = toUser.id,
            receiverName = toUser.name,
            receiverRole = toUser.role,
            text = text,
            timestamp = "Just Now",
            isRead = false,
            deliveryStatus = DeliveryStatus.DELIVERED
        )
        _chatMessages.update { it + newMessage }
        schoolDao?.let { dao ->
            repoScope.launch {
                dao.insertChatMessage(ChatMessageEntity.fromDomainModel(newMessage))
            }
        }
        addNotification("Message from ${fromUser.name}", text)
    }

    fun broadcastAnnouncement(title: String, text: String, targetRole: UserRole? = null) {
        val sender = _activeUser.value
        val broadcastMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = sender.id,
            senderName = sender.name,
            senderRole = sender.role,
            receiverId = "all",
            receiverName = targetRole?.label ?: "All Community Members",
            receiverRole = targetRole ?: UserRole.STUDENT,
            text = "📢 ANNOUNCEMENT: $title\n$text",
            timestamp = "Just Now",
            isRead = false,
            isBroadcast = true
        )
        _chatMessages.update { listOf(broadcastMsg) + it }
        schoolDao?.let { dao ->
            repoScope.launch {
                dao.insertChatMessage(ChatMessageEntity.fromDomainModel(broadcastMsg))
            }
        }
        addNotification("📢 Announcement: $title", text)
    }

    fun recordSalaryPayout(
        staffId: String,
        staffName: String,
        roleTitle: String,
        monthYear: String,
        baseSalary: Double,
        bonus: Double,
        deductions: Double,
        status: PaymentStatus = PaymentStatus.PAID,
        paymentDate: String? = "2024-10-25"
    ) {
        val net = baseSalary + bonus - deductions
        val newRecord = SalaryRecord(
            id = UUID.randomUUID().toString(),
            staffId = staffId,
            staffName = staffName,
            roleTitle = roleTitle,
            monthYear = monthYear,
            baseSalary = baseSalary,
            bonus = bonus,
            deductions = deductions,
            netSalary = net,
            paymentDate = if (status == PaymentStatus.PAID) (paymentDate ?: "2024-10-25") else null,
            status = status
        )
        _salaryRecords.update { listOf(newRecord) + it }
        schoolDao?.let { dao ->
            repoScope.launch {
                dao.insertSalary(SalaryEntity.fromDomainModel(newRecord))
            }
        }
        addNotification("Payroll Processed", "Salary payout recorded for $staffName ($monthYear).")
    }

    fun addPayment(studentId: String, studentName: String, feeType: FeeType, amount: Double) {
        recordFeePayment(studentId, studentName, feeType, amount, PaymentStatus.PAID, "2024-11-01", "2024-10-25")
    }

    fun recordFeePayment(
        studentId: String,
        studentName: String,
        feeType: FeeType,
        amount: Double,
        status: PaymentStatus = PaymentStatus.PAID,
        dueDate: String = "2024-11-01",
        paymentDate: String? = "2024-10-25"
    ) {
        val newPayment = PaymentRecord(
            id = UUID.randomUUID().toString(),
            studentId = studentId,
            studentName = studentName,
            feeType = feeType,
            amount = amount,
            dueDate = dueDate,
            paymentDate = if (status == PaymentStatus.PAID) (paymentDate ?: "2024-10-25") else null,
            status = status,
            transactionRef = if (status == PaymentStatus.PAID) "TXN-" + (10000..99999).random() else null
        )
        _paymentRecords.update { listOf(newPayment) + it }
        schoolDao?.let { dao ->
            repoScope.launch {
                dao.insertPayment(PaymentEntity.fromDomainModel(newPayment))
            }
        }
        addNotification("Fee Payment Recorded", "$feeType payment received for $studentName.")
    }

    // --- Analytics & Calculation Helpers ---

    fun getTotalFeeRevenueCollected(): Double {
        return _paymentRecords.value
            .filter { it.status == PaymentStatus.PAID }
            .sumOf { it.amount }
    }

    fun getTotalPendingFees(): Double {
        return _paymentRecords.value
            .filter { it.status != PaymentStatus.PAID }
            .sumOf { it.amount }
    }

    fun getTotalSalariesPaid(): Double {
        return _salaryRecords.value
            .filter { it.status == PaymentStatus.PAID }
            .sumOf { it.netSalary }
    }

    fun getPendingSalaries(): Double {
        return _salaryRecords.value
            .filter { it.status != PaymentStatus.PAID }
            .sumOf { it.netSalary }
    }

    fun getStudentFeeBalance(studentId: String): Double {
        return _paymentRecords.value
            .filter { it.studentId == studentId && it.status != PaymentStatus.PAID }
            .sumOf { it.amount }
    }

    fun getStudentAttendancePercentage(studentId: String): Double {
        val records = _attendanceRecords.value.filter { it.studentId == studentId }
        if (records.isEmpty()) return 100.0
        val attendedCount = records.count { it.status == AttendanceStatus.PRESENT || it.status == AttendanceStatus.EXCUSED || it.status == AttendanceStatus.LATE }
        return (attendedCount.toDouble() / records.size) * 100.0
    }

    fun getStaffAttendancePercentage(staffId: String): Double {
        val records = _staffAttendanceRecords.value.filter { it.staffId == staffId }
        if (records.isEmpty()) return 100.0
        val attendedCount = records.count { it.status == AttendanceStatus.PRESENT || it.status == AttendanceStatus.EXCUSED || it.status == AttendanceStatus.LATE }
        return (attendedCount.toDouble() / records.size) * 100.0
    }

    fun getMonthlyFinancialBreakdown(): List<MonthlyFinanceStat> {
        return listOf(
            MonthlyFinanceStat("September", 495.0, 2400.0),
            MonthlyFinanceStat("October", 450.0, 2500.0),
            MonthlyFinanceStat("November (Est)", 600.0, 2400.0),
            MonthlyFinanceStat("December (Est)", 650.0, 2400.0)
        )
    }

    fun getMonthlyAttendanceBreakdown(): List<MonthlyAttendanceStat> {
        return listOf(
            MonthlyAttendanceStat("September", 96.5, 3.5),
            MonthlyAttendanceStat("October", 94.8, 5.2),
            MonthlyAttendanceStat("November (Est)", 95.5, 4.5),
            MonthlyAttendanceStat("December (Est)", 97.0, 3.0)
        )
    }
}

data class MonthlyAttendanceStat(
    val month: String,
    val attendancePercentage: Double,
    val absencePercentage: Double
)
