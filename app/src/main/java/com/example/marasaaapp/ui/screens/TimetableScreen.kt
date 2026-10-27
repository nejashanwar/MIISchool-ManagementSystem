package com.example.marasaaapp.ui.screens

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.marasaaapp.model.DayOfWeekName
import com.example.marasaaapp.model.TimetableItem
import com.example.marasaaapp.model.UserRole
import com.example.marasaaapp.repository.SchoolRepository
import com.example.marasaaapp.ui.components.*

enum class SchedulePreset(val label: String) {
    MON_TO_FRI("Monday - Friday"),
    SUN_TO_THU("Sunday - Thursday (Islamic)"),
    ALL_DAYS("All 7 Days")
}

@Composable
fun TimetableScreen(
    repository: SchoolRepository = SchoolRepository
) {
    val context = LocalContext.current
    val timetableItems by repository.timetableItems.collectAsState()
    val teachers by repository.teachers.collectAsState()
    val activeRole by repository.activeRole.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var schedulePreset by remember { mutableStateOf(SchedulePreset.MON_TO_FRI) }
    var selectedDay by remember { mutableStateOf(DayOfWeekName.MONDAY) }

    // Filter States
    var selectedGradeFilter by remember { mutableStateOf<String?>(null) }
    var selectedTeacherFilter by remember { mutableStateOf<String?>(null) }

    // Dialog States
    var showAddDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<TimetableItem?>(null) }
    var viewingItem by remember { mutableStateOf<TimetableItem?>(null) }
    var deletingItem by remember { mutableStateOf<TimetableItem?>(null) }

    var actionNotice by remember { mutableStateOf<String?>(null) }

    val isAdminOrStaff = activeRole == UserRole.ADMIN || activeRole == UserRole.TEACHER

    val daysToShow = when (schedulePreset) {
        SchedulePreset.MON_TO_FRI -> listOf(
            DayOfWeekName.MONDAY,
            DayOfWeekName.TUESDAY,
            DayOfWeekName.WEDNESDAY,
            DayOfWeekName.THURSDAY,
            DayOfWeekName.FRIDAY
        )
        SchedulePreset.SUN_TO_THU -> listOf(
            DayOfWeekName.SUNDAY,
            DayOfWeekName.MONDAY,
            DayOfWeekName.TUESDAY,
            DayOfWeekName.WEDNESDAY,
            DayOfWeekName.THURSDAY
        )
        SchedulePreset.ALL_DAYS -> DayOfWeekName.entries
    }

    LaunchedEffect(schedulePreset) {
        if (selectedDay !in daysToShow) {
            selectedDay = daysToShow.first()
        }
    }

    LaunchedEffect(actionNotice) {
        actionNotice?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            actionNotice = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Timetable & Schedule Management",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Laravel Filament-inspired enterprise schedule administration",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isAdminOrStaff) {
                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Schedule Item", fontSize = 12.sp)
                }
            }
        }

        // Schedule Preset Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SchedulePreset.entries.forEach { preset ->
                FilterChip(
                    selected = schedulePreset == preset,
                    onClick = { schedulePreset = preset },
                    label = { Text(preset.label, fontSize = 11.sp) }
                )
            }
        }

        // Day Selector Tabs
        ScrollableTabRow(
            selectedTabIndex = daysToShow.indexOf(selectedDay).coerceAtLeast(0),
            edgePadding = 0.dp,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ) {
            daysToShow.forEach { day ->
                Tab(
                    selected = day == selectedDay,
                    onClick = { selectedDay = day },
                    text = { Text(day.label, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }
        }

        // Filament Table Toolbar
        val gradeOptions = listOf("Grade 8 - Tahfeedh", "Grade 10 - Shariah")
        val teacherOptions = teachers.map { it.name }

        FilamentTableToolbar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            searchPlaceholder = "Search by course, teacher, or room...",
            filterOptions = gradeOptions + teacherOptions,
            selectedFilter = selectedGradeFilter ?: selectedTeacherFilter,
            onFilterSelected = { filter ->
                if (filter == null) {
                    selectedGradeFilter = null
                    selectedTeacherFilter = null
                } else if (gradeOptions.contains(filter)) {
                    selectedGradeFilter = if (selectedGradeFilter == filter) null else filter
                } else if (teacherOptions.contains(filter)) {
                    selectedTeacherFilter = if (selectedTeacherFilter == filter) null else filter
                }
            },
            onCreateClicked = if (isAdminOrStaff) { { showAddDialog = true } } else null,
            createButtonLabel = "New Schedule",
            onExportPdfClicked = {
                actionNotice = "Generated Timetable PDF Report successfully."
            },
            onExportCsvClicked = {
                actionNotice = "Exported Timetable CSV successfully."
            },
            onPrintClicked = {
                actionNotice = "Sending timetable to printer..."
            }
        )

        // Filter Timetable Items
        val filteredItems = timetableItems.filter { item ->
            val matchesDay = item.dayOfWeek == selectedDay
            val matchesSearch = searchQuery.isBlank() ||
                    item.courseTitle.contains(searchQuery, ignoreCase = true) ||
                    item.courseCode.contains(searchQuery, ignoreCase = true) ||
                    item.teacherName.contains(searchQuery, ignoreCase = true) ||
                    item.classroom.contains(searchQuery, ignoreCase = true)
            val matchesGrade = selectedGradeFilter == null || item.targetGrade == selectedGradeFilter
            val matchesTeacher = selectedTeacherFilter == null || item.teacherName == selectedTeacherFilter

            matchesDay && matchesSearch && matchesGrade && matchesTeacher
        }

        // Filament Table Data View
        FilamentTable(
            headers = listOf("Time Slot", "Course & Code", "Grade Level", "Room", "Educator", "Status", "Actions"),
            columnWidths = listOf(110.dp, 160.dp, 130.dp, 90.dp, 150.dp, 90.dp, 110.dp),
            isEmpty = filteredItems.isEmpty(),
            emptyMessage = "No scheduled classes found for ${selectedDay.label} matching your criteria.",
            modifier = Modifier.weight(1f)
        ) {
            filteredItems.forEachIndexed { index, item ->
                FilamentTableRow(isEven = index % 2 == 1) {
                    // Time Slot
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                        modifier = Modifier.width(110.dp)
                    ) {
                        Text(
                            text = "${item.startTime}\n${item.endTime}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            maxLines = 2
                        )
                    }

                    // Course Title & Code
                    Column(modifier = Modifier.width(160.dp)) {
                        Text(
                            text = item.courseTitle,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1
                        )
                        Text(
                            text = item.courseCode,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Grade Level
                    Text(
                        text = item.targetGrade,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.width(130.dp),
                        maxLines = 1
                    )

                    // Room
                    Text(
                        text = item.classroom,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier.width(90.dp),
                        maxLines = 1
                    )

                    // Educator
                    Text(
                        text = item.teacherName,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.width(150.dp),
                        maxLines = 1
                    )

                    // Status Pill
                    Box(modifier = Modifier.width(90.dp)) {
                        FilamentStatusBadge(text = "Scheduled", isSuccess = true)
                    }

                    // Row Actions
                    Box(modifier = Modifier.width(110.dp)) {
                        FilamentRowActions(
                            onView = { viewingItem = item },
                            onEdit = if (isAdminOrStaff) { { editingItem = item } } else null,
                            onDelete = if (isAdminOrStaff) { { deletingItem = item } } else null
                        )
                    }
                }
            }
        }
    }

    // --- ADD / EDIT DIALOG ---
    if (showAddDialog || editingItem != null) {
        val currentEdit = editingItem
        var courseTitle by remember { mutableStateOf(currentEdit?.courseTitle ?: "") }
        var courseCode by remember { mutableStateOf(currentEdit?.courseCode ?: "QRN-101") }
        var teacherName by remember { mutableStateOf(currentEdit?.teacherName ?: teachers.firstOrNull()?.name ?: "Ustadh Abdurrahman Mahmoud") }
        var classroom by remember { mutableStateOf(currentEdit?.classroom ?: "Hall 10A") }
        var dayOfWeek by remember { mutableStateOf(currentEdit?.dayOfWeek ?: selectedDay) }
        var startTime by remember { mutableStateOf(currentEdit?.startTime ?: "08:00 AM") }
        var endTime by remember { mutableStateOf(currentEdit?.endTime ?: "09:30 AM") }
        var targetGrade by remember { mutableStateOf(currentEdit?.targetGrade ?: "Grade 8 - Tahfeedh") }

        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                editingItem = null
            },
            title = { Text(if (currentEdit == null) "Add New Schedule Item" else "Edit Schedule Item") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = courseTitle,
                        onValueChange = { courseTitle = it },
                        label = { Text("Course Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = courseCode,
                        onValueChange = { courseCode = it },
                        label = { Text("Course Code (e.g. QRN-101)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = teacherName,
                        onValueChange = { teacherName = it },
                        label = { Text("Assigned Teacher") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = classroom,
                        onValueChange = { classroom = it },
                        label = { Text("Room / Venue") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = targetGrade,
                        onValueChange = { targetGrade = it },
                        label = { Text("Target Grade Level") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { startTime = it },
                            label = { Text("Start Time") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = { endTime = it },
                            label = { Text("End Time") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (courseTitle.isNotBlank()) {
                            if (currentEdit == null) {
                                repository.addTimetableItem(
                                    courseId = "crs_${System.currentTimeMillis()}",
                                    courseTitle = courseTitle,
                                    courseCode = courseCode,
                                    teacherName = teacherName,
                                    classroom = classroom,
                                    dayOfWeek = dayOfWeek,
                                    startTime = startTime,
                                    endTime = endTime,
                                    targetGrade = targetGrade
                                )
                                actionNotice = "Added new schedule item successfully."
                            } else {
                                repository.updateTimetableItem(
                                    id = currentEdit.id,
                                    courseTitle = courseTitle,
                                    courseCode = courseCode,
                                    teacherName = teacherName,
                                    classroom = classroom,
                                    dayOfWeek = dayOfWeek,
                                    startTime = startTime,
                                    endTime = endTime,
                                    targetGrade = targetGrade
                                )
                                actionNotice = "Updated schedule item successfully."
                            }
                            showAddDialog = false
                            editingItem = null
                        } else {
                            actionNotice = "Please enter a course title."
                        }
                    }
                ) {
                    Text(if (currentEdit == null) "Create" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    editingItem = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- VIEW DETAIL DIALOG ---
    viewingItem?.let { item ->
        AlertDialog(
            onDismissRequest = { viewingItem = null },
            title = { Text("Schedule Details: ${item.courseCode}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Course: ${item.courseTitle} (${item.courseCode})", fontWeight = FontWeight.Bold)
                    Text("Day: ${item.dayOfWeek.label}")
                    Text("Time Slot: ${item.startTime} - ${item.endTime}")
                    Text("Class/Grade: ${item.targetGrade}")
                    Text("Room / Venue: ${item.classroom}")
                    Text("Educator: ${item.teacherName}")
                }
            },
            confirmButton = {
                Button(onClick = { viewingItem = null }) {
                    Text("Close")
                }
            }
        )
    }

    // --- DELETE CONFIRMATION DIALOG ---
    deletingItem?.let { item ->
        AlertDialog(
            onDismissRequest = { deletingItem = null },
            title = { Text("Delete Schedule Item") },
            text = { Text("Are you sure you want to delete ${item.courseTitle} (${item.courseCode}) on ${item.dayOfWeek.label}?") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    onClick = {
                        repository.deleteTimetableItem(item.id)
                        actionNotice = "Deleted schedule item ${item.courseCode}"
                        deletingItem = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingItem = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimetableScreenPreview() {
    MaterialTheme {
        TimetableScreen()
    }
}
