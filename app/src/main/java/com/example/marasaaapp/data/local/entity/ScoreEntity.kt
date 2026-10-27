package com.example.marasaaapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.marasaaapp.model.Score

@Entity(tableName = "scores")
data class ScoreEntity(
    @PrimaryKey val scoreId: String,
    val studentId: String,
    val courseId: String,
    val assignmentMarks: Double,
    val midtermMarks: Double,
    val finalExamMarks: Double,
    val totalMarks: Double,
    val grade: String,
    val passStatus: Boolean,
    val subjectName: String = "",
    val teacherName: String = ""
) {
    fun toDomainModel(): Score {
        return Score(
            subjectId = courseId,
            subjectName = subjectName,
            teacherName = teacherName,
            assignmentsMarks = assignmentMarks,
            midtermMarks = midtermMarks,
            finalExamMarks = finalExamMarks
        )
    }

    companion object {
        fun fromDomainModel(scoreId: String, studentId: String, courseId: String, score: Score): ScoreEntity {
            return ScoreEntity(
                scoreId = scoreId,
                studentId = studentId,
                courseId = courseId,
                assignmentMarks = score.assignmentsMarks,
                midtermMarks = score.midtermMarks,
                finalExamMarks = score.finalExamMarks,
                totalMarks = score.totalMarks,
                grade = score.grade,
                passStatus = score.isPass,
                subjectName = score.subjectName,
                teacherName = score.teacherName
            )
        }
    }
}
