package com.example.marasaaapp.model

enum class DayOfWeekName(val label: String) {
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    SUNDAY("Sunday")
}

data class TimetableItem(
    val id: String,
    val courseId: String,
    val courseTitle: String,
    val courseCode: String,
    val teacherName: String,
    val classroom: String,
    val dayOfWeek: DayOfWeekName,
    val startTime: String,
    val endTime: String,
    val targetGrade: String
)
