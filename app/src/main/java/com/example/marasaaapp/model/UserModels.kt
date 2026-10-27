package com.example.marasaaapp.model

enum class UserRole(val label: String) {
    ADMIN("Admin / Principal"),
    TEACHER("Ustadh / Teacher"),
    STUDENT("Talib / Student"),
    PARENT("Parent / Guardian")
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val avatarUrl: String? = null,
    val phone: String = "",
    val isOnline: Boolean = true
)

data class StudentProfile(
    val id: String,
    val userId: String,
    val studentIdNumber: String,
    val name: String,
    val gradeLevel: String,
    val parentId: String,
    val parentName: String,
    val dateOfBirth: String,
    val enrollmentDate: String,
    val gender: String
)

data class TeacherProfile(
    val id: String,
    val userId: String,
    val employeeId: String,
    val name: String,
    val qualification: String,
    val specialization: String,
    val assignedClasses: List<String>,
    val joinDate: String
)

data class StaffProfile(
    val id: String,
    val userId: String,
    val employeeId: String,
    val name: String,
    val roleTitle: String,
    val department: String,
    val joinDate: String
)
