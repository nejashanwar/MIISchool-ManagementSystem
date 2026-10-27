package com.example.marasaaapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.marasaaapp.model.AttendanceRecord
import com.example.marasaaapp.model.AttendanceStatus

@Entity(tableName = "attendance_records")
data class AttendanceEntity(
    @PrimaryKey val attendanceId: String,
    val studentId: String,
    val courseId: String,
    val date: String,
    val status: AttendanceStatus,
    val studentName: String = "",
    val courseName: String = "",
    val notes: String = ""
) {
    fun toDomainModel(): AttendanceRecord {
        return AttendanceRecord(
            id = attendanceId,
            studentId = studentId,
            studentName = studentName,
            courseId = courseId,
            courseName = courseName,
            date = date,
            status = status,
            notes = notes
        )
    }

    companion object {
        fun fromDomainModel(record: AttendanceRecord): AttendanceEntity {
            return AttendanceEntity(
                attendanceId = record.id,
                studentId = record.studentId,
                courseId = record.courseId,
                date = record.date,
                status = record.status,
                studentName = record.studentName,
                courseName = record.courseName,
                notes = record.notes
            )
        }
    }
}
