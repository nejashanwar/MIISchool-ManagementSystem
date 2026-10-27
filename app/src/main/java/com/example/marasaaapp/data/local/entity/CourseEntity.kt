package com.example.marasaaapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.marasaaapp.model.Course

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val courseId: String,
    val title: String,
    val code: String,
    val teacherId: String,
    val semester: String,
    val description: String = "",
    val gradeLevel: String = "",
    val creditHours: Int = 3,
    val teacherName: String = ""
) {
    fun toDomainModel(): Course {
        return Course(
            id = courseId,
            code = code,
            title = title,
            description = description,
            teacherId = teacherId,
            teacherName = teacherName,
            gradeLevel = gradeLevel,
            creditHours = creditHours
        )
    }

    companion object {
        fun fromDomainModel(course: Course, semester: String = "Semester 1, 2024/2025"): CourseEntity {
            return CourseEntity(
                courseId = course.id,
                title = course.title,
                code = course.code,
                teacherId = course.teacherId,
                semester = semester,
                description = course.description,
                gradeLevel = course.gradeLevel,
                creditHours = course.creditHours,
                teacherName = course.teacherName
            )
        }
    }
}
