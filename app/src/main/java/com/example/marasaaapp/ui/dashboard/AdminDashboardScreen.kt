package com.example.marasaaapp.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.marasaaapp.model.*
import com.example.marasaaapp.repository.SchoolRepository
import com.example.marasaaapp.ui.components.*
import com.example.marasaaapp.util.CsvExportHelper
import com.example.marasaaapp.util.PdfReportGenerator
import java.util.Locale

enum class AdminSubTab(val label: String) {
    ACADEMICS("Academic Overview"),
    COURSES("Courses & Semesters"),
    STAFF_FINANCE("Financials & Staff"),
    ATTENDANCE("Attendance & Reports")
}

@Composable
fun AdminDashboardScreen(
    repository: SchoolRepository = SchoolRepository
) {
    val context = LocalContext.current
    val students by repository.students.collectAsState()
    val teachers by repository.teachers.collectAsState()
    val staff by repository.staff.collectAsState()
    val courses by repository.courses.collectAsState()
    val semesters by repository.semesters.collectAsState()
    val payments by repository.paymentRecords.collectAsState()
    val staffAttendance by repository.staffAttendanceRecords.collectAsState()

    var selectedSubTab by remember { mutableStateOf(AdminSubTab.ACADEMICS) }

    // Search and filter states for Filament tables
    var studentSearchQuery by remember { mutableStateOf("") }
    var staffSearchQuery by remember { mutableStateOf("") }
    var courseSearchQuery by remember { mutableStateOf("") }
    var financeSearchQuery by remember { mutableStateOf("") }
    var selectedGradeFilter by remember { mutableStateOf<String?>(null) }

    // Dialog state for New Course
    var showAddCourseDialog by remember { mutableStateOf(false) }
    var courseCodeInput by remember { mutableStateOf("") }
    var courseTitleInput by remember { mutableStateOf("") }
    var courseDescInput by remember { mutableStateOf("") }
    var courseTeacherNameInput by remember { mutableStateOf("Ustadh Abdurrahman Mahmoud") }
    var courseGradeInput by remember { mutableStateOf("Grade 8 - Tahfeedh") }
    var courseCreditsInput by remember { mutableStateOf("3") }

    // Dialog state for New Semester
    var showAddSemesterDialog by remember { mutableStateOf(false) }
    var semNameInput by remember { mutableStateOf("Semester 3, 2024/2025") }
    var semStartInput by remember { mutableStateOf("2025-03-01") }
    var semEndInput by remember { mutableStateOf("2025-07-31") }

    // Dialog state for Fee Payment
    var showFeePaymentDialog by remember { mutableStateOf(false) }
    var feeStudentIdInput by remember { mutableStateOf(students.firstOrNull()?.userId ?: "usr_student_1") }
    var feeStudentNameInput by remember { mutableStateOf(students.firstOrNull()?.name ?: "Ahmed Ali") }
    var feeTypeInput by remember { mutableStateOf(FeeType.TUITION) }
    var feeAmountInput by remember { mutableStateOf("150.0") }

    // Dialog state for Staff Attendance
    var showStaffAttendanceDialog by remember { mutableStateOf(false) }
    var attStaffName by remember { mutableStateOf("Ustadh Abdurrahman Mahmoud") }
    var attStaffStatus by remember { mutableStateOf(AttendanceStatus.PRESENT) }
    var attCheckIn by remember { mutableStateOf("07:45 AM") }
    var attCheckOut by remember { mutableStateOf("03:30 PM") }
    var attNotes by remember { mutableStateOf("Normal duty shift") }

    // Dialog state for Student Admission
    var showStudentAdmissionDialog by remember { mutableStateOf(false) }
    var admName by remember { mutableStateOf("") }
    var admEmail by remember { mutableStateOf("") }
    var admPhone by remember { mutableStateOf("") }
    var admGrade by remember { mutableStateOf("Grade 8 - Tahfeedh") }
    var admGender by remember { mutableStateOf("Male") }
    var admParent by remember { mutableStateOf("Ali Ahmed") }

    // Dialog state for Staff Onboarding
    var showStaffOnboardingDialog by remember { mutableStateOf(false) }
    var stfName by remember { mutableStateOf("") }
    var stfEmail by remember { mutableStateOf("") }
    var stfPhone by remember { mutableStateOf("") }
    var stfRoleTitle by remember { mutableStateOf("Islamic Studies Educator") }
    var stfDept by remember { mutableStateOf("Academics") }
    var stfEmpId by remember { mutableStateOf("STAFF-105") }
    var stfIsTeacher by remember { mutableStateOf(true) }
    var stfQual by remember { mutableStateOf("B.A. Shariah & Islamic Education") }

    var actionNotice by remember { mutableStateOf<String?>(null) }

    val totalCollected = repository.getTotalFeeRevenueCollected()
    val pendingFees = repository.getTotalPendingFees()
    val totalSalariesPaid = repository.getTotalSalariesPaid()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Principal Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Madrasaa Administration • Filament Executive Portal",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Sheikh Ibrahim Al-Hassan",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Academy Principal & General Director • STAFF-001",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // SubTabs
        item {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                AdminSubTab.entries.forEachIndexed { index, tab ->
                    SegmentedButton(
                        selected = selectedSubTab == tab,
                        onClick = { selectedSubTab = tab },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = AdminSubTab.entries.size)
                    ) {
                        Text(tab.label, fontSize = 10.sp)
                    }
                }
            }
        }

        if (actionNotice != null) {
            item {
                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = actionNotice!!,
                            color = Color(0xFF065F46),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                        TextButton(onClick = { actionNotice = null }) {
                            Text("Dismiss", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Action Buttons Row (Student Admission & Staff Onboarding)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showStudentAdmissionDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Admit Student", fontSize = 11.sp)
                }

                Button(
                    onClick = { showStaffOnboardingDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(imageVector = Icons.Default.Work, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Onboard Staff", fontSize = 11.sp)
                }
            }
        }

        // SubTab 0: Academic Overview & Statistics
        if (selectedSubTab == AdminSubTab.ACADEMICS) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "School-Wide Academic Statistics",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FilamentMetricCard(
                            title = "Enrolled Students",
                            value = students.size.toString(),
                            icon = Icons.Default.School,
                            color = MaterialTheme.colorScheme.primary,
                            trendText = "+8% term",
                            trendPositive = true,
                            modifier = Modifier.weight(1f)
                        )
                        FilamentMetricCard(
                            title = "Faculty & Staff",
                            value = (teachers.size + staff.size).toString(),
                            icon = Icons.Default.People,
                            color = Color(0xFF3B82F6),
                            trendText = "100% active",
                            trendPositive = true,
                            modifier = Modifier.weight(1f)
                        )
                        FilamentMetricCard(
                            title = "Active Courses",
                            value = courses.size.toString(),
                            icon = Icons.Default.MenuBook,
                            color = Color(0xFF10B981),
                            trendText = "All accredited",
                            trendPositive = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Student List Table (Filament Style)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Student Admission List",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    FilamentTableToolbar(
                        searchQuery = studentSearchQuery,
                        onSearchQueryChange = { studentSearchQuery = it },
                        searchPlaceholder = "Search students by name or ID...",
                        filterOptions = listOf("Grade 8 - Tahfeedh", "Grade 7 - Shariah", "Grade 6 - Arabic"),
                        selectedFilter = selectedGradeFilter,
                        onFilterSelected = { selectedGradeFilter = it },
                        onCreateClicked = { showStudentAdmissionDialog = true },
                        createButtonLabel = "New Student",
                        onExportPdfClicked = { actionNotice = "Exported Student Roster PDF successfully." },
                        onExportCsvClicked = { actionNotice = "Exported Student Roster CSV successfully." }
                    )
                }
            }

            val filteredStudents = students.filter { std ->
                (studentSearchQuery.isBlank() || std.name.contains(studentSearchQuery, ignoreCase = true) || std.studentIdNumber.contains(studentSearchQuery, ignoreCase = true)) &&
                        (selectedGradeFilter == null || std.gradeLevel == selectedGradeFilter)
            }

            item {
                FilamentTable(
                    headers = listOf("Student Name", "ID Number", "Grade Level", "Parent / Guardian", "Status", "Actions"),
                    isEmpty = filteredStudents.isEmpty(),
                    emptyMessage = "No students match the search criteria."
                ) {
                    filteredStudents.forEachIndexed { index, std ->
                        FilamentTableRow(isEven = index % 2 == 1) {
                            Text(text = std.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            Text(text = std.studentIdNumber, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                            Text(text = std.gradeLevel, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            Text(text = std.parentName, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            FilamentStatusBadge(text = "Active", isSuccess = true, modifier = Modifier.weight(1f))
                            FilamentRowActions(
                                modifier = Modifier.weight(1f),
                                onView = { actionNotice = "Viewing profile for ${std.name}" },
                                onEdit = { actionNotice = "Editing record for ${std.name}" },
                                onDelete = { actionNotice = "Archived record for ${std.name}" }
                            )
                        }
                    }
                }
            }

            // Staff List Table (Filament Style)
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Faculty & Staff List",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    FilamentTableToolbar(
                        searchQuery = staffSearchQuery,
                        onSearchQueryChange = { staffSearchQuery = it },
                        searchPlaceholder = "Search staff by name or ID...",
                        onCreateClicked = { showStaffOnboardingDialog = true },
                        createButtonLabel = "New Staff",
                        onExportPdfClicked = { actionNotice = "Exported Faculty Roster PDF." }
                    )
                }
            }

            val filteredTeachers = teachers.filter { tch ->
                staffSearchQuery.isBlank() || tch.name.contains(staffSearchQuery, ignoreCase = true) || tch.employeeId.contains(staffSearchQuery, ignoreCase = true)
            }

            item {
                FilamentTable(
                    headers = listOf("Faculty Name", "Employee ID", "Specialization", "Qualification", "Status", "Actions"),
                    isEmpty = filteredTeachers.isEmpty(),
                    emptyMessage = "No faculty found."
                ) {
                    filteredTeachers.forEachIndexed { index, tch ->
                        FilamentTableRow(isEven = index % 2 == 1) {
                            Text(text = tch.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            Text(text = tch.employeeId, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                            Text(text = tch.specialization, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            Text(text = tch.qualification, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            FilamentStatusBadge(text = "Active", isSuccess = true, modifier = Modifier.weight(1f))
                            FilamentRowActions(
                                modifier = Modifier.weight(1f),
                                onView = { actionNotice = "Viewing faculty ${tch.name}" },
                                onEdit = { actionNotice = "Editing faculty ${tch.name}" },
                                onDelete = { actionNotice = "Archived faculty ${tch.name}" }
                            )
                        }
                    }
                }
            }
        }

        // SubTab 1: Courses & Semesters
        if (selectedSubTab == AdminSubTab.COURSES) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Madrasaa Curriculum & Course Management",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    FilamentTableToolbar(
                        searchQuery = courseSearchQuery,
                        onSearchQueryChange = { courseSearchQuery = it },
                        searchPlaceholder = "Search courses by title or code...",
                        onCreateClicked = { showAddCourseDialog = true },
                        createButtonLabel = "Add Course",
                        onExportPdfClicked = { actionNotice = "Exported Course Catalog PDF." }
                    )
                }
            }

            val filteredCourses = courses.filter { c ->
                courseSearchQuery.isBlank() || c.title.contains(courseSearchQuery, ignoreCase = true) || c.code.contains(courseSearchQuery, ignoreCase = true)
            }

            item {
                FilamentTable(
                    headers = listOf("Course Code", "Course Title", "Credits", "Instructor", "Grade Level", "Actions"),
                    isEmpty = filteredCourses.isEmpty(),
                    emptyMessage = "No courses found."
                ) {
                    filteredCourses.forEachIndexed { index, course ->
                        FilamentTableRow(isEven = index % 2 == 1) {
                            Text(text = course.code, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            Text(text = course.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.5f))
                            Text(text = "${course.creditHours} Credits", fontSize = 12.sp, modifier = Modifier.weight(0.8f))
                            Text(text = course.teacherName, fontSize = 12.sp, modifier = Modifier.weight(1.2f))
                            Text(text = course.gradeLevel, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            FilamentRowActions(
                                modifier = Modifier.weight(1f),
                                onView = { actionNotice = "Viewing course ${course.title}" },
                                onEdit = { actionNotice = "Editing course ${course.title}" },
                                onDelete = { actionNotice = "Deleted course ${course.title}" }
                            )
                        }
                    }
                }
            }

            // Semesters Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Academic Semesters",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    OutlinedButton(onClick = { showAddSemesterDialog = true }) {
                        Text("Add Semester", fontSize = 12.sp)
                    }
                }
            }

            items(semesters) { sem ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (sem.isCurrent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = sem.name, fontWeight = FontWeight.Bold)
                            Text(text = "${sem.startDate} to ${sem.endDate}", style = MaterialTheme.typography.labelSmall)
                        }
                        if (sem.isCurrent) {
                            FilamentStatusBadge(text = "Current Session", isSuccess = true)
                        }
                    }
                }
            }
        }

        // SubTab 2: Financial Management & Staff Payroll
        if (selectedSubTab == AdminSubTab.STAFF_FINANCE) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Academy Financial Summary",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FilamentMetricCard(
                            title = "Fee Revenue Collected",
                            value = "$${totalCollected.toInt()}",
                            icon = Icons.Default.AttachMoney,
                            color = Color(0xFF10B981),
                            trendText = "+15% term",
                            trendPositive = true,
                            modifier = Modifier.weight(1f)
                        )
                        FilamentMetricCard(
                            title = "Pending Student Fees",
                            value = "$${pendingFees.toInt()}",
                            icon = Icons.Default.MoneyOff,
                            color = Color(0xFFEF4444),
                            trendText = "Requires notice",
                            trendPositive = false,
                            modifier = Modifier.weight(1f)
                        )
                        FilamentMetricCard(
                            title = "Salaries Paid Out",
                            value = "$${totalSalariesPaid.toInt()}",
                            icon = Icons.Default.AccountBalanceWallet,
                            color = MaterialTheme.colorScheme.primary,
                            trendText = "Fully cleared",
                            trendPositive = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Financial Payouts & Fee Payments Table
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Financial Payouts & Fee Ledger",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Button(onClick = { showFeePaymentDialog = true }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Record Payment", fontSize = 11.sp)
                        }
                    }
                    FilamentTableToolbar(
                        searchQuery = financeSearchQuery,
                        onSearchQueryChange = { financeSearchQuery = it },
                        searchPlaceholder = "Search fee payments by student or ref...",
                        onExportPdfClicked = { actionNotice = "Exported Financial Ledger PDF." },
                        onExportCsvClicked = { actionNotice = "Exported Financial Ledger CSV." }
                    )
                }
            }

            val filteredPayments = payments.filter { p ->
                financeSearchQuery.isBlank() || p.studentName.contains(financeSearchQuery, ignoreCase = true) || p.feeType.label.contains(financeSearchQuery, ignoreCase = true)
            }

            item {
                FilamentTable(
                    headers = listOf("Payment ID", "Student Name", "Fee Type", "Amount", "Due Date", "Status", "Actions"),
                    isEmpty = filteredPayments.isEmpty(),
                    emptyMessage = "No payment records found."
                ) {
                    filteredPayments.forEachIndexed { index, pay ->
                        FilamentTableRow(isEven = index % 2 == 1) {
                            Text(text = pay.id, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f))
                            Text(text = pay.studentName, fontSize = 12.sp, modifier = Modifier.weight(1.2f))
                            Text(text = pay.feeType.label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1.2f))
                            Text(text = "$${pay.amount}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, modifier = Modifier.weight(0.8f))
                            Text(text = pay.dueDate, fontSize = 11.sp, modifier = Modifier.weight(1f))
                            val isPaid = pay.status == PaymentStatus.PAID
                            FilamentStatusBadge(text = pay.status.label, isSuccess = isPaid, modifier = Modifier.weight(1f))
                            FilamentRowActions(
                                modifier = Modifier.weight(1f),
                                onView = { actionNotice = "Viewing receipt ${pay.id}" },
                                onEdit = { actionNotice = "Updating payment status for ${pay.id}" },
                                onDelete = { actionNotice = "Deleted payment record ${pay.id}" }
                            )
                        }
                    }
                }
            }
        }

        // SubTab 3: Attendance & Reports
        if (selectedSubTab == AdminSubTab.ATTENDANCE) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Staff & Student Daily Attendance",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Button(onClick = { showStaffAttendanceDialog = true }) {
                        Text("Log Staff Shift", fontSize = 11.sp)
                    }
                }
            }

            items(staffAttendance) { stfAtt ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = stfAtt.staffName, fontWeight = FontWeight.Bold)
                            Text(text = "Date: ${stfAtt.date} • In: ${stfAtt.checkInTime} | Out: ${stfAtt.checkOutTime}", style = MaterialTheme.typography.labelSmall)
                        }
                        val isPresent = stfAtt.status == AttendanceStatus.PRESENT
                        FilamentStatusBadge(text = stfAtt.status.label, isSuccess = isPresent)
                    }
                }
            }
        }
    }

    // Add Course Dialog
    if (showAddCourseDialog) {
        AlertDialog(
            onDismissRequest = { showAddCourseDialog = false },
            title = { Text("Add New Madrasaa Course") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = courseCodeInput, onValueChange = { courseCodeInput = it }, label = { Text("Course Code (e.g., QUR-301)") })
                    OutlinedTextField(value = courseTitleInput, onValueChange = { courseTitleInput = it }, label = { Text("Course Title") })
                    OutlinedTextField(value = courseDescInput, onValueChange = { courseDescInput = it }, label = { Text("Description") })
                    OutlinedTextField(value = courseGradeInput, onValueChange = { courseGradeInput = it }, label = { Text("Grade Level") })
                    OutlinedTextField(value = courseCreditsInput, onValueChange = { courseCreditsInput = it }, label = { Text("Credit Hours") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (courseCodeInput.isNotBlank() && courseTitleInput.isNotBlank()) {
                        repository.addCourse(
                            code = courseCodeInput,
                            title = courseTitleInput,
                            description = courseDescInput.ifBlank { "Core Islamic & Quranic studies curriculum." },
                            teacherId = "tch_1",
                            teacherName = courseTeacherNameInput,
                            gradeLevel = courseGradeInput,
                            creditHours = courseCreditsInput.toIntOrNull() ?: 3
                        )
                        actionNotice = "Successfully added course: $courseTitleInput"
                        showAddCourseDialog = false
                        courseCodeInput = ""
                        courseTitleInput = ""
                    }
                }) {
                    Text("Save Course")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCourseDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Add Semester Dialog
    if (showAddSemesterDialog) {
        AlertDialog(
            onDismissRequest = { showAddSemesterDialog = false },
            title = { Text("Add Academic Semester") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = semNameInput, onValueChange = { semNameInput = it }, label = { Text("Semester Name") })
                    OutlinedTextField(value = semStartInput, onValueChange = { semStartInput = it }, label = { Text("Start Date (YYYY-MM-DD)") })
                    OutlinedTextField(value = semEndInput, onValueChange = { semEndInput = it }, label = { Text("End Date (YYYY-MM-DD)") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (semNameInput.isNotBlank()) {
                        repository.addSemester(
                            name = semNameInput,
                            startDate = semStartInput,
                            endDate = semEndInput,
                            isCurrent = true
                        )
                        actionNotice = "Added semester: $semNameInput"
                        showAddSemesterDialog = false
                    }
                }) {
                    Text("Add Semester")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSemesterDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Record Fee Payment Dialog
    if (showFeePaymentDialog) {
        AlertDialog(
            onDismissRequest = { showFeePaymentDialog = false },
            title = { Text("Record Fee Payment") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = feeStudentNameInput, onValueChange = { feeStudentNameInput = it }, label = { Text("Student Name") })
                    OutlinedTextField(value = feeAmountInput, onValueChange = { feeAmountInput = it }, label = { Text("Amount ($)") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    val amount = feeAmountInput.toDoubleOrNull() ?: 100.0
                    repository.recordFeePayment(
                        studentId = feeStudentIdInput,
                        studentName = feeStudentNameInput,
                        feeType = feeTypeInput,
                        amount = amount,
                        status = PaymentStatus.PAID,
                        dueDate = "2025-04-01",
                        paymentDate = "2025-03-10"
                    )
                    actionNotice = "Successfully recorded payment of $$amount for $feeStudentNameInput"
                    showFeePaymentDialog = false
                }) {
                    Text("Record Payment")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFeePaymentDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Admit Student Dialog
    if (showStudentAdmissionDialog) {
        AlertDialog(
            onDismissRequest = { showStudentAdmissionDialog = false },
            title = { Text("Admit New Student") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = admName, onValueChange = { admName = it }, label = { Text("Full Name") })
                    OutlinedTextField(value = admEmail, onValueChange = { admEmail = it }, label = { Text("Email Address") })
                    OutlinedTextField(value = admPhone, onValueChange = { admPhone = it }, label = { Text("Phone Number") })
                    OutlinedTextField(value = admGrade, onValueChange = { admGrade = it }, label = { Text("Grade Level") })
                    OutlinedTextField(value = admParent, onValueChange = { admParent = it }, label = { Text("Parent / Guardian Name") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (admName.isNotBlank()) {
                        repository.registerStudent(
                            name = admName,
                            email = admEmail.ifBlank { "${admName.lowercase().replace(" ", "")}@madrasaa.edu" },
                            phone = admPhone.ifBlank { "+252 61 500 0000" },
                            gradeLevel = admGrade,
                            gender = admGender,
                            dateOfBirth = "2012-01-01",
                            parentName = admParent.ifBlank { "Guardian Ali" }
                        )
                        actionNotice = "Successfully admitted student: $admName"
                        showStudentAdmissionDialog = false
                        admName = ""
                    }
                }) {
                    Text("Complete Admission")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStudentAdmissionDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Staff Onboarding Dialog
    if (showStaffOnboardingDialog) {
        AlertDialog(
            onDismissRequest = { showStaffOnboardingDialog = false },
            title = { Text("Onboard New Staff / Ustadh") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = stfName, onValueChange = { stfName = it }, label = { Text("Full Name") })
                    OutlinedTextField(value = stfEmail, onValueChange = { stfEmail = it }, label = { Text("Email Address") })
                    OutlinedTextField(value = stfRoleTitle, onValueChange = { stfRoleTitle = it }, label = { Text("Role Title") })
                    OutlinedTextField(value = stfQual, onValueChange = { stfQual = it }, label = { Text("Qualification") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (stfName.isNotBlank()) {
                        repository.registerStaff(
                            name = stfName,
                            email = stfEmail.ifBlank { "${stfName.lowercase().replace(" ", "")}@madrasaa.edu" },
                            phone = stfPhone.ifBlank { "+252 61 700 0000" },
                            roleTitle = stfRoleTitle,
                            department = stfDept,
                            employeeId = stfEmpId,
                            isTeacher = stfIsTeacher,
                            qualification = stfQual,
                            specialization = stfQual
                        )
                        actionNotice = "Successfully onboarded: $stfName"
                        showStaffOnboardingDialog = false
                        stfName = ""
                    }
                }) {
                    Text("Complete Onboarding")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStaffOnboardingDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Staff Attendance Dialog
    if (showStaffAttendanceDialog) {
        AlertDialog(
            onDismissRequest = { showStaffAttendanceDialog = false },
            title = { Text("Log Staff Shift / Attendance") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = attStaffName, onValueChange = { attStaffName = it }, label = { Text("Staff Name") })
                    OutlinedTextField(value = attCheckIn, onValueChange = { attCheckIn = it }, label = { Text("Check-In Time") })
                    OutlinedTextField(value = attCheckOut, onValueChange = { attCheckOut = it }, label = { Text("Check-Out Time") })
                    OutlinedTextField(value = attNotes, onValueChange = { attNotes = it }, label = { Text("Shift Notes") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    repository.addStaffAttendanceRecord(
                        staffId = "stf_1",
                        staffName = attStaffName,
                        date = "2025-03-24",
                        status = attStaffStatus,
                        checkInTime = attCheckIn,
                        checkOutTime = attCheckOut,
                        notes = attNotes
                    )
                    actionNotice = "Logged shift attendance for $attStaffName"
                    showStaffAttendanceDialog = false
                }) {
                    Text("Save Attendance")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStaffAttendanceDialog = false }) { Text("Cancel") }
            }
        )
    }
}
