package com.example.marasaaapp.model

data class Course(
    val id: String,
    val code: String,
    val title: String,
    val description: String,
    val teacherId: String,
    val teacherName: String,
    val gradeLevel: String,
    val creditHours: Int = 3
)

data class Semester(
    val id: String,
    val name: String,
    val startDate: String,
    val endDate: String,
    val isCurrent: Boolean = true
)

data class Enrollment(
    val id: String,
    val studentId: String,
    val courseId: String,
    val semesterId: String,
    val enrollmentDate: String
)

data class Score(
    val subjectId: String,
    val subjectName: String,
    val teacherName: String,
    val assignmentsMarks: Double = 15.0,
    val midtermMarks: Double = 25.0,
    val finalExamMarks: Double = 50.0
) {
    val continuousAssessmentMarks: Double
        get() = assignmentsMarks + midtermMarks

    val totalMarks: Double
        get() = continuousAssessmentMarks + finalExamMarks

    // Constructor for backward compatibility if 5 parameters (subjectId, subjectName, teacherName, continuousAssessmentMarks, finalExamMarks) are supplied
    constructor(
        subjectId: String,
        subjectName: String,
        teacherName: String,
        continuousAssessmentMarks: Double,
        finalExamMarks: Double
    ) : this(
        subjectId = subjectId,
        subjectName = subjectName,
        teacherName = teacherName,
        assignmentsMarks = continuousAssessmentMarks * 0.4,
        midtermMarks = continuousAssessmentMarks * 0.6,
        finalExamMarks = finalExamMarks
    )

    val grade: String
        get() = when {
            totalMarks >= 90 -> "A+"
            totalMarks >= 85 -> "A"
            totalMarks >= 80 -> "A-"
            totalMarks >= 75 -> "B+"
            totalMarks >= 70 -> "B"
            totalMarks >= 65 -> "C+"
            totalMarks >= 60 -> "C"
            totalMarks >= 50 -> "D"
            else -> "F"
        }

    val isPass: Boolean
        get() = totalMarks >= 60.0

    val gradePoint: Double
        get() = when {
            totalMarks >= 90 -> 4.0
            totalMarks >= 85 -> 3.75
            totalMarks >= 80 -> 3.5
            totalMarks >= 75 -> 3.25
            totalMarks >= 70 -> 3.0
            totalMarks >= 65 -> 2.5
            totalMarks >= 60 -> 2.0
            totalMarks >= 50 -> 1.0
            else -> 0.0
        }
}

data class SubjectResult(
    val courseCode: String,
    val courseTitle: String,
    val score: Score
)

data class ReportCard(
    val id: String,
    val studentId: String,
    val studentName: String,
    val gradeLevel: String,
    val semesterName: String,
    val results: List<SubjectResult>,
    val teacherComments: String,
    val issueDate: String
) {
    val overallTotal: Double
        get() = results.sumOf { it.score.totalMarks }

    val averageScore: Double
        get() = if (results.isNotEmpty()) overallTotal / results.size else 0.0

    val gpa: Double
        get() = if (results.isNotEmpty()) results.sumOf { it.score.gradePoint } / results.size else 0.0

    val isPassed: Boolean
        get() = results.isNotEmpty() && results.all { it.score.isPass }
}
