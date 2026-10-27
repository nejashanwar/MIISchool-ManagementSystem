package com.example.marasaaapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.marasaaapp.model.*

@Entity(tableName = "student_profiles")
data class StudentProfileEntity(
    @PrimaryKey val studentProfileId: String,
    val userId: String,
    val studentIdNumber: String,
    val name: String,
    val gradeLevel: String,
    val parentId: String,
    val parentName: String,
    val dateOfBirth: String,
    val enrollmentDate: String,
    val gender: String
) {
    fun toDomainModel(): StudentProfile = StudentProfile(
        id = studentProfileId,
        userId = userId,
        studentIdNumber = studentIdNumber,
        name = name,
        gradeLevel = gradeLevel,
        parentId = parentId,
        parentName = parentName,
        dateOfBirth = dateOfBirth,
        enrollmentDate = enrollmentDate,
        gender = gender
    )

    companion object {
        fun fromDomainModel(p: StudentProfile): StudentProfileEntity = StudentProfileEntity(
            studentProfileId = p.id,
            userId = p.userId,
            studentIdNumber = p.studentIdNumber,
            name = p.name,
            gradeLevel = p.gradeLevel,
            parentId = p.parentId,
            parentName = p.parentName,
            dateOfBirth = p.dateOfBirth,
            enrollmentDate = p.enrollmentDate,
            gender = p.gender
        )
    }
}

@Entity(tableName = "teacher_profiles")
data class TeacherProfileEntity(
    @PrimaryKey val teacherProfileId: String,
    val userId: String,
    val employeeId: String,
    val name: String,
    val qualification: String,
    val specialization: String,
    val joinDate: String
) {
    fun toDomainModel(): TeacherProfile = TeacherProfile(
        id = teacherProfileId,
        userId = userId,
        employeeId = employeeId,
        name = name,
        qualification = qualification,
        specialization = specialization,
        assignedClasses = emptyList(),
        joinDate = joinDate
    )

    companion object {
        fun fromDomainModel(p: TeacherProfile): TeacherProfileEntity = TeacherProfileEntity(
            teacherProfileId = p.id,
            userId = p.userId,
            employeeId = p.employeeId,
            name = p.name,
            qualification = p.qualification,
            specialization = p.specialization,
            joinDate = p.joinDate
        )
    }
}

@Entity(tableName = "staff_profiles")
data class StaffProfileEntity(
    @PrimaryKey val staffProfileId: String,
    val userId: String,
    val employeeId: String,
    val name: String,
    val roleTitle: String,
    val department: String,
    val joinDate: String
) {
    fun toDomainModel(): StaffProfile = StaffProfile(
        id = staffProfileId,
        userId = userId,
        employeeId = employeeId,
        name = name,
        roleTitle = roleTitle,
        department = department,
        joinDate = joinDate
    )

    companion object {
        fun fromDomainModel(p: StaffProfile): StaffProfileEntity = StaffProfileEntity(
            staffProfileId = p.id,
            userId = p.userId,
            employeeId = p.employeeId,
            name = p.name,
            roleTitle = p.roleTitle,
            department = p.department,
            joinDate = p.joinDate
        )
    }
}

@Entity(tableName = "semesters")
data class SemesterEntity(
    @PrimaryKey val semesterId: String,
    val name: String,
    val startDate: String,
    val endDate: String,
    val isCurrent: Boolean
) {
    fun toDomainModel(): Semester = Semester(
        id = semesterId,
        name = name,
        startDate = startDate,
        endDate = endDate,
        isCurrent = isCurrent
    )

    companion object {
        fun fromDomainModel(s: Semester): SemesterEntity = SemesterEntity(
            semesterId = s.id,
            name = s.name,
            startDate = s.startDate,
            endDate = s.endDate,
            isCurrent = s.isCurrent
        )
    }
}

@Entity(tableName = "enrollments")
data class EnrollmentEntity(
    @PrimaryKey val enrollmentId: String,
    val studentId: String,
    val courseId: String,
    val semesterId: String,
    val enrollmentDate: String
) {
    fun toDomainModel(): Enrollment = Enrollment(
        id = enrollmentId,
        studentId = studentId,
        courseId = courseId,
        semesterId = semesterId,
        enrollmentDate = enrollmentDate
    )

    companion object {
        fun fromDomainModel(e: Enrollment): EnrollmentEntity = EnrollmentEntity(
            enrollmentId = e.id,
            studentId = e.studentId,
            courseId = e.courseId,
            semesterId = e.semesterId,
            enrollmentDate = e.enrollmentDate
        )
    }
}

@Entity(tableName = "staff_attendance_records")
data class StaffAttendanceEntity(
    @PrimaryKey val staffAttendanceId: String,
    val staffId: String,
    val staffName: String,
    val date: String,
    val status: AttendanceStatus,
    val checkInTime: String,
    val checkOutTime: String,
    val notes: String
) {
    fun toDomainModel(): StaffAttendanceRecord = StaffAttendanceRecord(
        id = staffAttendanceId,
        staffId = staffId,
        staffName = staffName,
        date = date,
        status = status,
        checkInTime = checkInTime,
        checkOutTime = checkOutTime,
        notes = notes
    )

    companion object {
        fun fromDomainModel(r: StaffAttendanceRecord): StaffAttendanceEntity = StaffAttendanceEntity(
            staffAttendanceId = r.id,
            staffId = r.staffId,
            staffName = r.staffName,
            date = r.date,
            status = r.status,
            checkInTime = r.checkInTime,
            checkOutTime = r.checkOutTime,
            notes = r.notes
        )
    }
}
