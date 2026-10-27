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
import com.example.marasaaapp.model.AttendanceStatus
import com.example.marasaaapp.model.Score
import com.example.marasaaapp.repository.SchoolRepository
import com.example.marasaaapp.ui.components.*
import com.example.marasaaapp.util.CsvExportHelper
import com.example.marasaaapp.util.PdfReportGenerator
import java.util.Locale

enum class TeacherSubTab(val label: String) {
    GRADING("Grading & Scores"),
    ATTENDANCE("Class Attendance"),
    LIVE_REPORTS("Student Report Cards")
}

@Composable
fun TeacherDashboardScreen(
    repository: SchoolRepository = SchoolRepository
) {
    val context = LocalContext.current
    val activeUser by repository.activeUser.collectAsState()
    val courses by repository.courses.collectAsState()
    val students by repository.students.collectAsState()
    val attendanceRecords by repository.attendanceRecords.collectAsState()
    val staffAttendanceRecords by repository.staffAttendanceRecords.collectAsState()
    val reportCards by repository.reportCards.collectAsState()

    var selectedSubTab by remember { mutableStateOf(TeacherSubTab.GRADING) }

    // Search query states for Filament tables
    var scoreSearchQuery by remember { mutableStateOf("") }
    var attendanceSearchQuery by remember { mutableStateOf("") }

    // Grading Form State
    var selectedCourseId by remember { mutableStateOf(courses.firstOrNull()?.id ?: "crs_101") }
    var selectedStudentId by remember { mutableStateOf(students.firstOrNull()?.userId ?: "") }

    var assignmentsInput by remember { mutableStateOf("16.0") }
    var midtermInput by remember { mutableStateOf("24.0") }
    var finalExamInput by remember { mutableStateOf("52.0") }
    var commentsInput by remember { mutableStateOf("MashaAllah, great progress and active participation in class.") }

    var gradingSuccessMessage by remember { mutableStateOf<String?>(null) }

    // Attendance Form State
    var attStudentId by remember { mutableStateOf(students.firstOrNull()?.userId ?: "") }
    var attCourseId by remember { mutableStateOf(courses.firstOrNull()?.id ?: "crs_101") }
    var attStatus by remember { mutableStateOf(AttendanceStatus.PRESENT) }
    var attNote by remember { mutableStateOf("") }
    var attSuccessMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Teacher Profile Header
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
                        text = "Ustadh Portal • Filament Educator Dashboard",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Text(
                        text = activeUser.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Specialization: Quran Recitation & Tajweed • STAFF-101",
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
                TeacherSubTab.entries.forEachIndexed { index, tab ->
                    SegmentedButton(
                        selected = selectedSubTab == tab,
                        onClick = { selectedSubTab = tab },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = TeacherSubTab.entries.size)
                    ) {
                        Text(tab.label, fontSize = 11.sp)
                    }
                }
            }
        }

        // SubTab 0: Score Collection & Grading Table
        if (selectedSubTab == TeacherSubTab.GRADING) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Score Entry & Centralized Grading",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Icon(
                                imageVector = Icons.Default.Grade,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Select Course / Subject
                        Text(
                            text = "1. Select Course / Subject",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Column {
                            courses.forEach { course ->
                                FilterChip(
                                    selected = course.id == selectedCourseId,
                                    onClick = { selectedCourseId = course.id },
                                    label = { Text("${course.code} - ${course.title}") },
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Select Student
                        Text(
                            text = "2. Select Student",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            students.forEach { std ->
                                FilterChip(
                                    selected = std.userId == selectedStudentId,
                                    onClick = { selectedStudentId = std.userId },
                                    label = { Text(std.name) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "3. Input Marks Breakdown",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = assignmentsInput,
                                onValueChange = { assignmentsInput = it },
                                label = { Text("Assignments (20)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = midtermInput,
                                onValueChange = { midtermInput = it },
                                label = { Text("Midterm (30)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = finalExamInput,
                                onValueChange = { finalExamInput = it },
                                label = { Text("Final (50)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = commentsInput,
                            onValueChange = { commentsInput = it },
                            label = { Text("Teacher Comments / Feedback") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Calculated Preview Box
                        val assignVal = assignmentsInput.toDoubleOrNull() ?: 0.0
                        val midVal = midtermInput.toDoubleOrNull() ?: 0.0
                        val finalVal = finalExamInput.toDoubleOrNull() ?: 0.0
                        val previewScore = Score("preview", "Preview", "Teacher", assignVal, midVal, finalVal)

                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Live Calculation Preview:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Total Marks: ${previewScore.totalMarks} / 100",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    FilamentStatusBadge(
                                        text = "${previewScore.grade} (${if (previewScore.isPass) "PASS" else "FAIL"})",
                                        isSuccess = previewScore.isPass
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val selectedStd = students.firstOrNull { it.userId == selectedStudentId }
                                val selectedCrs = courses.firstOrNull { it.id == selectedCourseId }
                                if (selectedStd != null && selectedCrs != null) {
                                    repository.updateOrSubmitScore(
                                        studentId = selectedStd.userId,
                                        courseId = selectedCrs.id,
                                        assignmentsMarks = assignVal,
                                        midtermMarks = midVal,
                                        finalExamMarks = finalVal,
                                        comments = commentsInput
                                    )
                                    gradingSuccessMessage = "✓ Scores submitted dynamically for ${selectedStd.name} in ${selectedCrs.code}!"
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Submit & Centralize Score")
                        }

                        if (gradingSuccessMessage != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = Color(0xFF10B981).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = gradingSuccessMessage!!,
                                    color = Color(0xFF065F46),
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Student Score Entry Table (Filament Style)
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Student Score Entry Table",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    FilamentTableToolbar(
                        searchQuery = scoreSearchQuery,
                        onSearchQueryChange = { scoreSearchQuery = it },
                        searchPlaceholder = "Search students in score register...",
                        onExportPdfClicked = { PdfReportGenerator.printAttendanceReport(context, attendanceRecords, staffAttendanceRecords) }
                    )
                }
            }

            val filteredReportCards = reportCards.filter { rc ->
                scoreSearchQuery.isBlank() || rc.studentName.contains(scoreSearchQuery, ignoreCase = true)
            }

            item {
                FilamentTable(
                    headers = listOf("Student Name", "Grade Level", "GPA", "Average %", "Status", "Actions"),
                    isEmpty = filteredReportCards.isEmpty(),
                    emptyMessage = "No student scores found."
                ) {
                    filteredReportCards.forEachIndexed { index, rc ->
                        FilamentTableRow(isEven = index % 2 == 1) {
                            Text(text = rc.studentName, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1.2f))
                            Text(text = rc.gradeLevel, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            Text(text = String.format(Locale.US, "%.2f", rc.gpa), fontWeight = FontWeight.SemiBold, fontSize = 12.sp, modifier = Modifier.weight(0.8f))
                            Text(text = "${String.format(Locale.US, "%.1f", rc.averageScore)}%", fontSize = 12.sp, modifier = Modifier.weight(0.8f))
                            FilamentStatusBadge(text = if (rc.isPassed) "Passed" else "At Risk", isSuccess = rc.isPassed, modifier = Modifier.weight(1f))
                            FilamentRowActions(
                                modifier = Modifier.weight(1f),
                                onView = { PdfReportGenerator.printReportCard(context, rc) },
                                onEdit = { gradingSuccessMessage = "Selected ${rc.studentName} for grade editing." }
                            )
                        }
                    }
                }
            }
        }

        // SubTab 1: Class Attendance Marking & Attendance Register Table
        if (selectedSubTab == TeacherSubTab.ATTENDANCE) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Mark Student Class Attendance",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Select Course",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            courses.take(3).forEach { crs ->
                                FilterChip(
                                    selected = crs.id == attCourseId,
                                    onClick = { attCourseId = crs.id },
                                    label = { Text(crs.code) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Select Student",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            students.forEach { std ->
                                FilterChip(
                                    selected = std.userId == attStudentId,
                                    onClick = { attStudentId = std.userId },
                                    label = { Text(std.name) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Attendance Status",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AttendanceStatus.entries.forEach { status ->
                                FilterChip(
                                    selected = status == attStatus,
                                    onClick = { attStatus = status },
                                    label = { Text(status.label) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = attNote,
                            onValueChange = { attNote = it },
                            label = { Text("Notes / Comments (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val std = students.firstOrNull { it.userId == attStudentId }
                                val crs = courses.firstOrNull { it.id == attCourseId }
                                if (std != null && crs != null) {
                                    repository.addAttendanceRecord(
                                        studentId = std.userId,
                                        studentName = std.name,
                                        courseId = crs.id,
                                        courseName = crs.title,
                                        status = attStatus,
                                        notes = attNote
                                    )
                                    attSuccessMessage = "Attendance recorded for ${std.name} in ${crs.code}!"
                                    attNote = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Record Attendance")
                        }

                        if (attSuccessMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "✓ $attSuccessMessage",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Attendance Register Table (Filament Style)
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Attendance Register Table",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    FilamentTableToolbar(
                        searchQuery = attendanceSearchQuery,
                        onSearchQueryChange = { attendanceSearchQuery = it },
                        searchPlaceholder = "Search attendance records by student or course...",
                        onExportPdfClicked = { PdfReportGenerator.printAttendanceReport(context, attendanceRecords, staffAttendanceRecords) },
                        onExportCsvClicked = { CsvExportHelper.exportAttendanceCsv(context, attendanceRecords, staffAttendanceRecords) }
                    )
                }
            }

            val filteredAttendance = attendanceRecords.filter { att ->
                attendanceSearchQuery.isBlank() || att.studentName.contains(attendanceSearchQuery, ignoreCase = true) || att.courseName.contains(attendanceSearchQuery, ignoreCase = true)
            }

            item {
                FilamentTable(
                    headers = listOf("Student Name", "Course", "Date", "Status", "Notes", "Actions"),
                    isEmpty = filteredAttendance.isEmpty(),
                    emptyMessage = "No attendance records found."
                ) {
                    filteredAttendance.forEachIndexed { index, att ->
                        FilamentTableRow(isEven = index % 2 == 1) {
                            Text(text = att.studentName, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.2f))
                            Text(text = att.courseName, fontSize = 12.sp, modifier = Modifier.weight(1.2f))
                            Text(text = att.date, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.9f))
                            val isPresent = att.status == AttendanceStatus.PRESENT
                            FilamentStatusBadge(text = att.status.label, isSuccess = isPresent, modifier = Modifier.weight(1f))
                            Text(text = att.notes.ifBlank { "None" }, fontSize = 11.sp, modifier = Modifier.weight(1f))
                            FilamentRowActions(
                                modifier = Modifier.weight(1f),
                                onView = { attSuccessMessage = "Viewing attendance for ${att.studentName}" },
                                onDelete = { attSuccessMessage = "Deleted attendance log for ${att.studentName}" }
                            )
                        }
                    }
                }
            }
        }

        // SubTab 2: Live Centralized Report Cards
        if (selectedSubTab == TeacherSubTab.LIVE_REPORTS) {
            item {
                Text(
                    text = "Centralized Student Report Cards",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            items(reportCards) { card ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = card.studentName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${card.gradeLevel} • GPA: ${String.format(Locale.US, "%.2f", card.gpa)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            FilamentStatusBadge(text = if (card.isPassed) "PASSED" else "FAIL", isSuccess = card.isPassed)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        card.results.forEach { res ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${res.courseCode}: ${res.courseTitle}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "${res.score.totalMarks.toInt()} (${res.score.grade})",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
