package com.example.marasaaapp.ui.dashboard

import android.widget.Toast
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
import com.example.marasaaapp.model.PaymentStatus
import com.example.marasaaapp.repository.SchoolRepository
import com.example.marasaaapp.ui.components.*
import com.example.marasaaapp.util.CsvExportHelper
import com.example.marasaaapp.util.PdfReportGenerator

enum class StudentSubTab(val label: String) {
    RESULT_CARD("Result Card"),
    SEMESTER_REGISTRATION("Semester Registration"),
    SCHEDULE_FEES("Schedule & Fees")
}

@Composable
fun StudentDashboardScreen(
    repository: SchoolRepository = SchoolRepository
) {
    val context = LocalContext.current
    val activeUser by repository.activeUser.collectAsState()
    val reportCards by repository.reportCards.collectAsState()
    val attendanceRecords by repository.attendanceRecords.collectAsState()
    val payments by repository.paymentRecords.collectAsState()
    val enrollments by repository.enrollments.collectAsState()
    val courses by repository.courses.collectAsState()
    val semesters by repository.semesters.collectAsState()

    var selectedSubTab by remember { mutableStateOf(StudentSubTab.RESULT_CARD) }
    val currentSemester = semesters.firstOrNull { it.isCurrent } ?: semesters.firstOrNull()

    val reportCard = reportCards.firstOrNull { it.studentId == activeUser.id } ?: reportCards.firstOrNull()
    val myAttendance = attendanceRecords.filter { it.studentId == activeUser.id }
    val myPayments = payments.filter { it.studentId == activeUser.id }
    val myEnrollments = enrollments.filter { it.studentId == activeUser.id }

    var actionFeedback by remember { mutableStateOf<String?>(null) }
    var enrollmentSearchQuery by remember { mutableStateOf("") }
    var resultSearchQuery by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Student Header Banner
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "Talib / Student Portal • Filament View",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Text(
                                text = activeUser.name,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "ID: MII-2024-001 • Grade 8 - Tahfeedh",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }

        // Navigation SubTabs
        item {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                StudentSubTab.entries.forEachIndexed { index, tab ->
                    SegmentedButton(
                        selected = selectedSubTab == tab,
                        onClick = { selectedSubTab = tab },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = StudentSubTab.entries.size)
                    ) {
                        Text(tab.label, fontSize = 12.sp)
                    }
                }
            }
        }

        if (actionFeedback != null) {
            item {
                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = actionFeedback!!,
                        color = Color(0xFF065F46),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // SubTab 0: Result Card & Subject Result Card Table
        if (selectedSubTab == StudentSubTab.RESULT_CARD) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Academic Result Card & GPA Summary",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FilamentMetricCard(
                            title = "Cumulative GPA",
                            value = String.format("%.2f / 4.0", reportCard?.gpa ?: 3.8),
                            icon = Icons.Default.Grade,
                            color = MaterialTheme.colorScheme.primary,
                            trendText = "Dean's List",
                            trendPositive = true,
                            modifier = Modifier.weight(1f)
                        )
                        FilamentMetricCard(
                            title = "Average Score",
                            value = String.format("%.1f%%", reportCard?.averageScore ?: 91.5),
                            icon = Icons.Default.TrendingUp,
                            color = Color(0xFF10B981),
                            trendText = "Distinction",
                            trendPositive = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Subject Result Card Table (Filament Style)
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Subject Result Card Table",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    FilamentTableToolbar(
                        searchQuery = resultSearchQuery,
                        onSearchQueryChange = { resultSearchQuery = it },
                        searchPlaceholder = "Search subjects...",
                        onExportPdfClicked = {
                            reportCard?.let { PdfReportGenerator.printReportCard(context, it) }
                        },
                        onExportCsvClicked = {
                            reportCard?.let {
                                val file = CsvExportHelper.exportAndShareCsv(context, it)
                                if (file != null) actionFeedback = "Exported CSV to ${file.name}"
                            }
                        }
                    )
                }
            }

            val filteredResults = reportCard?.results?.filter { res ->
                resultSearchQuery.isBlank() || res.courseTitle.contains(resultSearchQuery, ignoreCase = true) || res.courseCode.contains(resultSearchQuery, ignoreCase = true)
            } ?: emptyList()

            item {
                FilamentTable(
                    headers = listOf("Course Code & Title", "Assignments", "Midterm", "Final Exam", "Total", "Grade", "Status"),
                    isEmpty = filteredResults.isEmpty(),
                    emptyMessage = "No subject results found."
                ) {
                    filteredResults.forEachIndexed { index, res ->
                        FilamentTableRow(isEven = index % 2 == 1) {
                            Text(text = "${res.courseCode}: ${res.courseTitle}", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.5f))
                            Text(text = "${res.score.assignmentsMarks}", fontSize = 12.sp, modifier = Modifier.weight(0.8f))
                            Text(text = "${res.score.midtermMarks}", fontSize = 12.sp, modifier = Modifier.weight(0.8f))
                            Text(text = "${res.score.finalExamMarks}", fontSize = 12.sp, modifier = Modifier.weight(0.8f))
                            Text(text = "${res.score.totalMarks.toInt()}", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(0.8f))
                            Text(text = res.score.grade, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(0.6f))
                            FilamentStatusBadge(text = if (res.score.isPass) "Pass" else "Fail", isSuccess = res.score.isPass, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // SubTab 1: Semester Registration & Course Enrollment Table
        if (selectedSubTab == StudentSubTab.SEMESTER_REGISTRATION) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Course Enrollment & Semester Registration",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    FilamentTableToolbar(
                        searchQuery = enrollmentSearchQuery,
                        onSearchQueryChange = { enrollmentSearchQuery = it },
                        searchPlaceholder = "Search enrolled courses...",
                        onCreateClicked = { actionFeedback = "Course registration request submitted to Registrar." },
                        createButtonLabel = "Register Course"
                    )
                }
            }

            val enrolledCourses = courses.filter { crs ->
                enrollmentSearchQuery.isBlank() || crs.title.contains(enrollmentSearchQuery, ignoreCase = true) || crs.code.contains(enrollmentSearchQuery, ignoreCase = true)
            }

            item {
                FilamentTable(
                    headers = listOf("Course Code", "Course Title", "Credits", "Instructor", "Status", "Actions"),
                    isEmpty = enrolledCourses.isEmpty(),
                    emptyMessage = "No enrolled courses found."
                ) {
                    enrolledCourses.forEachIndexed { index, crs ->
                        FilamentTableRow(isEven = index % 2 == 1) {
                            Text(text = crs.code, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            Text(text = crs.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.weight(1.5f))
                            Text(text = "${crs.creditHours} Credits", fontSize = 12.sp, modifier = Modifier.weight(0.8f))
                            Text(text = crs.teacherName, fontSize = 12.sp, modifier = Modifier.weight(1.2f))
                            FilamentStatusBadge(text = "Enrolled", isSuccess = true, modifier = Modifier.weight(1f))
                            FilamentRowActions(
                                modifier = Modifier.weight(1f),
                                onView = { actionFeedback = "Viewing syllabus for ${crs.title}" }
                            )
                        }
                    }
                }
            }
        }

        // SubTab 2: Schedule & Fee Statement Table
        if (selectedSubTab == StudentSubTab.SCHEDULE_FEES) {
            item {
                Text(
                    text = "My Fee Statement & Ledger",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            item {
                FilamentTable(
                    headers = listOf("Payment ID", "Fee Type", "Amount", "Due Date", "Status", "Actions"),
                    isEmpty = myPayments.isEmpty(),
                    emptyMessage = "No payment records found."
                ) {
                    myPayments.forEachIndexed { index, pay ->
                        FilamentTableRow(isEven = index % 2 == 1) {
                            Text(text = pay.id, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f))
                            Text(text = pay.feeType.label, fontSize = 12.sp, modifier = Modifier.weight(1.2f))
                            Text(text = "$${pay.amount}", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(0.8f))
                            Text(text = pay.dueDate, fontSize = 11.sp, modifier = Modifier.weight(1f))
                            val isPaid = pay.status == PaymentStatus.PAID
                            FilamentStatusBadge(text = pay.status.label, isSuccess = isPaid, modifier = Modifier.weight(1f))
                            FilamentRowActions(
                                modifier = Modifier.weight(1f),
                                onView = { actionFeedback = "Viewing receipt for ${pay.id}" }
                            )
                        }
                    }
                }
            }
        }
    }
}
