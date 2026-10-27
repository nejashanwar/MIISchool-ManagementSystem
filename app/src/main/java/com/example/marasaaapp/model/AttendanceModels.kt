package com.example.marasaaapp.model

enum class AttendanceStatus(val label: String) {
    PRESENT("Present"),
    ABSENT("Absent"),
    LATE("Late"),
    EXCUSED("Excused")
}

data class AttendanceRecord(
    val id: String,
    val studentId: String,
    val studentName: String,
    val courseId: String,
    val courseName: String,
    val date: String,
    val status: AttendanceStatus,
    val notes: String = ""
)

data class StaffAttendanceRecord(
    val id: String,
    val staffId: String,
    val staffName: String,
    val date: String,
    val status: AttendanceStatus,
    val checkInTime: String,
    val checkOutTime: String,
    val notes: String = ""
)
